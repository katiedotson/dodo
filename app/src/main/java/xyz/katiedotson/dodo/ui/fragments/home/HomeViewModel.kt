package xyz.katiedotson.dodo.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.katiedotson.dodo.common.Event
import xyz.katiedotson.dodo.data.dto.DodoError
import xyz.katiedotson.dodo.data.label.LabelRepository
import xyz.katiedotson.dodo.data.todo.TodoDto
import xyz.katiedotson.dodo.data.todo.TodoRepository
import xyz.katiedotson.dodo.data.usersettings.SortSetting
import xyz.katiedotson.dodo.data.usersettings.UserSettingsDto
import xyz.katiedotson.dodo.data.usersettings.UserSettingsRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    labelRepository: LabelRepository,
    settingsRepository: UserSettingsRepository
) : ViewModel() {

    val todos: LiveData<List<TodoDto>> get() = _todos
    private val _todos: MutableLiveData<List<TodoDto>> = MutableLiveData()

    val labels = labelRepository.labelsFlow

    private var internalTodos: List<TodoDto> = listOf()
    private lateinit var settings: UserSettingsDto

    private var appliedLabelColor: String = ""
    private var appliedSearchString: String = ""

    init {
        viewModelScope.launch {
            settings = settingsRepository.userSettings.first()
            todoRepository.todosFlow.collect {
                _todos.value = it
                internalTodos = it
                filterAndSortTodos()
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
        filterAndSortTodos()
    }

    fun labelFilterChecked(selectedChipColor: String?) {
        appliedLabelColor = if (selectedChipColor == null) "" else "#$selectedChipColor"
        filterAndSortTodos()
    }

    private fun filterAndSortTodos() {
        var filtered = internalTodos
            .filter { appliedSearchString == "" || it.description.contains(appliedSearchString, true) }
            .filter { appliedLabelColor == "" || appliedLabelColor.equals(it.labelColor, true) }
            .sortedWith(
                compareBy { getComparator(it) }
            )
        if (settings.sortSetting == SortSetting.LastUpdate || settings.sortSetting == SortSetting.DueDate) {
            filtered = filtered.reversed().toList()
        }
        _todos.value = filtered
    }

    private fun getComparator(first: TodoDto): Comparable<*>? {
        return when (settings.sortSetting) {
            SortSetting.Alphabetical -> first.description
            SortSetting.DateCreated -> first.dateCreated
            SortSetting.DueDate -> first.dateDue
            SortSetting.LastUpdate -> first.lastUpdate
            else -> first.description
        }

    }

    fun todosExist(): Boolean {
        return internalTodos.isNotEmpty()
    }

    sealed class DeleteEvent {
        object Success : DeleteEvent()
        data class Failure(val error: DodoError) : DeleteEvent()
    }

}
