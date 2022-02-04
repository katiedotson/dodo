package xyz.katiedotson.dodo.data.usersettings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usersettings")
data class UserSettings(
    @PrimaryKey(autoGenerate = false) val id: Long,
    val sort: String,
    val allowFilteringByLabels: Boolean,
    val showDueDate: Boolean,
    val showLastUpdate: Boolean,
    val showDateCreated: Boolean,
    val showNotes: Boolean
)