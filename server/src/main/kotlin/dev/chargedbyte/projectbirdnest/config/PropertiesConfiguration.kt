package dev.chargedbyte.projectbirdnest.config

import dev.chargedbyte.projectbirdnest.properties.NoDroneZoneProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(NoDroneZoneProperties::class)
class PropertiesConfiguration
