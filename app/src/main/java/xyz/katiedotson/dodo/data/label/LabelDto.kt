package xyz.katiedotson.dodo.data.label

import java.time.LocalDateTime

data class LabelDto(
    val labelId: Long,
    val labelName: String,
    val colorHex: String,
    val useWhiteText: Boolean?,
    val useBorder: Boolean?,
    val dateCreated: LocalDateTime,
    val lastUpdate: LocalDateTime
)