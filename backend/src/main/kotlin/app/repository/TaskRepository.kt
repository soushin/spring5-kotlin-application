package app.repository

import app.ConflictException
import app.NotFoundException
import app.SystemException
import app.entity.Task
import com.github.kittinunf.result.Result
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import org.springframework.stereotype.Repository

/**
 *
 * @author nsoushi
 */
@Repository
class TaskRepository(val data: KotlinEntityDataStore<Any>) {

    fun findOneById(id: Long): Result<Task, RepositoryException> {
        return data.invoke {
            val query = select(Task::class) where (Task::id eq id)
            if (query.get().firstOrNull() == null)
                Result.Failure(RepositoryException.NotFoundException("task not found"))
            else
                Result.Success(query.get().first())
        }
    }
}
