package dev.chargedbyte.projectbirdnest.extensions

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

fun birdnestJsonMapper(): ObjectMapper = JsonMapper()
    .registerModule(JavaTimeModule())
    .registerKotlinModule()
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

fun birdnestXmlMapper(): ObjectMapper = XmlMapper()
    .registerModule(JavaTimeModule())
    .registerKotlinModule()
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

inline fun <reified T : Any> String.fromJson(): T = birdnestJsonMapper().readValue(this)

inline fun <reified T : Any> String.fromXml(): T = birdnestXmlMapper().readValue(this)
