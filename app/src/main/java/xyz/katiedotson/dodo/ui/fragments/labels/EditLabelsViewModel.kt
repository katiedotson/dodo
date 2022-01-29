package xyz.katiedotson.dodo.ui.fragments.labels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.katiedotson.dodo.common.Event
import xyz.katiedotson.dodo.data.color.DodoColor
import xyz.katiedotson.dodo.data.color.ColorRepository
import xyz.katiedotson.dodo.data.dto.DodoError
import xyz.katiedotson.dodo.data.label.Label
import xyz.katiedotson.dodo.data.label.LabelRepository
import xyz.katiedotson.dodo.ui.base.BaseViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditLabelsViewModel @Inject constructor(private val labelRepository: LabelRepository, private val colorRepository: ColorRepository) :
    BaseViewModel() {

    val labels: LiveData<List<Label>> = labelRepository.labelsFlow.asLiveData()
    val colors: LiveData<List<DodoColor>> = colorRepository.colors.asLiveData()

    val mediator: MediatorLiveData<AdapterState> = MediatorLiveData<AdapterState>().apply {
        addSource(labels) {
            value = AdapterState(colors.value, labels.value)
        }
        addSource(colors) {
            value = AdapterState(colors.value, labels.value)
        }
    }

    private val _labelCreatedEvent: MutableLiveData<Event<LabelCreatedEvent>> = MutableLiveData()
    val labelCreatedEvent get() = _labelCreatedEvent

    private val _viewState: MutableLiveData<EditLabelsViewState> = MutableLiveData()
    val viewState get() = _viewState

    private var _labelColor: String? = null
    private var _labelName: String = ""
    private var _labelId: Long = 0
    private var _labelCreatedDate = LocalDateTime.now()

    fun saveNewLabel() {
        viewModelScope.launch {
            kotlin.runCatching {
                val label = Label(
                    id = _labelId,
                    name = _labelName,
                    colorHex = "#" + _labelColor!!,
                    dateCreated = _labelCreatedDate,
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
        _labelName = ""
        _labelId = 0L
        _labelCreatedDate = LocalDateTime.now()
        _viewState.value = EditLabelsViewState.NewLabel
    }

    fun nameFieldChanged(name: String) {
        _labelName = name
    }

    fun checkedColorChanged(color: String?) {
        _labelColor = color
    }

    fun labelSelectedForEdit(label: Label) {
        _labelColor = label.colorHex
        _labelName = label.name
        _labelId = label.id
        _labelCreatedDate = label.dateCreated
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

    data class AdapterState(val colors: List<DodoColor>?, val labels: List<Label>?)


}