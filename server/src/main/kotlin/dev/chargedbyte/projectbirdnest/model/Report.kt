package dev.chargedbyte.projectbirdnest.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import java.time.OffsetDateTime

data class Report(val deviceInformation: DeviceInformation, val capture: Capture) {
    data class DeviceInformation(
        @JacksonXmlProperty(localName = "deviceId", isAttribute = true)
        val id: String,
        val listenRange: Int,
        val deviceStarted: OffsetDateTime,
        val uptimeSeconds: Int,
        val updateIntervalMs: Long
    )

    data class Capture(
        @JacksonXmlProperty(isAttribute = true)
        val snapshotTimestamp: OffsetDateTime,
        @JacksonXmlProperty(localName = "drone")
        @JacksonXmlElementWrapper(useWrapping = false)
        val drones: List<Drone>
    )
}
