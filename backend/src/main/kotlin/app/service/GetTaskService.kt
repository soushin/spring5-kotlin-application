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

data class GetTaskCommand(val id: Long)

@Service("getTaskService")
class GetTaskService(private val taskRepository: TaskRepository) {

    @Transactional(readOnly = true)
    fun getTask(command: GetTaskCommand): Task {
        return taskRepository.findOneById(command.id).fold({
            task -> task
        }, {
            error -> throw handle(error)
        })
    }
}
