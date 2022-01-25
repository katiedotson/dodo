package xyz.katiedotson.dodo.ui.fragments.addedit

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.katiedotson.dodo.ui.base.BaseViewModel
import xyz.katiedotson.dodo.data.dto.DodoError
import xyz.katiedotson.dodo.data.todo.Todo
import xyz.katiedotson.dodo.data.todo.TodoRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(private val repo: TodoRepository) : BaseViewModel() {

    private var isEdit = false

    private var _todo: Todo? = null

    fun dueDate() = _todo?.dateDue

    private val _viewState: MutableLiveData<AddEditViewState> = MutableLiveData<AddEditViewState>()
    val viewState: LiveData<AddEditViewState> get() = _viewState

    fun loadTodo(todoId: Long) {
        viewModelScope.launch {
            kotlin.runCatching {
                if (todoId != 0L) {
                    isEdit = true
                    _todo = repo.readTodo(todoId).first()
                    _viewState.value = AddEditViewState.InitialState(_todo)
                } else {
                    _todo = Todo(
                        id = 0,
                        name = "",
                        dateCreated = LocalDateTime.now(),
                        lastUpdate = LocalDateTime.now(),
                        dateDue = null
                    )
                    _viewState.value = AddEditViewState.InitialState(_todo)
                }
            }.onFailure {
                Timber.e(it)
                _viewState.value = AddEditViewState.ErrorState(DodoError.DATABASE_ERROR)
            }
        }
    }

    @SuppressLint("NewApi")
    fun dueDateChanged(newDueDate: Long?) {
        if (newDueDate == null) {
            _todo?.dateDue = null
            _viewState.value = AddEditViewState.EditedState(_todo)
        } else {
            val date = Instant.ofEpochMilli(newDueDate).atZone(ZoneId.systemDefault()).toLocalDate()
            _todo?.dateDue = date
            _viewState.value = AddEditViewState.EditedState(_todo)
        }
    }

    fun titleChanged(titleText: String) {
        _todo?.name = titleText
        if (_viewState.value !is AddEditViewState.EditedState) {
            _viewState.value = AddEditViewState.EditedState(_todo)
        }
    }

    fun submit() {
        viewModelScope.launch {
            _todo?.lastUpdate = LocalDateTime.now()
            kotlin.runCatching {
                if (isEdit) {
                    repo.updateTodo(_todo!!)
                } else {
                    repo.createTodo(_todo!!)
                }
            }.onSuccess { id ->
                _viewState.value = AddEditViewState.SuccessState(_todo!!, id)
            }.onFailure {
                Timber.e(it)
                _viewState.value = AddEditViewState.ErrorState(DodoError.DATABASE_ERROR)
            }
        }
    }

    sealed class AddEditViewState {
        data class ErrorState(val error: DodoError) : AddEditViewState()
        data class ValidState(val todo: Todo?) : AddEditViewState()
        data class InitialState(val todo: Todo?) : AddEditViewState()
        data class SuccessState(val todo: Todo, val id: Long) : AddEditViewState()
        data class EditedState(val todo: Todo?) : AddEditViewState()
    }

}