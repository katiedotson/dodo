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

    @Query("SELECT * FROM WithLabel")
    fun getCompleteTodosFlow(): Flow<List<Todo.WithLabel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todo: List<Todo>)

    @Query("SELECT * FROM WithLabel WHERE todoId = :id")
    fun readSingleTodo(id: Long): Flow<Todo.WithLabel>

    @Update
    suspend fun updateTodo(todo: Todo)

    @Insert
    suspend fun insertOne(todo: Todo): Long

    @Delete
    suspend fun delete(todo: Todo)

}