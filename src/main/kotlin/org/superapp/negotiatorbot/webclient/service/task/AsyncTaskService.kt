package org.superapp.negotiatorbot.webclient.service.task

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService

private val log = KotlinLogging.logger {}

@Component
abstract class AsyncTaskService<out T : TaskEnabled>(
    protected val taskService: TaskRecordService
) {

    @OptIn(DelicateCoroutinesApi::class)
    fun execute(taskEnabled: TaskEnabled, vararg data: Any): Job {
        return GlobalScope.launch {
            var task = taskService.createTask(taskEnabled, data)
            try {
                log.info("Starting task [$taskEnabled]")
                task = run(task, taskEnabled, data)
                taskService.changeStatus(task, TaskStatus.FINISHED)
                log.info("Finished task [$taskEnabled]")
            } catch (e: TaskException) {
                log.error("Task exception occur: ", e)
                taskService.changeStatus(task, e.status)
            } catch (e: Exception) {
                log.error("Unexpected exception in task  occur: ", e)
                taskService.changeStatus(task, TaskStatus.UNEXPECTED_ERROR)
            }
        }
    }

    abstract fun run(task: TaskRecord, taskEnabled: TaskEnabled, vararg data: Any): TaskRecord
}