package xyz.katiedotson.dodo.ui.fragments.addedit

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.katiedotson.dodo.data.dto.DodoError
import xyz.katiedotson.dodo.data.label.LabelRepository
import xyz.katiedotson.dodo.data.todo.TodoDto
import xyz.katiedotson.dodo.data.todo.TodoRepository
import xyz.katiedotson.dodo.ui.base.BaseViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(private val repo: TodoRepository, private val labelsRepository: LabelRepository) : BaseViewModel() {

    private var isEdit = false

    private var todoId: Long = 0L
    private var name: String? = null
    private var dateCreated: LocalDateTime = LocalDateTime.now()
    private var lastUpdate: LocalDateTime = LocalDateTime.now()
    private var dueDate: LocalDateTime? = null
    private var labelColor: String? = null

    private val _viewState: MutableLiveData<AddEditViewState> = MutableLiveData<AddEditViewState>()
    val viewState: LiveData<AddEditViewState> get() = _viewState

    val labels = labelsRepository.labelsFlow.asLiveData()

    private fun currentTodo(): TodoDto {
        return TodoDto(
            id = todoId,
            name = name ?: "",
            dateDue = dueDate,
            lastUpdate = lastUpdate,
            dateCreated = dateCreated,
            labelColor = labelColor,
            labelName = null,
            useWhiteText = null,
            useBorder = null
        )
    }

    private fun setTodo(todoDto: TodoDto) {
        todoId = todoDto.id
        name = todoDto.name
        dateCreated = todoDto.dateCreated
        lastUpdate = todoDto.lastUpdate
        dueDate = todoDto.dateDue
        labelColor = todoDto.labelColor
    }

    fun loadTodo(todoId: Long) {
        viewModelScope.launch {
            kotlin.runCatching {
                if (todoId != 0L) {
                    isEdit = true
                    val todo = repo.readTodo(todoId).first()
                    setTodo(todo)
                    _viewState.value = AddEditViewState.InitialState(currentTodo())
                } else {
                    val todo = TodoDto(
                        id = 0,
                        name = "",
                        dateDue = null,
                        lastUpdate = LocalDateTime.now(),
                        dateCreated = LocalDateTime.now(),
                        labelColor = labelColor,
                        labelName = null,
                        useWhiteText = null,
                        useBorder = null
                    )
                    setTodo(todo)
                    _viewState.value = AddEditViewState.InitialState(currentTodo())
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
            dueDate = null
            _viewState.value = AddEditViewState.EditedState(currentTodo())
        } else {
            val date = Instant.ofEpochMilli(newDueDate).atZone(ZoneId.systemDefault()).toLocalDateTime()
            dueDate = date
            _viewState.value = AddEditViewState.EditedState(currentTodo())
        }
    }

    fun titleChanged(titleText: String) {
        name = titleText
        if (_viewState.value !is AddEditViewState.EditedState) {
            _viewState.value = AddEditViewState.EditedState(currentTodo())
        }
    }

    fun submit() {
        viewModelScope.launch {
            lastUpdate = LocalDateTime.now()
            kotlin.runCatching {
                if (isEdit) {
                    repo.updateTodo(currentTodo())
                } else {
                    repo.createTodo(currentTodo())
                }
            }.onSuccess {
                _viewState.value = AddEditViewState.SuccessState(currentTodo(), it)
            }.onFailure {
                Timber.e(it)
                _viewState.value = AddEditViewState.ErrorState(DodoError.DATABASE_ERROR)
            }
        }
    }

    fun checkedColorChanged(color: String?) {
        labelColor = "#$color"
    }

    sealed class AddEditViewState {
        data class ErrorState(val error: DodoError) : AddEditViewState()
        data class ValidState(val todo: TodoDto?) : AddEditViewState()
        data class InitialState(val todo: TodoDto?) : AddEditViewState()
        data class SuccessState(val todo: TodoDto, val id: Long) : AddEditViewState()
        data class EditedState(val todo: TodoDto?) : AddEditViewState()
    }

}