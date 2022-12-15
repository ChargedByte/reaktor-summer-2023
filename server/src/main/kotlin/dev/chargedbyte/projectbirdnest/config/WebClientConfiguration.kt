package dev.chargedbyte.projectbirdnest.config

import dev.chargedbyte.projectbirdnest.extensions.birdnestJsonMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {
    @Bean
    fun birdnestApiClient(): WebClient = WebClient.builder()
        .baseUrl("https://assignments.reaktor.com/birdnest")
        .codecs { configurer ->
            configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(birdnestJsonMapper()))
            configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(birdnestJsonMapper()))
        }
        .build()
}
