package xyz.katiedotson.dodo.data.todo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject


class TodoRepository @Inject constructor(private val todoDao: TodoDao) {

    val todosFlow: Flow<List<TodoDto>>
        get() = todoDao.getCompleteTodosFlow().transform{
            emit(it.map { todo ->
                toDto(todo)
            })
        }

    fun readTodo(id: Long): Flow<TodoDto> {
        return todoDao.readSingleTodo(id).map {
            toDto(it)
        }
    }

    suspend fun createTodo(dto: TodoDto): Long {
        val todo = toTodo(dto)
        return todoDao.insertOne(todo)
    }

    suspend fun updateTodo(dto: TodoDto): Long {
        val todo = toTodo(dto)
        todoDao.updateTodo(todo)
        return todo.id
    }

    suspend fun deleteTodo(dto: TodoDto) {
        val todo = toTodo(dto)
        todoDao.delete(todo)
    }

    private fun toDto(it: Todo.WithLabel): TodoDto {
        return TodoDto(
            id = it.todoId,
            name = it.todoName,
            dateDue = it.dateDue,
            lastUpdate = it.lastUpdate,
            dateCreated = it.dateCreated,
            labelId = it.labelId,
            labelColor = it.colorHex,
            labelName = it.labelName,
            useBorder = it.useBorder,
            useWhiteText = it.useWhiteText
        )
    }

    private fun toTodo(dto: TodoDto): Todo {
        return Todo(
            dto.id,
            dto.name,
            dto.dateCreated,
            dto.lastUpdate,
            dto.dateDue,
            dto.labelId
        )
    }

}