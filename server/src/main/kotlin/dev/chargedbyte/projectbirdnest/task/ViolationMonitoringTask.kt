package dev.chargedbyte.projectbirdnest.task

import dev.chargedbyte.projectbirdnest.extensions.getLogger
import dev.chargedbyte.projectbirdnest.model.Violation
import dev.chargedbyte.projectbirdnest.service.BirdnestService
import dev.chargedbyte.projectbirdnest.service.ViolationService
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class ViolationMonitoringTask(
    private val birdnestService: BirdnestService,
    private val violationService: ViolationService
) : Runnable {
    private val logger = getLogger()

    override fun run(): Unit = runBlocking {
        val report = birdnestService.getReport()

        val (snapshotTimestamp, drones) = report.capture

        logger.debug("Report timestamp='$snapshotTimestamp', drones=${drones.count()}")

        // Reset expiration of pre-existing violations
        drones.asFlow()
            .mapNotNull { violationService.findByKey(it.serialNumber) }
            .collect(violationService::save)

        // Create violations from drones in the current snapshot
        drones.asFlow()
            .filter { !violationService.existsByKey(it.serialNumber) && violationService.isDroneInViolation(it) }
            .map { Pair(it, birdnestService.getPilotByDroneSerialNumber(it.serialNumber)) }
            .map { Violation(it.first, it.second, snapshotTimestamp) }
            .collect(violationService::save)
    }
}
