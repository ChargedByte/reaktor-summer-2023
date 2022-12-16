package dev.chargedbyte.projectbirdnest.service

import dev.chargedbyte.projectbirdnest.model.Drone
import dev.chargedbyte.projectbirdnest.model.Violation
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.getAndAwait
import org.springframework.data.redis.core.hasKeyAndAwait
import org.springframework.data.redis.core.setAndAwait
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.math.pow
import kotlin.math.sqrt

private const val NDZ_RADIUS: Int = 100_000
private const val NDZ_ORIGIN_X: Int = 250_000
private const val NDZ_ORIGIN_Y: Int = 250_000

@Service
class ViolationService(private val redisOps: ReactiveRedisOperations<String, Violation>) {
    fun isDroneInViolation(drone: Drone): Boolean =
        sqrt((drone.positionX - NDZ_ORIGIN_X).pow(2) + (drone.positionY - NDZ_ORIGIN_Y).pow(2)) < NDZ_RADIUS

    suspend fun existsByKey(key: String): Boolean = redisOps.hasKeyAndAwait(key)

    suspend fun findByKey(key: String): Violation? = redisOps.opsForValue().getAndAwait(key)

    suspend fun save(violation: Violation) {
        redisOps.opsForValue().setAndAwait(violation.drone.serialNumber, violation, Duration.ofMinutes(10))
    }
}
