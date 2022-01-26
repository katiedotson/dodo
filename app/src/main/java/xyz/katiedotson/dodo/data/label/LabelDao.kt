package xyz.katiedotson.dodo.data.label

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {

    @Query("SELECT * from labels ORDER BY id")
    fun getLabelsFlow(): Flow<List<Label>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todo: List<Label>)

    @Query("SELECT * FROM labels WHERE id = :id")
    fun readSingleLabel(id: Long): Flow<Label>

    @Update
    suspend fun updateLabel(label: Label)

    @Insert
    suspend fun insertOne(label: Label): Long

    @Delete
    suspend fun delete(label: Label)

}