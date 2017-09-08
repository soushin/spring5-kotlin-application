package app.service

import app.entity.Task
import app.repository.RepositoryException.Companion.handle
import app.repository.TaskRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author nsoushi
 */

data class CreateTaskCommand(val title: String)

@Service("createTaskService")
class CreateTaskService(private val taskRepository: TaskRepository) {

    @Transactional
    fun createTask(command: CreateTaskCommand): Task {
        return taskRepository.create(command.title).fold({
            task -> task
        }, {
            error -> throw handle(error)
        })
    }
}
