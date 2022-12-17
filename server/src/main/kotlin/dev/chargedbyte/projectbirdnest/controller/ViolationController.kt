package dev.chargedbyte.projectbirdnest.controller

import dev.chargedbyte.projectbirdnest.model.Violation
import dev.chargedbyte.projectbirdnest.model.message.ViolationMessage
import dev.chargedbyte.projectbirdnest.service.ViolationService
import kotlinx.coroutines.flow.Flow
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("api.v1.violations")
class ViolationController(private val violationService: ViolationService) {
    @MessageMapping("all")
    fun all(): Flow<Violation> = violationService.findAll()

    @MessageMapping("events")
    fun events(): Flow<ViolationMessage> = violationService.stream()
}
