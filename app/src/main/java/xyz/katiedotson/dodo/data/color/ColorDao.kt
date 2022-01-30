package xyz.katiedotson.dodo.data.color

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorDao {

    @Query("SELECT * from colors ORDER BY id")
    fun getColorsFlow(): Flow<List<DodoColor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(colors: List<DodoColor>)

    @Query("DELETE FROM colors")
    suspend fun deleteAll()

}