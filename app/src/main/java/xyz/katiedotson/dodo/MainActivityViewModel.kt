package xyz.katiedotson.dodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.katiedotson.dodo.data.color.ColorRepository
import xyz.katiedotson.dodo.data.color.DodoColor
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val colorRepository: ColorRepository) : ViewModel() {

    fun initColors() {
        val colors = listOf(
            DodoColor(id = 1L, displayName = "Pure White", hex = "#FFFFFFFF", useWhiteText = false, useBorder = true),
            DodoColor(id = 2L, displayName = "Pure Black", hex = "#FF000000", useWhiteText = true, useBorder = false),
            DodoColor(id = 3L, displayName = "Navajo White", hex = "#FFF9DDAB", useWhiteText = false, useBorder = false),
            DodoColor(id = 4L, displayName = "Chrome Yellow", hex = "#FFF9A706", useWhiteText = false, useBorder = false),
            DodoColor(id = 5L, displayName = "Orange Blaze", hex = "#FFFF6704", useWhiteText = false, false),
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
            }.onSuccess {
                Timber.e("colors added to database")
            }
        }

    }


}