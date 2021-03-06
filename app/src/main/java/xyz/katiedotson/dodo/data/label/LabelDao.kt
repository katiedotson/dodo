package xyz.katiedotson.dodo.data.label

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {

    @Query("SELECT * from WithColor ORDER BY labelId")
    fun getLabelsFlow(): Flow<List<Label.WithColor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todo: List<Label>)

    @Query("SELECT * FROM WithColor WHERE labelId = :id")
    fun readSingleLabel(id: Long): Flow<Label.WithColor>

    @Update
    suspend fun updateLabel(label: Label)

    @Insert
    suspend fun insertOne(label: Label): Long

    @Delete
    suspend fun delete(label: Label)

}