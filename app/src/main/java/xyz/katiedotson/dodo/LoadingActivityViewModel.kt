package xyz.katiedotson.dodo

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.katiedotson.dodo.common.Event
import xyz.katiedotson.dodo.data.color.ColorRepository
import xyz.katiedotson.dodo.data.color.DodoColor
import xyz.katiedotson.dodo.data.usersettings.SortSetting
import xyz.katiedotson.dodo.data.usersettings.UserSettings
import xyz.katiedotson.dodo.data.usersettings.UserSettingsRepository
import javax.inject.Inject

@HiltViewModel
class LoadingActivityViewModel @Inject constructor(
    private val colorRepository: ColorRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        const val COLORS_LOADED = "colors"
        const val USER_SETTINGS_LOADED = "user_settings"
    }

    private val _colorsEvent = MutableLiveData<Event<ColorsLoadEvent>>()
    private val _userSettingsEvent = MutableLiveData<Event<UserSettingsLoadEvent>>()

    val mediatorLiveData = MediatorLiveData<LoadingState>().apply {
        this.value = LoadingState(colorsHandled = false, userSettingsHandled = false)
        addSource(_colorsEvent) {
            this.value = this.value?.copy(colorsHandled = true)
        }
        addSource(_userSettingsEvent) {
            this.value = this.value?.copy(userSettingsHandled = true)
        }
    }

    init {
        if (!sharedPreferences.contains(COLORS_LOADED)) {
            initColors()
        } else if (sharedPreferences.getBoolean(COLORS_LOADED, true)) {
            _colorsEvent.value = Event(ColorsLoadEvent.Success)
        }

        if (!sharedPreferences.contains(USER_SETTINGS_LOADED)) {
            initUserSettings()
        } else if (sharedPreferences.getBoolean(USER_SETTINGS_LOADED, true)) {
            _userSettingsEvent.value = Event(UserSettingsLoadEvent.Success)
        }
    }

    private fun initColors() {
        val colors = listOf(
            DodoColor(id = 1L, displayName = "Pure White", hex = "#FFFFFFFF", useWhiteText = false, useBorder = true),
            DodoColor(id = 2L, displayName = "Pure Black", hex = "#FF000000", useWhiteText = true, useBorder = false),
            DodoColor(id = 3L, displayName = "Navajo White", hex = "#FFF9DDAB", useWhiteText = false, useBorder = false),
            DodoColor(id = 4L, displayName = "Chrome Yellow", hex = "#FFF9A706", useWhiteText = false, useBorder = false),
            DodoColor(id = 5L, displayName = "Orange Blaze", hex = "#FFFF6704", useWhiteText = false, useBorder = false),
            DodoColor(id = 6L, displayName = "Candy Apple", hex = "#FFE7062E", useWhiteText = true, useBorder = false),
            DodoColor(id = 7L, displayName = "Purple Pink", hex = "#FFA2217E", useWhiteText = true, useBorder = false),
            DodoColor(id = 8L, displayName = "Blue Violet", hex = "#FF511296", useWhiteText = true, useBorder = false),
            DodoColor(id = 9L, displayName = "Midnight Blue", hex = "#FF020759", useWhiteText = true, useBorder = false),
            DodoColor(id = 10L, displayName = "Cerulean Blue", hex = "#FF114FC6", useWhiteText = true, useBorder = false),
            DodoColor(id = 11L, displayName = "Sea Green", hex = "#FF038C5E", useWhiteText = true, useBorder = false),
            DodoColor(id = 12L, displayName = "Mantis Green", hex = "#FF7FC24F", useWhiteText = false, useBorder = false)
        )

        viewModelScope.launch {
            kotlin.runCatching {
                colorRepository.deleteAll()
                colorRepository.insertColors(colors)
            }.onFailure {
                Timber.e(it)
                Timber.e("failed to load colors")
                _colorsEvent.value = Event(ColorsLoadEvent.Failure)
            }.onSuccess {
                Timber.e("colors added to database")
                _colorsEvent.value = Event(ColorsLoadEvent.Success)
                sharedPreferences.edit().putBoolean(COLORS_LOADED, true).apply()
            }
        }

    }

    private fun initUserSettings() {
        viewModelScope.launch {
            kotlin.runCatching {
                val userSettings = UserSettings(
                    id = 0L,
                    sort = SortSetting.Alphabetical.name,
                    allowFilteringByLabels = true,
                    showDueDate = true,
                    showLastUpdate = false,
                    showDateCreated = false,
                    showNotes = false,
                    showLabel = true
                )
                userSettingsRepository.insert(userSettings)
            }.onFailure {
                Timber.e(it)
                Timber.e("failed to load user")
                _userSettingsEvent.value = Event(UserSettingsLoadEvent.Failure)
            }.onSuccess {
                Timber.e("user added to db")
                _userSettingsEvent.value = Event(UserSettingsLoadEvent.Success)
                sharedPreferences.edit().putBoolean(USER_SETTINGS_LOADED, true).apply()
            }
        }
    }

    sealed class ColorsLoadEvent {
        object Success : ColorsLoadEvent()
        object Failure : ColorsLoadEvent()
    }

    sealed class UserSettingsLoadEvent {
        object Success : UserSettingsLoadEvent()
        object Failure : UserSettingsLoadEvent()
    }

    data class LoadingState(val colorsHandled: Boolean, val userSettingsHandled: Boolean)

}