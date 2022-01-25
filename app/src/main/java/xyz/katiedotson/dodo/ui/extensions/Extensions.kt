package xyz.katiedotson.dodo.ui.extensions

import android.view.View


fun View.toggleVisible(show: Boolean?) {
    this.visibility = if (show == true) View.VISIBLE else View.GONE
}