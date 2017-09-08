package app.service

import org.springframework.stereotype.Service

@Service
class DelegateTaskService(private val getTaskService: GetTaskService,
                          private val findTaskService: FindTaskService,
                          private val createTaskService: CreateTaskService,
                          private val updateTaskService: UpdateTaskService,
                          private val deleteTaskService: DeleteTaskService,
                          private val finishTaskService: FinishTaskService) :
        GetTaskService by getTaskService,
        FindTaskService by findTaskService,
        CreateTaskService by createTaskService,
        UpdateTaskService by updateTaskService,
        DeleteTaskService by deleteTaskService,
        FinishTaskService by finishTaskService
