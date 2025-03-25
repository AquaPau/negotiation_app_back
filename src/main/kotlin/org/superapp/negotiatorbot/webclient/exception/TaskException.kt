package org.superapp.negotiatorbot.webclient.exception

import org.superapp.negotiatorbot.webclient.enums.TaskStatus

class TaskException(val status: TaskStatus): RuntimeException()