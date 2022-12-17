package dev.chargedbyte.projectbirdnest.model.message

data class ExpiredViolationMessage(override val content: String) : ViolationMessage {
    override val action: MessageAction = MessageAction.EXPIRED
}
