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

data class GetTaskListCommand(val page: Int)

typealias GetTaskListService = ApplicationService<GetTaskListCommand, ListModel<Task>>

@Service("getTaskListService")
class GetTaskListServiceImpl(private val taskRepository: TaskRepository) : GetTaskListService {

    @Transactional(readOnly = true)
    override fun invoke(command: GetTaskListCommand): ListModel<Task> {
        return taskRepository.findMany().fold({
            taskList -> ListModel(taskList)
        }, {
            error -> throw handle(error)
        })
    }
}