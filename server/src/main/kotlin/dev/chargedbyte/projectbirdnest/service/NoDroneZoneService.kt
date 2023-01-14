package dev.chargedbyte.projectbirdnest.service

import dev.chargedbyte.projectbirdnest.model.Drone
import dev.chargedbyte.projectbirdnest.properties.NoDroneZoneProperties
import org.springframework.stereotype.Service
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * [Service] component for No Drone Zone (NDZ) calculations.
 */
@Service
class NoDroneZoneService(private val ndzProperties: NoDroneZoneProperties) {
    /**
     * Calculate the distance from [drone]'s position to the center of the No Drone Zone (NDZ).
     */
    fun getDroneDistanceToTheNest(drone: Drone): Double =
        sqrt((drone.positionX - ndzProperties.originX).pow(2) + (drone.positionY - ndzProperties.originY).pow(2))

    /**
     * Check whether the [drone] is inside the No Drone Zone (NDZ).
     */
    fun isDroneInViolation(drone: Drone): Boolean = getDroneDistanceToTheNest(drone) < ndzProperties.radius
}
