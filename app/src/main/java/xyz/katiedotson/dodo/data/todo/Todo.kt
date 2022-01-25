package xyz.katiedotson.dodo.data.todo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var name: String,
    var dateCreated: LocalDateTime,
    var lastUpdate: LocalDateTime,
    var dateDue: LocalDate?
) {
    override fun toString() = name

    @Ignore
    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    @Ignore
    fun formattedDueDate(): String = if (dateDue != null) dateTimeFormatter.format(dateDue) else "No Due Date"

    @Ignore
    fun formattedLastUpdate(): String = dateTimeFormatter.format(lastUpdate)

    @Ignore
    fun formattedDateCreated(): String = dateTimeFormatter.format(dateCreated)

    @Ignore
    fun dueDateExists() = dateDue != null

}
