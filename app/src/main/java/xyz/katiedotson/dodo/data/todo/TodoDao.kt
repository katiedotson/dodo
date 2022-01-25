package xyz.katiedotson.dodo.data.todo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY name")
    fun getTodosFlow(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todo: List<Todo>)

    @Query("SELECT * FROM todos WHERE id = :id")
    fun readSingleTodo(id: Long): Flow<Todo>

    @Update
    suspend fun updateTodo(todo: Todo)

    @Insert
    suspend fun insertOne(todo: Todo): Long

    @Delete
    suspend fun delete(todo: Todo)

}