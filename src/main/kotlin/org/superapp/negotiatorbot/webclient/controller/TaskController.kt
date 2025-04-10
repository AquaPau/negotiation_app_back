package org.superapp.negotiatorbot.webclient.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.superapp.negotiatorbot.webclient.dto.TaskRecordDto
import org.superapp.negotiatorbot.webclient.entity.task.TaskRecord
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskRecordService

@RestController
@RequestMapping("/api/task")
class TaskController(private val taskService: TaskRecordService) {
    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: Long): TaskRecordDto {
        return taskService.getById(taskId)
    }
}