package dev.chargedbyte.projectbirdnest.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class Pilot(
    @JsonProperty("pilotId")
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    @JsonAlias("createdDt")
    val createdAt: OffsetDateTime,
    val email: String
)
