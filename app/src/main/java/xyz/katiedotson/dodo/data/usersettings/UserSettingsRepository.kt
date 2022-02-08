package xyz.katiedotson.dodo.data.usersettings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class UserSettingsRepository @Inject constructor(private val userSettingsDao: UserSettingsDao) {

    val userSettings: Flow<UserSettingsDto>
        get() = userSettingsDao.select().map { settings ->
            toDto(settings)
        }

    suspend fun insert(userSettings: UserSettings) {
        userSettingsDao.insertOne(userSettings)
    }

    suspend fun update(userSettingsDto: UserSettingsDto) {
        userSettingsDao.update(toModel(settingsDto = userSettingsDto))
    }

    private fun toDto(settings: UserSettings): UserSettingsDto {
        return UserSettingsDto(
            settings.id,
            SortSetting.valueOf(settings.sort),
            allowFilteringByLabels = settings.allowFilteringByLabels,
            showDueDate = settings.showDueDate,
            showLastUpdate = settings.showLastUpdate,
            showDateCreated = settings.showDateCreated,
            showNotes = settings.showNotes,
            showLabel = settings.showLabel
        )
    }

    private fun toModel(settingsDto: UserSettingsDto): UserSettings {
        return UserSettings(
            id = settingsDto.id,
            sort = settingsDto.sortSetting.name,
            allowFilteringByLabels = settingsDto.allowFilteringByLabels,
            showDueDate = settingsDto.showDueDate,
            showLastUpdate = settingsDto.showLastUpdate,
            showDateCreated = settingsDto.showDateCreated,
            showNotes = settingsDto.showNotes,
            showLabel = settingsDto.showLabel
        )
    }

}