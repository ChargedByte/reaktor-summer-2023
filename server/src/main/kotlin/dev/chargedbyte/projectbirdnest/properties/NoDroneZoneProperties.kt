package dev.chargedbyte.projectbirdnest.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * [properties][ConfigurationProperties] for the No Drone Zone (NDZ).
 */
@ConfigurationProperties("projectbirdnest.ndz")
class NoDroneZoneProperties(
    /**
     * x-Coordinate of the No Drone Zone (NDZ)'s origin.
     */
    var originX: Double = 0.0,
    /**
     * y-Coordinate of the No Drone Zone (NDZ)'s origin.
     */
    var originY: Double = 0.0,
    /**
     * Radius of the No Drone Zone (NDZ).
     */
    var radius: Double = 0.0,
)
