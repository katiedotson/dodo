package xyz.katiedotson.dodo.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.katiedotson.dodo.common.Event
import xyz.katiedotson.dodo.data.dto.DodoError
import xyz.katiedotson.dodo.data.label.LabelRepository
import xyz.katiedotson.dodo.data.todo.TodoDto
import xyz.katiedotson.dodo.data.todo.TodoRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val todoRepository: TodoRepository, labelRepository: LabelRepository) : ViewModel() {

    val todos: LiveData<List<TodoDto>> get() = _todos
    private val _todos: MutableLiveData<List<TodoDto>> = MutableLiveData()

    val labels = labelRepository.labelsFlow

    private var internalTodos: List<TodoDto>? = null

    private var appliedLabelColor: String = ""
    private var appliedSearchString: String = ""

    init {
        viewModelScope.launch {
            todoRepository.todosFlow.collect {
                _todos.value = it
                internalTodos = it
            }
        }
    }

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

    fun searchTextChanged(text: CharSequence?) {
        appliedSearchString = text?.toString() ?: ""
        filterTodos()
    }

    fun labelFilterChecked(selectedChipColor: String?) {
        appliedLabelColor = if (selectedChipColor == null) "" else "#$selectedChipColor"
        filterTodos()
    }

    private fun filterTodos() {
        val filtered = internalTodos
            ?.filter { appliedSearchString == "" || it.name.startsWith(appliedSearchString) }
            ?.filter { appliedLabelColor == "" || appliedLabelColor.equals(it.labelColor, true) }
            ?: internalTodos
        _todos.value = filtered ?: listOf()
    }


    sealed class DeleteEvent {
        object Success : DeleteEvent()
        data class Failure(val error: DodoError) : DeleteEvent()
    }

}
