package xyz.katiedotson.dodo.data.usersettings

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM usersettings Where id = 0")
    fun select(): Flow<UserSettings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(userSettings: UserSettings)

    @Update
    suspend fun update(toModel: UserSettings)

}