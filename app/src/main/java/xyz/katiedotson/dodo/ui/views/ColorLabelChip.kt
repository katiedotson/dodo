package xyz.katiedotson.dodo.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.data.dto.LabelColor

@SuppressLint("ViewConstructor")
class ColorLabelChip(context: Context, color: LabelColor.LabelColorItem) : Chip(context) {
    init {
        this.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(color.hex))
        this.text = color.colorName
        this.isCheckable = true
        if (color.useWhiteText) {
            this.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        if (color.useBorder) {
            this.chipStrokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey))
            this.chipStrokeWidth = 1f
        }
    }
}