package dev.chargedbyte.projectbirdnest.model

import com.fasterxml.jackson.annotation.JsonAlias
import java.time.OffsetDateTime

data class Pilot(
    val pilotId: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    @JsonAlias("createdDt")
    val createdAt: OffsetDateTime,
    val email: String
)
