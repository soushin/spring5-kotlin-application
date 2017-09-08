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

interface DeleteTaskService {
    fun deleteTask(command: DeleteTaskCommand): Task
}

@Service("deleteTaskService")
class DeleteTaskServiceImpl(private val taskRepository: TaskRepository) : DeleteTaskService {

    @Transactional
    override fun deleteTask(command: DeleteTaskCommand): Task {
        return taskRepository.deleteById(command.id).fold({
            task -> task
        }, {
            error -> throw handle(error)
        })
    }
}
