package xyz.katiedotson.dodo.data.label

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "labels")
data class Label(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var name: String,
    var colorHex: String,
    var dateCreated: LocalDateTime,
    var lastUpdate: LocalDateTime
) {
    override fun toString() = name

    @DatabaseView(
        "SELECT " +
                "label.id as labelId, " +
                "label.name as labelName, " +
                "label.dateCreated, " +
                "label.lastUpdate, " +
                "color.hex as colorHex, " +
                "color.useBorder, " +
                "color.useWhiteText " +
                "FROM labels as label " +
                "JOIN colors as color ON upper(label.colorHex) = upper(color.hex) "
    )
    data class WithColor(
        val labelId: Long,
        val labelName: String,
        val dateCreated: LocalDateTime,
        val lastUpdate: LocalDateTime,
        val colorHex: String,
        val useWhiteText: Boolean,
        val useBorder: Boolean
    )

}