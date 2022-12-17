package dev.chargedbyte.projectbirdnest.model.message

import dev.chargedbyte.projectbirdnest.model.Violation

data class SetViolationMessage(override val content: Violation) : ViolationMessage {
    override val action: MessageAction = MessageAction.SET
}
