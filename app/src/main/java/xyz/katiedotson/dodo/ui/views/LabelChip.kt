package xyz.katiedotson.dodo.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.data.color.DodoColor

class LabelChip(context: Context) : Chip(context) {

    constructor(context: Context, color: DodoColor, mode: Mode) : this(context) {
        setLabelBackgroundColor(color)
        setText(color.displayName)
        setMode(mode)
        setBorder(color)
        setTextColor(color)
    }

    fun setMode(mode: Mode) {
        this.isCheckable = mode == Mode.ColorChoice
//        if (mode == Mode.Edit) {
//            this.setChipIconResource(R.drawable.ic_baseline_edit_24)
//            this.chipIconSize = 20f
//            this.chipStartPadding = 8f
//        }
    }

    fun setText(name: String) {
        this.text = name
    }

    fun setLabelBackgroundColor(item: DodoColor?) {
        this.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(item?.hex))
    }

    fun setBorder(labelColor: DodoColor) {
        if (labelColor.useBorder) {
            this.chipStrokeColor = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this.context,
                    R.color.grey
                )
            )
            this.chipStrokeWidth = 1f
        } else {
            this.chipStrokeColor = null
            this.chipStrokeWidth = 0f
        }
    }

    fun setTextColor(labelColor: DodoColor) {
        if (!labelColor.useWhiteText) {
            val grey = ContextCompat.getColor(
                this.context,
                R.color.grey
            )
            this.setTextColor(grey)
            this.chipIconTint = ColorStateList.valueOf(grey)
        } else if (labelColor.useWhiteText) {
            val white = ContextCompat.getColor(this.context, R.color.pure_white)
            this.setTextColor(white)
            this.chipIconTint = ColorStateList.valueOf(white)
        }
    }

    enum class Mode {
        Edit,
        ColorChoice
    }

}