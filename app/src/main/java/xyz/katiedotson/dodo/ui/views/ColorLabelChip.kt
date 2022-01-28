package xyz.katiedotson.dodo.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.data.dto.LabelColor
import xyz.katiedotson.dodo.databinding.ViewLabelChipListItemBinding

@SuppressLint("ViewConstructor")
class ColorLabelChip(context: Context) : Chip(context) {

    private val binding = ViewLabelChipListItemBinding.inflate(LayoutInflater.from(context))

    constructor(context: Context, color: LabelColor.LabelColorItem) : this(context) {
        binding.root.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(color.hex))
        binding.root.text = color.colorName
        binding.root.isCheckable = true
        if (color.useWhiteText) {
            binding.root.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        if (color.useBorder) {
            binding.root.chipStrokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey))
            binding.root.chipStrokeWidth = 1f
        }
    }

    fun setText(name: String) {
        binding.root.text = name
    }

    fun setLabelBackgroundColor(item: LabelColor.LabelColorItem) {
        binding.root.chipBackgroundColor =
            ColorStateList.valueOf(Color.parseColor(item.hex))
    }

    fun setBorder(labelColor: LabelColor.LabelColorItem) {
        if (labelColor.useBorder) {
            binding.root.chipStrokeColor = ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.grey
                )
            )
            binding.root.chipStrokeWidth = 1f
        } else {
            binding.root.chipStrokeColor = null
            binding.root.chipStrokeWidth = 0f
        }
    }

    fun setTextColor(labelColor: LabelColor.LabelColorItem) {
        if (!labelColor.useWhiteText) {
            val grey = ContextCompat.getColor(
                binding.root.context,
                R.color.grey
            )
            binding.root.setTextColor(grey)
            binding.root.chipIconTint = ColorStateList.valueOf(grey)
        } else if (labelColor.useWhiteText) {
            val white = ContextCompat.getColor(binding.root.context, R.color.pure_white)
            binding.root.setTextColor(white)
            binding.root.chipIconTint = ColorStateList.valueOf(white)
        }
    }

}