package xyz.katiedotson.dodo.data.todo

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val description: String,
    val dateCreated: LocalDateTime,
    val lastUpdate: LocalDateTime,
    val dateDue: LocalDateTime?,
    val labelId: Long?,
    val notes: String
) {
    override fun toString() = "{ TODO: \nDescription: $description \nID: $id} "

    @DatabaseView(
        "SELECT " +
                "todo.id as todoId, " +
                "todo.description, " +
                "todo.dateCreated, " +
                "todo.lastUpdate, " +
                "todo.dateDue, " +
                "todo.notes, " +
                "label.id as labelId, " +
                "label.name as labelName, " +
                "label.colorHex, " +
                "color.useBorder, " +
                "color.useWhiteText " +
                "FROM todos as todo  " +
                "LEFT OUTER JOIN labels as label ON upper(todo.labelId) = upper(label.id) " +
                "LEFT OUTER JOIN colors as color ON upper(color.hex) = upper(label.colorHex) "
    )
    data class WithLabel(
        val todoId: Long,
        val description: String,
        val dateCreated: LocalDateTime,
        val lastUpdate: LocalDateTime,
        val dateDue: LocalDateTime?,
        val notes: String,
        val labelId: Long?,
        val labelName: String?,
        val colorHex: String?,
        val useWhiteText: Boolean?,
        val useBorder: Boolean?
    )


}
