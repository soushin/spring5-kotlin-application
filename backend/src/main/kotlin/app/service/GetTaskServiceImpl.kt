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

data class GetTaskCommand(val id: Long)

typealias GetTaskService = ApplicationService<GetTaskCommand, Task>

@Service("getTaskService")
class GetTaskServiceImpl(private val taskRepository: TaskRepository) : GetTaskService {

    @Transactional(readOnly = true)
    override fun invoke(command: GetTaskCommand): Task {
        return taskRepository.findOneById(command.id).fold({
            task -> task
        }, {
            error -> throw handle(error)
        })
    }
}