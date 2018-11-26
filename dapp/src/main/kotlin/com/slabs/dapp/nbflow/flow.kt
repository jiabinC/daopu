package com.slabs.nbflow

import co.paralleluniverse.fibers.Suspendable
import com.slabs.nbflow.trace.MyResolveTransactionsFlow
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.crypto.isFulfilledBy
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.StatesToRecord
import net.corda.core.node.services.vault.Builder
import net.corda.core.node.services.vault.CriteriaExpression
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.schemas.PersistentState
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap

internal object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction based on State.")
internal object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints.")
internal object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")
internal object GATHERING_SIGS : ProgressTracker.Step("Gathering the counterparty's signature.") {
    override fun childProgressTracker() = CollectSignaturesFlow.tracker()
}

internal object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
    override fun childProgressTracker() = FinalityFlow.tracker()
}

fun FlowLogic.Companion.tracker() = ProgressTracker(
        GENERATING_TRANSACTION,
        VERIFYING_TRANSACTION,
        SIGNING_TRANSACTION,
        GATHERING_SIGS,
        FINALISING_TRANSACTION
)

abstract class FlowLogicX<T> : FlowLogic<T>() {

    override val progressTracker = tracker()

    // 封装TransactionBuilder
    fun transactionBuilder(notary: Party): TransactionBuilder {
        progressTracker?.currentStep = GENERATING_TRANSACTION
        return TransactionBuilder(notary)
    }

    // Verify that the transaction is valid.
    fun TransactionBuilder.verify(): TransactionBuilder {
        progressTracker?.currentStep = VERIFYING_TRANSACTION
        verify(serviceHub)
        // 代指包含它的最内层作用域的对象;
        return this // txBuilder
    }

    // Sign the transaction.
    // li: Verifies this transaction and runs contract code
    fun TransactionBuilder.sign(): SignedTransaction {
        progressTracker?.currentStep = SIGNING_TRANSACTION
        return serviceHub.signInitialTransaction(this)
    }

    // Send the state to the counterparty, and receive it back with their signature.
    @Suspendable
    inline fun SignedTransaction.collectSignatures(party: Party) = collectSignatures(setOf(initiateFlow(party)))

    @Suspendable
    fun SignedTransaction.collectSignatures(parties: Set<FlowSession>): SignedTransaction {
        progressTracker?.currentStep = GATHERING_SIGS
        return subFlow(CollectSignaturesFlow(this, parties, GATHERING_SIGS.childProgressTracker()))
    }

    // Notarise and record the transaction in both parties' vaults.
    @Suspendable
    fun SignedTransaction.finality(): SignedTransaction {
        progressTracker?.currentStep = FINALISING_TRANSACTION
        return subFlow(FinalityFlow(this, FINALISING_TRANSACTION.childProgressTracker()))
    }

    @Suspendable
    fun notariseAndRecord(transaction: SignedTransaction): SignedTransaction {
        val notarised = if (needsNotarySignature(transaction)) {
            val notarySignatures = subFlow(NotaryFlow.Client(transaction))
            transaction + notarySignatures
        } else {
            transaction
        }
        serviceHub.recordTransactions(notarised)
        return notarised
    }

    fun needsNotarySignature(stx: SignedTransaction): Boolean {
        val wtx = stx.tx
        val needsNotarisation = wtx.inputs.isNotEmpty() || wtx.timeWindow != null
        return needsNotarisation && hasNoNotarySignature(stx)
    }

    private fun hasNoNotarySignature(stx: SignedTransaction): Boolean {
        val notaryKey = stx.tx.notary?.owningKey
        val signers = stx.sigs.map { it.by }.toSet()
        return notaryKey?.isFulfilledBy(signers) != true
    }


}

// 默认notary
val FlowLogic<*>.defaultNotary: Party get() = serviceHub.networkMapCache.notaryIdentities[0]

inline fun FlowLogic<*>.getNotary(name: CordaX500Name) = serviceHub.networkMapCache.getNotary(name)
        ?: throw IllegalArgumentException("$name notary no found")

val FlowLogic<*>.myParty: Party get() = serviceHub.myInfo.legalIdentities.first()

// state查询支持
inline fun queryCriteria(init: Builder.() -> CriteriaExpression<out PersistentState, Boolean>) = QueryCriteria.VaultCustomQueryCriteria(builder(init))

inline infix fun QueryCriteria.and(init: Builder.() -> CriteriaExpression<out PersistentState, Boolean>) = this.and(queryCriteria(init))

inline infix fun QueryCriteria.or(init: Builder.() -> CriteriaExpression<out PersistentState, Boolean>) = this.or(queryCriteria(init))

inline fun <reified T : ContractState> FlowLogic<*>.queryBy(criteria: QueryCriteria): List<StateAndRef<T>> {
    return serviceHub.vaultService.queryBy<T>(T::class.java, criteria).states
}

inline fun <reified T : ContractState> FlowLogic<*>.queryBy(init: Builder.() -> CriteriaExpression<out PersistentState, Boolean>): List<StateAndRef<T>> {
    return queryBy(QueryCriteria.VaultCustomQueryCriteria(builder(init)))
}

operator inline fun String.invoke(init: () -> Boolean) {
    if (!init()) throw IllegalArgumentException("Failed requirement: $this")


}

@Suspendable
fun <R> FlowLogic<R>.signTransaction(otherPartyFlow: FlowSession, checkTransaction: SignedTransaction.() -> Unit): SignedTransaction {
    return subFlow(object : SignTransactionFlow(otherPartyFlow) {
        override fun checkTransaction(stx: SignedTransaction) = stx.checkTransaction()
    })
}

@InitiatingFlow
class ReportToRegulatorFlow(private val regulator: List<Party>, private val finalTx: SignedTransaction) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // val session = initiateFlow(regulator)
        // subFlow(SendTransactionFlow(session, finalTx))
        for (party in regulator) {
            if (!serviceHub.myInfo.isLegalIdentity(party)) {
                val session = initiateFlow(party)
                // finalTx 最终的Transaction
                subFlow(SendTransactionFlow(session, finalTx))
            }
        }
    }
}


@InitiatedBy(ReportToRegulatorFlow::class)
class ReceiveRegulatoryReportFlow(private val otherSideSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val stx = otherSideSession.receive<SignedTransaction>().unwrap {
            subFlow(MyResolveTransactionsFlow(it, otherSideSession))
            it.verify(serviceHub, true)
            it
        }

        logger.info("stx is $stx")
        // Start the matching side of SendTransactionFlow above, but tell it to record all visible states even
        // though they (as far as the node can tell) are nothing to do with us.
//            subFlow(ReceiveTransactionFlow(otherSideSession, true, StatesToRecord.ALL_VISIBLE))
        serviceHub.recordTransactions(StatesToRecord.ALL_VISIBLE, setOf(stx))
    }
}



