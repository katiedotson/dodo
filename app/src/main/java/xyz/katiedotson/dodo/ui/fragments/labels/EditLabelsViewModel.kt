package xyz.katiedotson.dodo.ui.fragments.labels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.katiedotson.dodo.common.Event
import xyz.katiedotson.dodo.data.label.Label
import xyz.katiedotson.dodo.data.label.LabelRepository
import xyz.katiedotson.dodo.ui.base.BaseViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditLabelsViewModel @Inject constructor(private val labelRepository: LabelRepository) :
    BaseViewModel() {

    val labels: LiveData<List<Label>> = labelRepository.labelsFlow.asLiveData()

    private val _labelCreatedEvent: MutableLiveData<Event<LabelCreatedEvent>> = MutableLiveData()
    val labelCreatedEvent get() = _labelCreatedEvent

    private val _newLabelClearedEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val newLabelClearedEvent get() = _newLabelClearedEvent

    private val _viewState: MutableLiveData<EditLabelsViewState> = MutableLiveData()
    val viewState get() = _viewState

    private var _labelColor: Int? = null
    private var _labelName: String = ""
    private var _labelId: Long = 0
    private var _labelCreatedDate = LocalDateTime.now()

    fun saveNewLabel() {
        viewModelScope.launch {
            kotlin.runCatching {
                val label = Label(
                    id = _labelId,
                    name = _labelName,
                    color = _labelColor!!,
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
                _labelCreatedEvent.value = Event(LabelCreatedEvent.Failure)
            }
            _viewState.value = EditLabelsViewState.NewLabel
        }
    }

    fun clearNewLabel() {
        _labelName = ""
        _labelColor = 0
        _newLabelClearedEvent.value = Event(true)
        _viewState.value = EditLabelsViewState.NewLabel
    }

    fun nameFieldChanged(name: String) {
        _labelName = name
    }

    fun checkedColorChanged(color: Int?) {
        _labelColor = color
    }

    fun labelSelectedForEdit(label: Label) {
        _labelColor = label.color
        _labelName = label.name
        _labelId = label.id
        _labelCreatedDate = label.dateCreated
        _viewState.value = EditLabelsViewState.EditLabel
    }

    fun formCleared() {
        _viewState.value = EditLabelsViewState.NewLabel
    }

    sealed class LabelCreatedEvent {
        object Success : LabelCreatedEvent()
        object Failure : LabelCreatedEvent()
    }

    sealed class EditLabelsViewState {
        object NewLabel : EditLabelsViewState()
        object EditLabel : EditLabelsViewState()
    }


}