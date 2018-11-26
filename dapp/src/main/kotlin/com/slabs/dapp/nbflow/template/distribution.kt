package com.slabs.nbflow.template

import com.slabs.nbflow.FlowLogicX
import com.slabs.nbflow.ReportToRegulatorFlow
import com.slabs.nbflow.defaultNotary
import com.slabs.nbflow.queryBy
import net.corda.core.identity.Party
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.schemas.QueryableState
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.text.SimpleDateFormat
import java.util.*

interface AbstractPermissionState : QueryableState {
    fun permission(): AbstractPermission
}

interface AbstractPermission {
    fun party(): Party
}

abstract class DistributionFlow<T> : FlowLogicX<T>() {

    companion object {
        fun now() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    }

    fun init(traceInfo: String): TransactionBuilder {
        val tranBuilder = transactionBuilder(defaultNotary)
        return tranBuilder
    }

    fun distribute(transactionBuilder: TransactionBuilder, f: () -> List<Party>): SignedTransaction {
        val notarySignedTraction = transactionBuilder.verify().sign()
        val distributionPartys = f()
        subFlow(ReportToRegulatorFlow(distributionPartys, notarySignedTraction))
        return notarySignedTraction
    }

    protected fun QueryCriteria.partyListFromDb(): List<Party> {
        val states = queryBy<AbstractPermissionState>(this)
        return states.map { it.state.data.permission().party() }.toMutableList()
    }
}