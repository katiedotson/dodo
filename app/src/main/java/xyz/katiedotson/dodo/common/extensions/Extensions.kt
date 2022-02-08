package xyz.katiedotson.dodo.common.extensions

import android.view.View


fun View.toggleVisible(show: Boolean?) {
    this.visibility = if (show == true) View.VISIBLE else View.INVISIBLE
}

fun View.toggleGone(show: Boolean?) {
    this.visibility = if (show == true) View.VISIBLE else View.GONE
}