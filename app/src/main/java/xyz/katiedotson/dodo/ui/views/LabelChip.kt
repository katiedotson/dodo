package xyz.katiedotson.dodo.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.data.color.DodoColor
import xyz.katiedotson.dodo.data.label.LabelDto

class LabelChip(context: Context) : Chip(context) {

    var labelId: Long? = null

    constructor(context: Context, label: LabelDto, mode: Mode): this(context) {
        setLabelBackgroundColor(label.colorHex)
        setText(label.labelName)
        setMode(mode)
        setBorder(label.useBorder == true)
        setTextColor(label.useWhiteText == true)
        labelId = label.labelId
    }

    constructor(context: Context, color: DodoColor) : this(context) {
        setLabelBackgroundColor(color.hex)
        setText(color.displayName)
        setMode(Mode.Choice)
        setBorder(color.useBorder)
        setTextColor(color.useWhiteText)
    }

    fun setMode(mode: Mode) {
        this.isCheckable = mode == Mode.Choice
        this.isClickable = mode !== Mode.Display
    }

    fun setText(name: String) {
        this.text = name
    }

    fun setLabelBackgroundColor(colorString: String?) {
        this.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(colorString))
    }

    fun setBorder(useBorder: Boolean) {
        if (useBorder) {
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

    fun setTextColor(useWhiteText: Boolean) {
        if (!useWhiteText) {
            val grey = ContextCompat.getColor(
                this.context,
                R.color.grey
            )
            this.setTextColor(grey)
            this.chipIconTint = ColorStateList.valueOf(grey)
        } else {
            val white = ContextCompat.getColor(this.context, R.color.pure_white)
            this.setTextColor(white)
            this.chipIconTint = ColorStateList.valueOf(white)
        }
    }

    enum class Mode {
        Edit,
        Choice,
        Display
    }

}