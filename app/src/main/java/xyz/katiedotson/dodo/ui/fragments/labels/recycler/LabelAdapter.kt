package xyz.katiedotson.dodo.ui.fragments.labels.recycler

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.data.dto.LabelColor
import xyz.katiedotson.dodo.data.label.Label
import xyz.katiedotson.dodo.databinding.ViewLabelChipListItemBinding

class LabelAdapter(private val labelClickListener: LabelClickListener) :
    ListAdapter<Label, RecyclerView.ViewHolder>(LabelDiffCallback()) {

    interface LabelClickListener {
        fun onLabelChipClick(label: Label)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val label = getItem(position)
        (holder as LabelViewHolder).bind(label, listener = labelClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LabelViewHolder(
            ViewLabelChipListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class LabelViewHolder(private val binding: ViewLabelChipListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Label, listener: LabelClickListener) {

            val labelColor = LabelColor.fromHex(item.colorHex)

            binding.root.text = item.name

            binding.root.chipBackgroundColor =
                ColorStateList.valueOf(Color.parseColor(item.colorHex))

            if (labelColor != null && !labelColor.useWhiteText) {
                val grey = ContextCompat.getColor(
                    binding.root.context,
                    R.color.grey
                )
                binding.root.setTextColor(grey)
                binding.root.chipIconTint = ColorStateList.valueOf(grey)
            }

            if (labelColor?.useBorder == true) {
                binding.root.chipStrokeColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.grey
                    )
                )
                binding.root.chipStrokeWidth = 1f
            }

            binding.root.setOnClickListener {
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