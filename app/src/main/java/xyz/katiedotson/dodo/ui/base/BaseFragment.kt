package xyz.katiedotson.dodo.ui.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.data.dto.DodoError


abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    val mainNavController: NavController
        get() {
            val activity = requireActivity()
            return activity.findNavController(R.id.nav_host_fragment)
        }

    open fun showError(view: View, error: DodoError) {
        Snackbar.make(view, getErrorText(error), Snackbar.LENGTH_SHORT).show()
    }

    open fun showSuccess(view: View) {
        Snackbar.make(view, "Success!", Snackbar.LENGTH_LONG).show()
    }

    private fun getErrorText(error: DodoError): String {
        return when (error) {
            DodoError.DATABASE_ERROR -> {
                "There was something wrong when we tried to receive data."
            }
            DodoError.NETWORK_ERROR -> {
                "There was something wrong when we tried to connect to the network."
            }
            DodoError.VALIDATION_ERROR -> {
                "Something wasn't entered correctly. Please check all of the fields are filled out correctly."
            }
        }
    }

}