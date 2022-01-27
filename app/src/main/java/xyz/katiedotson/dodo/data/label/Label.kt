package xyz.katiedotson.dodo.data.label

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
)