package xyz.katiedotson.dodo.data.todo

import xyz.katiedotson.dodo.data.label.LabelDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class TodoDto(
    val id: Long,
    val name: String,
    val dateDue: LocalDateTime?,
    val lastUpdate: LocalDateTime,
    val dateCreated: LocalDateTime,
    val labelId: Long?,
    val labelColor: String?,
    val labelName: String?,
    val useWhiteText: Boolean?,
    val useBorder: Boolean?
) {

    val labelDto: LabelDto? = if (labelName != null && labelColor != null) LabelDto(
        labelId = 0,
        labelName = labelName,
        colorHex = labelColor,
        useWhiteText = useWhiteText,
        useBorder = useBorder,
        dateCreated = LocalDateTime.now(),
        lastUpdate = LocalDateTime.now()
    ) else null

    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    fun formattedDueDate(): String = if (dateDue != null) dateTimeFormatter.format(dateDue) else "No Due Date"

    fun formattedLastUpdate(): String = dateTimeFormatter.format(lastUpdate)

    fun formattedDateCreated(): String = dateTimeFormatter.format(dateCreated)

    fun dueDateExists() = dateDue != null
}