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

interface FindTaskService {
    fun findTask(command: FindTaskCommand): ListModel<Task>
}

@Service("findTaskService")
class FindTaskServiceImpl(private val taskRepository: TaskRepository) : FindTaskService {

    @Transactional(readOnly = true)
    override fun findTask(command: FindTaskCommand): ListModel<Task> {
        return taskRepository.findMany().fold({
            taskList -> ListModel(taskList)
        }, {
            error -> throw handle(error)
        })
    }
}
