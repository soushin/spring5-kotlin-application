package app.entity

import org.seasar.doma.*
import java.time.LocalDateTime

@Entity(immutable = true)
@Table(name = "task")
data class Task(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "task_id")
        val id: Int? = null,
        @Column(name = "title")
        val title: String,
        @Column(name = "finished_at")
        val finishedAt: LocalDateTime?,
        @Column(name = "created_at")
        val createdAt: LocalDateTime,
        @Column(name = "updated_at")
        val updatedAt: LocalDateTime
)
