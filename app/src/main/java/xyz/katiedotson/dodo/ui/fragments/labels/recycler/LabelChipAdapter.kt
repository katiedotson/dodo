package xyz.katiedotson.dodo.ui.fragments.labels.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.katiedotson.dodo.data.color.DodoColor
import xyz.katiedotson.dodo.data.label.LabelDto
import xyz.katiedotson.dodo.ui.views.LabelChip

class LabelChipAdapter(private val labelClickListener: LabelClickListener, val colors: List<DodoColor>) :
    ListAdapter<LabelDto, LabelChipAdapter.LabelChipViewHolder>(LabelDiffCallback()) {

    interface LabelClickListener {
        fun onLabelChipClick(label: LabelDto)
    }

    override fun onBindViewHolder(holder: LabelChipViewHolder, position: Int) {
        val label = getItem(position)
        holder.bind(label, labelClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelChipViewHolder {
        return LabelChipViewHolder(LabelChip(parent.context), colors)
    }

    class LabelChipViewHolder(private val chip: LabelChip, private val colors: List<DodoColor>) :
        RecyclerView.ViewHolder(chip) {
        fun bind(item: LabelDto, listener: LabelClickListener) {
            val color = colors.find { it.hex.equals(item.colorHex, true) }!!
            chip.setLabelBackgroundColor(color.hex)
            chip.setBorder(color.useBorder)
            chip.setText(item.labelName)
            chip.setTextColor(color.useWhiteText)
            chip.setMode(LabelChip.Mode.Edit)
            chip.setOnClickListener {
                listener.onLabelChipClick(item)
            }
        }
    }
}

private class LabelDiffCallback : DiffUtil.ItemCallback<LabelDto>() {
    override fun areItemsTheSame(oldItem: LabelDto, newItem: LabelDto): Boolean {
        return oldItem.labelId == newItem.labelId
    }

    override fun areContentsTheSame(oldItem: LabelDto, newItem: LabelDto): Boolean {
        return oldItem == newItem
    }
}