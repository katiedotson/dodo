package xyz.katiedotson.dodo.data.usersettings

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UserSettingsRepository @Inject constructor(private val userSettingsDao: UserSettingsDao) {

    val userSettings: Flow<UserSettings>
        get() = userSettingsDao.select()

    suspend fun insert(userSettings: UserSettings) {
        userSettingsDao.insertOne(userSettings)
    }

}