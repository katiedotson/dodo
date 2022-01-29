package xyz.katiedotson.dodo.ui.fragments.labels.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.katiedotson.dodo.data.color.DodoColor
import xyz.katiedotson.dodo.data.label.Label
import xyz.katiedotson.dodo.ui.views.LabelChip

class LabelChipAdapter(private val labelClickListener: LabelClickListener, val colors: List<DodoColor>) :
    ListAdapter<Label, LabelChipAdapter.LabelChipViewHolder>(LabelDiffCallback()) {

    interface LabelClickListener {
        fun onLabelChipClick(label: Label)
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
        fun bind(item: Label, listener: LabelClickListener) {
            val color = colors.find { it.hex.equals(item.colorHex, true) }
            chip.setLabelBackgroundColor(color!!)
            chip.setBorder(color)
            chip.setText(item.name)
            chip.setTextColor(color)
            chip.setMode(LabelChip.Mode.Edit)
            chip.setOnClickListener {
                listener.onLabelChipClick(item)
            }
        }
    }
}

private class LabelDiffCallback : DiffUtil.ItemCallback<Label>() {
    override fun areItemsTheSame(oldItem: Label, newItem: Label): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Label, newItem: Label): Boolean {
        return oldItem == newItem
    }
}