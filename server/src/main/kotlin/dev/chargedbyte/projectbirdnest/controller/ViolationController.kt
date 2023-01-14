package dev.chargedbyte.projectbirdnest.controller

import dev.chargedbyte.projectbirdnest.extensions.toSetMessage
import dev.chargedbyte.projectbirdnest.model.Violation
import dev.chargedbyte.projectbirdnest.model.message.ViolationMessage
import dev.chargedbyte.projectbirdnest.service.ViolationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/violations")
@MessageMapping("api.v1.violations")
class ViolationController(private val violationService: ViolationService) {
    @GetMapping
    fun all(): Flow<Violation> = violationService.findAll()

    @MessageMapping("events")
    fun events(): Flow<ViolationMessage> = violationService
        .eventStream()
        .onStart { emitAll(violationService.findAll().map(Violation::toSetMessage)) }
}
