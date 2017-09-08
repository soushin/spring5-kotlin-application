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

interface UpdateTaskService {
    fun updateTask(command: UpdateTaskCommand): Task
}

@Service("updateTaskService")
class UpdateTaskServiceImpl(private val taskRepository: TaskRepository) : UpdateTaskService {

    @Transactional
    override fun updateTask(command: UpdateTaskCommand): Task {
        return taskRepository.updateById(command.id, command.title).fold({
            task -> task
        }, {
            error -> throw handle(error)
        })
    }
}
