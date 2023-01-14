package dev.chargedbyte.projectbirdnest.task

import dev.chargedbyte.projectbirdnest.extensions.getLogger
import dev.chargedbyte.projectbirdnest.model.Violation
import dev.chargedbyte.projectbirdnest.service.BirdnestApiService
import dev.chargedbyte.projectbirdnest.service.NoDroneZoneService
import dev.chargedbyte.projectbirdnest.service.ViolationService
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class ViolationMonitoringTask(
    private val birdnestApiService: BirdnestApiService,
    private val noDroneZoneService: NoDroneZoneService,
    private val violationService: ViolationService,
) : Runnable {
    private val logger = getLogger()

    override fun run(): Unit = runBlocking {
        val report = birdnestApiService.getReport()

        val (snapshotTimestamp, drones) = report.capture

        val (oldDrones, newDrones) = drones.partition { violationService.existsById(it.serialNumber) }

        // Update the expiry of violations from drones in the current snapshot
        val updated = oldDrones.asFlow()
            .map { Pair(violationService.findById(it.serialNumber)!!, it) }
            .map { (violation, drone) ->
                Violation(
                    drone,
                    // If pilot information is missing, try looking for it again
                    violation.pilot ?: birdnestApiService.getPilotByDroneSerialNumber(drone.serialNumber),
                    violation.recordedAt,
                    // Select the smallest of the two distances
                    min(violation.closestDistanceToNest, noDroneZoneService.getDroneDistanceToTheNest(drone))
                )
            }
            .map(violationService::save)
            .count()

        // Create violations for new drones
        val created = newDrones.asFlow()
            .filter { noDroneZoneService.isDroneInViolation(it) }
            .map {
                Violation(
                    it,
                    birdnestApiService.getPilotByDroneSerialNumber(it.serialNumber),
                    snapshotTimestamp,
                    noDroneZoneService.getDroneDistanceToTheNest(it)
                )
            }
            .map(violationService::save)
            .count()

        logger.info("Found $created violations and updated $updated existing violations")
    }
}
