package xyz.katiedotson.dodo.ui.fragments.home.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.katiedotson.dodo.common.extensions.toggleVisible
import xyz.katiedotson.dodo.data.todo.TodoDto
import xyz.katiedotson.dodo.databinding.ViewListItemTodoBinding
import xyz.katiedotson.dodo.ui.views.LabelChip

class TodoAdapter(private val clickListener: TodoClickListeners) : ListAdapter<TodoDto, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    interface TodoClickListeners {
        fun onEditButtonClicked(todo: TodoDto)
        fun onDeleteButtonClicked(todo: TodoDto)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(todo, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            ViewListItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class TodoViewHolder(private val binding: ViewListItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoDto, clickListener: TodoClickListeners) {
            binding.date.text = if (item.dueDateExists()) "Due " + item.formattedDueDate() else null
            binding.date.toggleVisible(item.dueDateExists())
            binding.title.text = item.name
            binding.editBtn.setOnClickListener {
                clickListener.onEditButtonClicked(item)
            }
            binding.deleteBtn.setOnClickListener {
                clickListener.onDeleteButtonClicked(item)
            }
            if (item.labelDto != null) {
                val labelChip = LabelChip(binding.labels.context, item.labelDto, LabelChip.Mode.Display)
                binding.labels.addView(labelChip)
            }
        }
    }
}

private class TodoDiffCallback : DiffUtil.ItemCallback<TodoDto>() {
    override fun areItemsTheSame(oldItem: TodoDto, newItem: TodoDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoDto, newItem: TodoDto): Boolean {
        return oldItem == newItem
    }
}