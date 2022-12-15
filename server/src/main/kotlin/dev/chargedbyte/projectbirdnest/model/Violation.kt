package dev.chargedbyte.projectbirdnest.model

import java.time.OffsetDateTime
import java.util.*

data class Violation(
    val id: UUID = UUID.randomUUID(),
    val recordedAt: OffsetDateTime,
    val pilot: Pilot?,
    val drone: Report.Capture.Drone
)
