package dev.chargedbyte.projectbirdnest.config

import dev.chargedbyte.projectbirdnest.extensions.birdnestJsonMapper
import dev.chargedbyte.projectbirdnest.model.Violation
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration {
    @Bean
    fun violationRedisOperations(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, Violation> =
        runBlocking {
            // Enable Redis keyspace notifications
            factory.reactiveConnection.apply {
                serverCommands().setConfig("notify-keyspace-events", "E\$x").awaitSingle()
            }.close()

            val serializer = Jackson2JsonRedisSerializer(birdnestJsonMapper(), Violation::class.java)
            val builder = RedisSerializationContext.newSerializationContext<String, Violation>(StringRedisSerializer())
            val context = builder.value(serializer).build()

            ReactiveRedisTemplate(factory, context)
        }
}
