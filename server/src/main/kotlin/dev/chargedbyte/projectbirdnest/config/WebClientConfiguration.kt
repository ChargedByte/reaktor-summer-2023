package dev.chargedbyte.projectbirdnest.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.http.codec.xml.Jaxb2XmlDecoder
import org.springframework.http.codec.xml.Jaxb2XmlEncoder
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {
    @Bean
    fun birdnestApiClient(): WebClient = WebClient.builder()
        .baseUrl("https://assignments.reaktor.com/birdnest")
        .codecs { configurer ->
            val json = jsonMapper {
                addModules(JavaTimeModule(), kotlinModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            }

            configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(json))
            configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(json))

            configurer.defaultCodecs().jaxb2Decoder(Jaxb2XmlDecoder())
            configurer.defaultCodecs().jaxb2Encoder(Jaxb2XmlEncoder())
        }
        .build()
}
