package xyz.katiedotson.dodo.ui.fragments.labels.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.katiedotson.dodo.data.label.LabelDto
import xyz.katiedotson.dodo.ui.views.LabelChip

class LabelChipAdapter(private val labelClickListener: LabelClickListener) :
    ListAdapter<LabelDto, LabelChipAdapter.LabelChipViewHolder>(LabelDiffCallback()) {

    interface LabelClickListener {
        fun onLabelChipClick(label: LabelDto)
    }

    override fun onBindViewHolder(holder: LabelChipViewHolder, position: Int) {
        val label = getItem(position)
        holder.bind(label, labelClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelChipViewHolder {
        return LabelChipViewHolder(LabelChip(parent.context))
    }

    class LabelChipViewHolder(private val chip: LabelChip) :
        RecyclerView.ViewHolder(chip) {
        fun bind(item: LabelDto, listener: LabelClickListener) {
            chip.setLabelBackgroundColor(item.colorHex)
            chip.setBorder(item.useBorder == true)
            chip.setText(item.labelName)
            chip.setTextColor(item.useWhiteText == true)
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