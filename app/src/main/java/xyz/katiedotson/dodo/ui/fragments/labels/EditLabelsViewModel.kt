package xyz.katiedotson.dodo.ui.fragments.labels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.katiedotson.dodo.common.Event
import xyz.katiedotson.dodo.data.color.ColorRepository
import xyz.katiedotson.dodo.data.color.DodoColor
import xyz.katiedotson.dodo.data.dto.DodoError
import xyz.katiedotson.dodo.data.label.LabelDto
import xyz.katiedotson.dodo.data.label.LabelRepository
import xyz.katiedotson.dodo.ui.base.BaseViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditLabelsViewModel @Inject constructor(private val labelRepository: LabelRepository, private val colorRepository: ColorRepository) :
    BaseViewModel() {

    val labels: LiveData<List<LabelDto>> = labelRepository.labelsFlow.asLiveData()
    val colors: LiveData<List<DodoColor>> = colorRepository.colors.asLiveData()

    private val _labelCreatedEvent: MutableLiveData<Event<LabelCreatedEvent>> = MutableLiveData()
    val labelCreatedEvent get() = _labelCreatedEvent

    private val _viewState: MutableLiveData<EditLabelsViewState> = MutableLiveData()
    val viewState get() = _viewState

    private var labelColor: String? = null
    private var labelName: String = ""
    private var labelId: Long = 0
    private var labelCreatedDate = LocalDateTime.now()

    fun saveNewLabel() {
        viewModelScope.launch {
            kotlin.runCatching {
                val label = LabelDto(
                    labelId = labelId,
                    labelName = labelName,
                    colorHex = "#" + labelColor!!,
                    useWhiteText = null, useBorder = null,
                    dateCreated = labelCreatedDate,
                    lastUpdate = LocalDateTime.now()
                )
                if (_viewState.value is EditLabelsViewState.EditLabel) {
                    labelRepository.updateLabel(label)
                } else {
                    labelRepository.createLabel(label)
                }
            }.onSuccess {
                _labelCreatedEvent.value = Event(LabelCreatedEvent.Success)
            }.onFailure {
                _labelCreatedEvent.value = Event(LabelCreatedEvent.Failure(DodoError.DATABASE_ERROR))
            }
            _viewState.value = EditLabelsViewState.NewLabel
        }
    }

    fun clearNewLabel() {
        labelName = ""
        labelId = 0L
        labelCreatedDate = LocalDateTime.now()
        _viewState.value = EditLabelsViewState.NewLabel
    }

    fun nameFieldChanged(name: String) {
        labelName = name
    }

    fun checkedColorChanged(color: String?) {
        labelColor = color
    }

    fun labelSelectedForEdit(label: LabelDto) {
        labelColor = label.colorHex
        labelName = label.labelName
        labelId = label.labelId
        labelCreatedDate = label.dateCreated
        _viewState.value = EditLabelsViewState.EditLabel
    }

    sealed class LabelCreatedEvent {
        object Success : LabelCreatedEvent()
        data class Failure(val error: DodoError) : LabelCreatedEvent()
    }

    sealed class EditLabelsViewState {
        object NewLabel : EditLabelsViewState()
        object EditLabel : EditLabelsViewState()
    }

}