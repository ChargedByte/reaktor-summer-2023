package dev.chargedbyte.projectbirdnest.service

import dev.chargedbyte.projectbirdnest.extensions.toSetMessage
import dev.chargedbyte.projectbirdnest.model.Violation
import dev.chargedbyte.projectbirdnest.model.message.ExpiredViolationMessage
import dev.chargedbyte.projectbirdnest.model.message.MessageAction
import dev.chargedbyte.projectbirdnest.model.message.ViolationMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * [Service] component for [Violation] data handling.
 */
@Service
class ViolationService(
    private val redisProperties: RedisProperties,
    private val stringRedisOperations: ReactiveRedisOperations<String, String>,
    private val violationRedisOperations: ReactiveRedisOperations<String, Violation>,
) {
    /**
     * Check whether a [Violation] exists with the provided [id].
     */
    suspend fun existsById(id: String): Boolean = findById(id) != null

    /**
     * Get all [Violations][Violation] in the database as a Kotlin [Flow].
     */
    fun findAll(): Flow<Violation> =
        violationRedisOperations.scanAsFlow().map(violationRedisOperations.opsForValue()::getAndAwait).filterNotNull()

    /**
     * Find a [Violation] in the database with the provided [id], returns `null` if not found.
     */
    suspend fun findById(id: String): Violation? = violationRedisOperations.opsForValue().getAndAwait(id)

    /**
     * Save the provided [entity] to the database with the specified [timeout/expiry][timeout] (defaults to 10 minutes).
     */
    suspend fun save(entity: Violation, timeout: Duration = Duration.ofMinutes(10)): Boolean =
        violationRedisOperations.opsForValue().setAndAwait(entity.drone.serialNumber, entity, timeout)

    /**
     * Listen to Redis keyspace notifications, received messages are parsed to [ViolationMessages][ViolationMessage].
     */
    fun eventStream(): Flow<ViolationMessage> = stringRedisOperations
        .listenToPatternAsFlow(
            "__keyevent@${redisProperties.database}__:set",
            "__keyevent@${redisProperties.database}__:expired"
        )
        .map { Pair(MessageAction.valueOf(it.channel.split(':')[1].uppercase()), it.message) }
        .map { (action, id) ->
            when (action) {
                MessageAction.SET -> findById(id)?.toSetMessage()
                    ?: throw RuntimeException("No violation found for id '$id'")

                MessageAction.EXPIRED -> ExpiredViolationMessage(id)
            }
        }
}
