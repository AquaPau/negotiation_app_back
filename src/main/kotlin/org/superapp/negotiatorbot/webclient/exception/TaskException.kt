package org.superapp.negotiatorbot.webclient.exception

import org.superapp.negotiatorbot.webclient.enums.TaskStatus

class TaskException(val status: TaskStatus) :
    RuntimeException("Task was not finished successfully and exited with status $status")