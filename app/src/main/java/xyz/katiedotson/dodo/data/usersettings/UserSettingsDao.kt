package xyz.katiedotson.dodo.data.usersettings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM usersettings Where id = 1")
    fun select(): Flow<UserSettings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(userSettings: UserSettings)

}