package xyz.katiedotson.dodo.data.color

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "colors")
data class DodoColor(
    @PrimaryKey(autoGenerate = false) val id: Long,
    var displayName: String,
    var hex: String,
    var useWhiteText: Boolean,
    var useBorder: Boolean
)