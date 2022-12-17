package dev.chargedbyte.projectbirdnest.model.message

interface ViolationMessage {
    val action: MessageAction
    val content: Any
}
