package xyz.katiedotson.dodo.ui.fragments.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.katiedotson.dodo.common.Event
import xyz.katiedotson.dodo.data.usersettings.SortSetting
import xyz.katiedotson.dodo.data.usersettings.UserSettingsDto
import xyz.katiedotson.dodo.data.usersettings.UserSettingsRepository
import xyz.katiedotson.dodo.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val userSettingsRepository: UserSettingsRepository) : BaseViewModel() {

    private var id: Long = 0
    private var sortSetting: SortSetting = SortSetting.Alphabetical
    private var allowFilteringByLabels = true
    private var showDueDate = true
    private var showLastUpdate = false
    private var showDateCreated = false
    private var showLabel = true
    private var showNotes = false

    private val _userSettings = MutableLiveData<UserSettingsDto>()
    val userSettings: LiveData<UserSettingsDto> get() = _userSettings

    private val _settingsUpdateEvent = MutableLiveData<Event<SettingsUpdateEvent>>()
    val settingsUpdateEvent: LiveData<Event<SettingsUpdateEvent>> get() = _settingsUpdateEvent

    init {
        viewModelScope.launch {
            userSettingsRepository.userSettings.collect {
                _userSettings.value = it
                id = it.id
                sortSetting = it.sortSetting
                allowFilteringByLabels = it.allowFilteringByLabels
                showDueDate = it.showDueDate
                showLastUpdate = it.showLastUpdate
                showDateCreated = it.showDateCreated
                showNotes = it.showNotes
                showLabel = it.showLabel
            }
        }
    }

    private fun trySave() {
        viewModelScope.launch {
            kotlin.runCatching {
                userSettingsRepository.update(
                    UserSettingsDto(
                        id = id,
                        sortSetting = sortSetting,
                        allowFilteringByLabels = allowFilteringByLabels,
                        showDueDate = showDueDate,
                        showLastUpdate = showLastUpdate,
                        showDateCreated = showDateCreated,
                        showNotes = showNotes,
                        showLabel = showLabel
                    )
                )
            }.onFailure {
                _settingsUpdateEvent.value = Event(SettingsUpdateEvent.Failure)
            }.onSuccess {
                _settingsUpdateEvent.value = Event(SettingsUpdateEvent.Success)
            }
        }
    }

    fun onSortSettingChanged(sortSetting: SortSetting) {
        this.sortSetting = sortSetting
        trySave()
    }

    fun onAllowFilterByLabelsChanged(filterByLabels: Boolean) {
        allowFilteringByLabels = filterByLabels
        trySave()
    }

    fun onShowDueDateChanged(showDueDate: Boolean) {
        this.showDueDate = showDueDate
        trySave()
    }

    fun onShowLastUpdateChanged(showLastUpdate: Boolean) {
        this.showLastUpdate = showLastUpdate
        trySave()
    }

    fun onShowDateCreated(showDateCreated: Boolean) {
        this.showDateCreated = showDateCreated
        trySave()
    }

    fun onShowNotesChanged(showNotes: Boolean) {
        this.showNotes = showNotes
        trySave()
    }

    fun onShowLabelChanged(showLabel: Boolean) {
        this.showLabel = showLabel
        trySave()
    }

    sealed class SettingsUpdateEvent {
        object Success : SettingsUpdateEvent()
        object Failure : SettingsUpdateEvent()
    }

}