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

data class DeleteTaskCommand(val id: Long)

@Service("deleteTaskService")
class DeleteTaskService(private val taskRepository: TaskRepository) {

    @Transactional
    fun deleteTask(command: DeleteTaskCommand): Task {
        return taskRepository.deleteById(command.id).fold({
            task -> task
        }, {
            error -> throw handle(error)
        })
    }
}
