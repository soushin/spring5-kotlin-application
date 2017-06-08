package app.entity

import io.requery.*
import java.time.LocalDateTime

/**
 *
 * @author nsoushi
 */
@Entity(model = "kt")
@Table(name = "task")
interface Task {

    @get:Key
    @get:Generated
    @get:Column(name = "task_id")
    var id: Long

    @get:Column(name = "title")
    var title: String

    @get:Column(name = "finished_at")
    var finishedAt: LocalDateTime?

    @get:Column(name = "created_at")
    var createdAt: LocalDateTime

    @get:Column(name = "updated_at")
    var updatedAt: LocalDateTime
}
