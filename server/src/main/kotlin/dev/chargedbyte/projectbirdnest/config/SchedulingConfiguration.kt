package dev.chargedbyte.projectbirdnest.config

import dev.chargedbyte.projectbirdnest.extensions.getLogger
import dev.chargedbyte.projectbirdnest.service.BirdnestApiService
import dev.chargedbyte.projectbirdnest.task.ViolationMonitoringTask
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import java.time.Duration

@Configuration
@EnableScheduling
class SchedulingConfiguration(
    private val taskScheduler: TaskScheduler,
    private val birdnestApiService: BirdnestApiService,
    private val violationMonitoringTask: ViolationMonitoringTask
) {
    private val logger = getLogger()

    @EventListener(ApplicationReadyEvent::class)
    fun scheduleTasks(): Unit = runBlocking {
        val report = birdnestApiService.getReport()

        // Let's add 15 ms of delay to avoid pulling the date too early
        val period = Duration.ofMillis(report.deviceInformation.updateIntervalMs + 15)
        val startTime = report.capture.snapshotTimestamp.plus(period).toInstant()

        logger.info("Scheduling '${ViolationMonitoringTask::class.qualifiedName}' to start at '$startTime' and to run every ${period.toMillis()} ms")

        taskScheduler.scheduleAtFixedRate(violationMonitoringTask, startTime, period)
    }
}
