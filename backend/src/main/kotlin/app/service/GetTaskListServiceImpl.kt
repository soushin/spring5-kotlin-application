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

data class GetTaskListCommand(val page: Int)

@Service("getTaskListService")
class GetTaskListServiceImpl(private val taskRepository: TaskRepository) : ApplicationService<GetTaskListCommand, List<Task>> {

    @Transactional(readOnly = true)
    override fun invoke(command: GetTaskListCommand): List<Task> {
        return taskRepository.findMany().fold({
            taskList -> taskList
        }, {
            error -> throw handle(error)
        })
    }
}