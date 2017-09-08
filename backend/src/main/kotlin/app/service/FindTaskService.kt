package app.service

import app.entity.Task
import app.repository.RepositoryException.Companion.handle
import app.repository.TaskRepository
import app.util.ListModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author nsoushi
 */

data class FindTaskCommand(val page: Int)

@Service("findTaskService")
class FindTaskService(private val taskRepository: TaskRepository) {

    @Transactional(readOnly = true)
    fun findTask(command: FindTaskCommand): ListModel<Task> {
        return taskRepository.findMany().fold({
            taskList -> ListModel(taskList)
        }, {
            error -> throw handle(error)
        })
    }
}
