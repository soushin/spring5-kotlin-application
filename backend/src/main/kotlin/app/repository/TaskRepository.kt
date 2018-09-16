package app.repository

import app.dao.TaskDao
import app.entity.Task
import com.github.kittinunf.result.Result
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 *
 * @author nsoushi
 */
@Repository
class TaskRepository(val dao: TaskDao) {

    fun create(title: String): Result<Task, RepositoryException> {
        val now = LocalDateTime.now()
        val result = dao.insert(Task(title = title, finishedAt = null, createdAt = now, updatedAt = now))
        return Result.Success(result.entity)
    }

    fun findMany(): Result<List<Task>, RepositoryException> {
        val entities = dao.selectAll()
        if (entities.isEmpty()) return Result.Failure(RepositoryException.NotFoundException("task not found"))
        return Result.Success(entities)
    }

    fun findOneById(id: Long): Result<Task, RepositoryException> {
        val entity = dao.selectById(id) ?: return Result.Failure(RepositoryException.NotFoundException("task not found"))
        return Result.Success(entity)
    }

    fun updateById(id: Long, title: String): Result<Task, RepositoryException> {
        val entity = dao.selectById(id) ?: return Result.Failure(RepositoryException.NotFoundException("task not found"))
        val result = dao.update(entity.copy(title = title, updatedAt = LocalDateTime.now()))
        return Result.Success(result.entity)
    }

    fun deleteById(id: Long): Result<Task, RepositoryException> {
        val entity = dao.selectById(id) ?: return Result.Failure(RepositoryException.NotFoundException("task not found"))
        dao.delete(entity)
        return Result.Success(entity)
    }

    fun finishById(id: Long): Result<Task, RepositoryException> {
        val entity = dao.selectById(id) ?: return Result.Failure(RepositoryException.NotFoundException("task not found"))
        val now = LocalDateTime.now()
        val result = dao.update(entity.copy(finishedAt = now, updatedAt = now))
        return Result.Success(result.entity)
    }

    fun fetchCount(): Result<Int, RepositoryException> {
        val count = dao.selectCount() ?: return Result.Failure(RepositoryException.NotFoundException("task not found"))
        return Result.Success(count)
    }
}
