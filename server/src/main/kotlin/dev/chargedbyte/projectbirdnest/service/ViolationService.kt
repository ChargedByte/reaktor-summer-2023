package dev.chargedbyte.projectbirdnest.service

import dev.chargedbyte.projectbirdnest.model.Drone
import dev.chargedbyte.projectbirdnest.model.Violation
import dev.chargedbyte.projectbirdnest.model.message.ExpiredViolationMessage
import dev.chargedbyte.projectbirdnest.model.message.MessageAction
import dev.chargedbyte.projectbirdnest.model.message.SetViolationMessage
import dev.chargedbyte.projectbirdnest.model.message.ViolationMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.math.pow
import kotlin.math.sqrt

private const val NDZ_RADIUS: Int = 100_000
private const val NDZ_ORIGIN_X: Int = 250_000
private const val NDZ_ORIGIN_Y: Int = 250_000

@Service
class ViolationService(
    private val redisProperties: RedisProperties,
    private val stringRedisOps: ReactiveRedisOperations<String, String>,
    private val violationRedisOps: ReactiveRedisOperations<String, Violation>,
) {
    fun isDroneInViolation(drone: Drone): Boolean =
        sqrt((drone.positionX - NDZ_ORIGIN_X).pow(2) + (drone.positionY - NDZ_ORIGIN_Y).pow(2)) < NDZ_RADIUS

    suspend fun existsByKey(key: String): Boolean = violationRedisOps.hasKeyAndAwait(key)

    suspend fun findByKey(key: String): Violation? = violationRedisOps.opsForValue().getAndAwait(key)

    fun findAll(): Flow<Violation> =
        violationRedisOps.scanAsFlow().map(violationRedisOps.opsForValue()::getAndAwait).filterNotNull()

    suspend fun save(violation: Violation) {
        violationRedisOps.opsForValue().setAndAwait(violation.drone.serialNumber, violation, Duration.ofMinutes(10))
    }

    fun stream(): Flow<ViolationMessage> {
        val visibleKeys = mutableSetOf<String>()

        return stringRedisOps
            .listenToPatternAsFlow(
                "__keyevent@${redisProperties.database}__:set",
                "__keyevent@${redisProperties.database}__:expired"
            )
            .map { Pair(MessageAction.valueOf(it.channel.split(':')[1].uppercase()), it.message) }
            .filter { !(it.first == MessageAction.SET && visibleKeys.contains(it.second)) }
            .map {
                when (it.first) {
                    MessageAction.SET -> {
                        visibleKeys.add(it.second)
                        SetViolationMessage(findByKey(it.second) ?: TODO("In theory this is unreachable"))
                    }

                    MessageAction.EXPIRED -> {
                        visibleKeys.remove(it.second)
                        ExpiredViolationMessage(it.second)
                    }
                }
            }
    }
}
