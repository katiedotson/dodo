package xyz.katiedotson.dodo.data.todo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import javax.inject.Inject


class TodoRepository @Inject constructor(private val todoDao: TodoDao) {

    val todosFlow: Flow<List<Todo>>
        get() = todoDao.getTodosFlow()

    fun readTodo(id: Long): Flow<Todo> {
        return todoDao.readSingleTodo(id)
    }

    suspend fun createTodo(todo: Todo): Long {
        return todoDao.insertOne(todo)
    }

    suspend fun updateTodo(todo: Todo): Long {
        todoDao.updateTodo(todo)
        return todo.id
    }

    suspend fun deleteTodo(todo: Todo) {
        todoDao.delete(todo)
    }

}