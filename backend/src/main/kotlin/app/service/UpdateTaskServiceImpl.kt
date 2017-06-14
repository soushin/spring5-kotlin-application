package app.service

import app.entity.Task
import app.repository.RepositoryException.Companion.handle
import app.repository.TaskRepository
import com.github.kittinunf.result.Validation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author nsoushi
 */

data class UpdateTaskCommand(val id: Long, val title: String)

@Service("updateTaskService")
class UpdateTaskServiceImpl(private val taskRepository: TaskRepository) : ApplicationService<UpdateTaskCommand, Task> {

    @Transactional
    override fun invoke(command: UpdateTaskCommand): Task {
        return taskRepository.updateById(command.id, command.title).fold({
            task -> task
        }, {
            error -> throw handle(error)
        })
    }
}