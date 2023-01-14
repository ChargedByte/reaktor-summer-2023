package dev.chargedbyte.projectbirdnest.extensions

import dev.chargedbyte.projectbirdnest.model.Violation
import dev.chargedbyte.projectbirdnest.model.message.SetViolationMessage

fun Violation.toSetMessage(): SetViolationMessage = SetViolationMessage(this)
