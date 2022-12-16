package dev.chargedbyte.projectbirdnest.model

data class Drone(
    val serialNumber: String,
    val model: String,
    val manufacturer: String,
    val mac: String,
    val ipv4: String,
    val ipv6: String,
    val firmware: String,
    val positionY: Double,
    val positionX: Double,
    val altitude: Double
)
