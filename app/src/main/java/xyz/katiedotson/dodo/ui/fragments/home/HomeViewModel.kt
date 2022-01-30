package xyz.katiedotson.dodo.ui.fragments.home

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.katiedotson.dodo.common.Event
import xyz.katiedotson.dodo.data.dto.DodoError
import xyz.katiedotson.dodo.data.todo.TodoDto
import xyz.katiedotson.dodo.data.todo.TodoRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val todoRepository: TodoRepository) : ViewModel() {

    val todos = todoRepository.todosFlow

    private val _deleteEvent: MutableLiveData<Event<DeleteEvent>> = MutableLiveData()
    val deleteEvent: LiveData<Event<DeleteEvent>> get() = _deleteEvent

    fun deleteTodo(todo: TodoDto) {
        viewModelScope.launch {
            kotlin.runCatching {
                todoRepository.deleteTodo(todo)
            }.onFailure {
                Timber.e(it)
                _deleteEvent.value = Event(DeleteEvent.Failure(DodoError.DATABASE_ERROR))
            }.onSuccess {
                _deleteEvent.value = Event(DeleteEvent.Success)
            }
        }
    }

    sealed class DeleteEvent {
        object Success : DeleteEvent()
        data class Failure(val error: DodoError) : DeleteEvent()
    }

}