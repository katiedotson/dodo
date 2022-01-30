package xyz.katiedotson.dodo.data.todo

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val dateCreated: LocalDateTime,
    val lastUpdate: LocalDateTime,
    val dateDue: LocalDateTime?,
    val labelColor: String?
) {
    override fun toString() = name

    @DatabaseView(
        "SELECT " +
                "todo.id as todoId, " +
                "todo.name as todoName, " +
                "todo.dateCreated, " +
                "todo.lastUpdate, " +
                "todo.dateDue, " +
                "label.name as labelName, " +
                "label.colorHex, " +
                "color.useBorder, " +
                "color.useWhiteText " +
                "FROM todos as todo  " +
                "LEFT OUTER JOIN labels as label ON upper(todo.labelColor) = upper(label.colorHex) " +
                "LEFT OUTER JOIN colors as color ON upper(color.hex) = upper(label.colorHex) "
    )
    data class WithLabel(
        val todoId: Long,
        val todoName: String,
        val dateCreated: LocalDateTime,
        val lastUpdate: LocalDateTime,
        val dateDue: LocalDateTime?,
        val labelId: Long?,
        val labelName: String?,
        val colorHex: String?,
        val useWhiteText: Boolean?,
        val useBorder: Boolean?
    )


}
