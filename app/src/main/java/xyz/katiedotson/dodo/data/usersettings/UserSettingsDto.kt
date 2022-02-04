package xyz.katiedotson.dodo.data.usersettings

data class UserSettingsDto(
    val id: Long,
    val sortSetting: SortSetting,
    val allowFilteringByLabels: Boolean,
    val showDueDate: Boolean,
    val showLastUpdate: Boolean,
    val showDateCreated: Boolean,
    val showNotes: Boolean,
    val showLabel: Boolean
)