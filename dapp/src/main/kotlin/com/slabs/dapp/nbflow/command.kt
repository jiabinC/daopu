package com.slabs.nbflow

import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty

/**
 * This contract only implements one command, Create.
 */
interface Commands : CommandData {
    class Create : Commands
    class Update : Commands

    companion object {
        inline fun createCommand(participants: List<AbstractParty>)
                = Command(Commands.Create(), participants.map { it.owningKey })

        inline fun updateCommand(participants: List<AbstractParty>)
                = Command(Commands.Update(), participants.map { it.owningKey })

        inline fun createCommand(state: ContractState)
          = createCommand(state.participants)

        inline fun updateCommand(state: ContractState)
          = updateCommand(state.participants)
    }
}

val ContractState.createCommand: Command<Commands.Create>
    inline get() = Command(Commands.Create(), participants.map { it.owningKey })

val ContractState.updateCommand: Command<Commands.Update>
    inline get() = Command(Commands.Update(), participants.map { it.owningKey })


operator inline fun ContractState.plus(id: ContractClassName)
        = StateAndContract(this, id)

inline infix fun ContractState.and(id: ContractClassName) = this + id
