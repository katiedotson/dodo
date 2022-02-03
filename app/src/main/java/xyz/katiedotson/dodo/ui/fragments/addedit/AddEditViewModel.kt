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
import xyz.katiedotson.dodo.common.DodoFieldError
import xyz.katiedotson.dodo.common.FieldValidator
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
class AddEditViewModel @Inject constructor(
    private val repo: TodoRepository,
    labelsRepository: LabelRepository,
    private val fieldValidator: FieldValidator
) : BaseViewModel() {

    private var isEdit = false

    private var todoId: Long = 0L
    private var description: String? = null
    private var dateCreated: LocalDateTime = LocalDateTime.now()
    private var lastUpdate: LocalDateTime = LocalDateTime.now()
    private var dueDate: LocalDateTime? = null
    private var labelId: Long? = null
    private var notes: String = ""

    private val _viewState: MutableLiveData<AddEditViewState> = MutableLiveData<AddEditViewState>()
    val viewState: LiveData<AddEditViewState> get() = _viewState

    private val _validationState = MutableLiveData(Validation(true, null))
    val validationState: LiveData<Validation> get() = _validationState

    val labels = labelsRepository.labelsFlow.asLiveData()

    private fun currentTodo(): TodoDto {
        return TodoDto(
            id = todoId,
            description = description ?: "",
            dateDue = dueDate,
            lastUpdate = lastUpdate,
            dateCreated = dateCreated,
            notes = notes,
            labelId = labelId,
            labelColor = null,
            labelName = null,
            useWhiteText = null,
            useBorder = null
        )
    }

    private fun setTodo(todoDto: TodoDto) {
        todoId = todoDto.id
        description = todoDto.description
        notes = todoDto.notes
        dateCreated = todoDto.dateCreated
        lastUpdate = todoDto.lastUpdate
        dueDate = todoDto.dateDue
        labelId = todoDto.labelId
    }

    fun loadTodo(todoId: Long) {
        viewModelScope.launch {
            kotlin.runCatching {
                if (todoId != 0L) {
                    isEdit = true
                    val todo = repo.readTodo(todoId).first()
                    setTodo(todo)
                    _viewState.value = AddEditViewState.InitialState(currentTodo())
                }
            }.onFailure {
                Timber.e(it)
                _viewState.value = AddEditViewState.ErrorState(DodoError.DATABASE_ERROR)
            }
        }
    }

    fun submit() {
        val validation = validate()
        if (validation.passed) {
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
        } else {
            _validationState.value = validation
        }
    }

    private fun validate(): Validation {
        val descriptionError = fieldValidator.validateNotEmpty(description)
        return Validation(passed = (descriptionError == null), descriptionError)
    }

    fun descriptionChanged(descriptionText: String) {
        description = descriptionText
        if (_viewState.value !is AddEditViewState.EditedState) {
            _viewState.value = AddEditViewState.EditedState(currentTodo())
        } else {
            _validationState.value = validate()
        }
    }

    fun notesChanged(notesText: String) {
        notes = notesText
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

    fun checkedLabelChanged(labelId: Long?) {
        this.labelId = labelId
    }

    sealed class AddEditViewState {
        data class ErrorState(val error: DodoError) : AddEditViewState()
        data class InitialState(val todo: TodoDto?) : AddEditViewState()
        data class SuccessState(val todo: TodoDto, val id: Long) : AddEditViewState()
        data class EditedState(val todo: TodoDto?) : AddEditViewState()
    }

    data class Validation(val passed: Boolean, val descriptionValidation: DodoFieldError?)

}