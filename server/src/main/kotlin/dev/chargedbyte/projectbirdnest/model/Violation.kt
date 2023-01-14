package dev.chargedbyte.projectbirdnest.model

import java.time.OffsetDateTime

data class Violation(
    val drone: Drone,
    val pilot: Pilot?,
    val recordedAt: OffsetDateTime,
    val closestDistanceToNest: Double,
)
