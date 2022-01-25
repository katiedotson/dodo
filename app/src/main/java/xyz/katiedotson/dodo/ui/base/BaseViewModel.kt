package xyz.katiedotson.dodo.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber


abstract class BaseViewModel() : ViewModel() {

    protected val _spinner = MutableLiveData(false)
    internal val spinner: LiveData<Boolean>
        get() = _spinner

    private val _snackbar = MutableLiveData<String?>()
    internal val snackbar: LiveData<String?>
        get() = _snackbar

    internal fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                Timber.e(error)
                throw(error)
            } finally {
                _spinner.value = false
            }
        }
    }

    fun onSnackbarShown() {
        _snackbar.value = null
    }



}