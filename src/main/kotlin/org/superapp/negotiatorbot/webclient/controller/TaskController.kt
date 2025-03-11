package org.superapp.negotiatorbot.webclient.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.superapp.negotiatorbot.webclient.entity.task.Task
import org.superapp.negotiatorbot.webclient.entity.task.TaskType
import org.superapp.negotiatorbot.webclient.service.functionality.task.TaskService

@RestController
@RequestMapping("/api/task")
class TaskController(private val taskService: TaskService) {
    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: Long): ResponseEntity<Task> {
        return ResponseEntity.ok(taskService.getById(taskId))
    }

    @GetMapping("/company/{companyId}")
    fun getTaskCompany(@PathVariable companyId: Long): ResponseEntity<Task> {
        return ResponseEntity.ok(taskService.getByTypeAndReference(TaskType.COMPANY, companyId))
    }

    @GetMapping("/contractor/{contractorId}")
    fun getTaskContractor(@PathVariable contractorId: Long): ResponseEntity<Task> {
        return ResponseEntity.ok(taskService.getByTypeAndReference(TaskType.CONTRACTOR, contractorId))
    }

    @GetMapping("/project/{projectId}")
    fun getTaskProject(@PathVariable projectId: Long): ResponseEntity<Task> {
        return ResponseEntity.ok(taskService.getByTypeAndReference(TaskType.PROJECT, projectId))
    }
}