package dev.chargedbyte.projectbirdnest.config

import dev.chargedbyte.projectbirdnest.extensions.birdnestJsonMapper
import dev.chargedbyte.projectbirdnest.model.Violation
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration(private val factory: ReactiveRedisConnectionFactory) {
    @Bean
    fun redisOperations(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, Violation> {
        val serializer = Jackson2JsonRedisSerializer(birdnestJsonMapper(), Violation::class.java)
        val builder = RedisSerializationContext.newSerializationContext<String, Violation>(StringRedisSerializer())
        val context = builder.value(serializer).build()
        return ReactiveRedisTemplate(factory, context)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun configureRedis(): Unit = runBlocking {
        val requiredFlags = "E\$x".toCharArray()

        factory.reactiveConnection.use { connection ->
            val currentFlags = connection
                .serverCommands()
                .getConfig("notify-keyspace-events")
                .awaitSingle()
                .getProperty("notify-keyspace-events", String())

            var newFlags = currentFlags

            for (flag in requiredFlags) {
                if (!newFlags.contains(flag)) {
                    newFlags += flag
                }
            }

            if (newFlags != currentFlags) {
                connection.serverCommands().setConfig("notify-keyspace-events", newFlags).awaitSingle()
            }
        }
    }
}
