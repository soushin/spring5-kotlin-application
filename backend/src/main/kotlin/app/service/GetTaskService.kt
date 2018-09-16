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

interface GetTaskService {
    fun getTask(command: GetTaskCommand): Task
    fun getCount(): Int
}

@Service("getTaskService")
class GetTaskServiceImpl(private val taskRepository: TaskRepository) : GetTaskService {

    @Transactional(readOnly = true)
    override fun getTask(command: GetTaskCommand): Task {
        return taskRepository.findOneById(command.id).fold({
            task -> task
        }, {
            error -> throw handle(error)
        })
    }

    @Transactional(readOnly = true)
    override fun getCount(): Int {
        return taskRepository.fetchCount().fold({
            it
        }, {
            error -> throw handle(error)
        })
    }
}
