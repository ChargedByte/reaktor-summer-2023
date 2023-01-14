package dev.chargedbyte.projectbirdnest.service

import dev.chargedbyte.projectbirdnest.extensions.fromXml
import dev.chargedbyte.projectbirdnest.model.Pilot
import dev.chargedbyte.projectbirdnest.model.Report
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

/**
 * [Service] component for interacting with the Reaktor's Birdnest API.
 */
@Service
class BirdnestApiService(private val birdnestApiClient: WebClient) {
    suspend fun getReport(): Report = birdnestApiClient.get()
        .uri("/drones")
        .accept(MediaType.APPLICATION_XML)
        .retrieve()
        .bodyToMono<String>()
        .retryWhen(Retry.backoff(5, Duration.ofMillis(250)))
        .awaitSingle()
        .fromXml()

    suspend fun getPilotByDroneSerialNumber(serialNumber: String): Pilot? = birdnestApiClient.get()
        .uri("/pilots/{serialNumber}", serialNumber)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus({ status -> status == HttpStatus.NOT_FOUND }) { Mono.empty() }
        .bodyToMono<Pilot>()
        .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
        .awaitSingleOrNull()
}
