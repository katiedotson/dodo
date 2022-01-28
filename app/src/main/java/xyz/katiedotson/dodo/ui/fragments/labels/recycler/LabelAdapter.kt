package xyz.katiedotson.dodo.ui.fragments.labels.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.katiedotson.dodo.data.dto.LabelColor
import xyz.katiedotson.dodo.data.label.Label
import xyz.katiedotson.dodo.ui.views.ColorLabelChip

class LabelAdapter(private val labelClickListener: LabelClickListener) :
    ListAdapter<Label, LabelAdapter.LabelViewHolder>(LabelDiffCallback()) {

    interface LabelClickListener {
        fun onLabelChipClick(label: Label)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        val label = getItem(position)
        holder.bind(label, listener = labelClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        return LabelViewHolder(ColorLabelChip(parent.context))
    }

    class LabelViewHolder(private val colorLabelChip: ColorLabelChip) :
        RecyclerView.ViewHolder(colorLabelChip) {
        fun bind(item: Label, listener: LabelClickListener) {
            val labelColorItem = LabelColor.fromHex(item.colorHex)
            colorLabelChip.setLabelBackgroundColor(labelColorItem)
            colorLabelChip.setBorder(labelColorItem)
            colorLabelChip.setText(item.name)
            colorLabelChip.setTextColor(labelColorItem)
            colorLabelChip.setOnClickListener {
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