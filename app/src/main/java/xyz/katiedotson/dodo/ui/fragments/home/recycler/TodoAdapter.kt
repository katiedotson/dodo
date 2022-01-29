package xyz.katiedotson.dodo.ui.fragments.home.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.katiedotson.dodo.common.extensions.toggleVisible
import xyz.katiedotson.dodo.data.todo.Todo
import xyz.katiedotson.dodo.databinding.ViewListItemTodoBinding

class TodoAdapter(private val clickListener: TodoClickListeners) : ListAdapter<Todo, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    interface TodoClickListeners {
        fun onEditButtonClicked(todo: Todo)
        fun onDeleteButtonClicked(todo: Todo)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = getItem(position)
        (holder as TodoViewHolder).bind(todo, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            ViewListItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class TodoViewHolder(private val binding: ViewListItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Todo, clickListener: TodoClickListeners) {
            binding.date.text = if (item.dueDateExists()) "Due " + item.formattedDueDate() else null
            binding.date.toggleVisible(item.dueDateExists())
            binding.title.text = item.name
            binding.editBtn.setOnClickListener {
                clickListener.onEditButtonClicked(item)
            }
            binding.deleteBtn.setOnClickListener {
                clickListener.onDeleteButtonClicked(item)
            }
        }
    }
}

private class TodoDiffCallback : DiffUtil.ItemCallback<Todo>() {
    override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem == newItem
    }
}