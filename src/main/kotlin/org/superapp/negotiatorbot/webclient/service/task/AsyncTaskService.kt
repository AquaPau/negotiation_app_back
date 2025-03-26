package org.superapp.negotiatorbot.webclient.service.task

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.superapp.negotiatorbot.webclient.entity.TaskEnabled
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.enums.TaskStatus
import org.superapp.negotiatorbot.webclient.exception.TaskException
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService

abstract class AsyncTaskService<out T : TaskEnabled>(
    private val taskService: TaskRecordService
) {

    @OptIn(DelicateCoroutinesApi::class)
    fun execute(taskEnabled: TaskEnabled, vararg data: Any) {
        GlobalScope.launch {
            var task = taskService.createTask(taskEnabled, data)
            try {
                task = run(task, taskEnabled, data)
                taskService.changeStatus(task, TaskStatus.FINISHED)
            } catch (e: TaskException) {
                taskService.changeStatus(task, e.status)
            } catch (e: Exception) {
                taskService.changeStatus(task, TaskStatus.UNEXPECTED_ERROR)
            }
        }
    }

    abstract fun run(task: TaskRecord, taskEnabled: TaskEnabled, vararg data: Any): TaskRecord
}