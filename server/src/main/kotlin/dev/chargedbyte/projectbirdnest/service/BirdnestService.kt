package dev.chargedbyte.projectbirdnest.service

import dev.chargedbyte.projectbirdnest.extensions.fromXml
import dev.chargedbyte.projectbirdnest.model.Pilot
import dev.chargedbyte.projectbirdnest.model.Report
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class BirdnestService(private val birdnestApiClient: WebClient) {
    suspend fun getReport(): Report = birdnestApiClient.get()
        .uri("/drones")
        .accept(MediaType.APPLICATION_XML)
        .retrieve()
        .awaitBody<String>()
        .fromXml()

    suspend fun getPilotByDroneSerialNumber(serialNumber: String): Pilot? = birdnestApiClient.get()
        .uri("/pilots/{serialNumber}", serialNumber)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono<Pilot>()
        .retry(3)
        .awaitSingleOrNull()
}
