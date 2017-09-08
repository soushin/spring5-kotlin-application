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

data class UpdateTaskCommand(val id: Long, val title: String)

@Service("updateTaskService")
class UpdateTaskService(private val taskRepository: TaskRepository) {

    @Transactional
    fun updateTask(command: UpdateTaskCommand): Task {
        return taskRepository.updateById(command.id, command.title).fold({
            task -> task
        }, {
            error -> throw handle(error)
        })
    }
}
