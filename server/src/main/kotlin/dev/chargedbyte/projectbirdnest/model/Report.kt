package dev.chargedbyte.projectbirdnest.model

import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlList
import jakarta.xml.bind.annotation.XmlRootElement
import java.util.*

@XmlRootElement
data class Report(var deviceInformation: DeviceInformation? = null, var capture: Capture? = null) {
    data class DeviceInformation(
        @get:XmlAttribute
        var deviceId: String? = null,
        var listenRange: Int? = null,
        var deviceStarted: Calendar? = null,
        var uptimeSeconds: Long? = null,
        var updateIntervalMs: Long? = null
    )

    data class Capture(
        @get:XmlAttribute
        var snapshotTimestamp: Calendar? = null,
        @get:XmlElement(name = "drone")
        @XmlList
        var drones: List<Drone>? = null
    ) {
        data class Drone(
            var serialNumber: String? = null,
            var model: String? = null,
            var manufacturer: String? = null,
            var mac: String? = null,
            var ipv4: String? = null,
            var ipv6: String? = null,
            var firmware: String? = null,
            var positionY: Double? = null,
            var positionX: Double? = null,
            var altitude: Double? = null
        )
    }
}
