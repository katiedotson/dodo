package xyz.katiedotson.dodo.ui.fragments.home.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.katiedotson.dodo.common.extensions.toggleGone
import xyz.katiedotson.dodo.data.todo.TodoDto
import xyz.katiedotson.dodo.data.usersettings.UserSettingsDto
import xyz.katiedotson.dodo.databinding.ViewListItemTodoBinding
import xyz.katiedotson.dodo.ui.views.LabelChip

class TodoAdapter(private val settings: UserSettingsDto, private val clickListener: TodoClickListeners) : ListAdapter<TodoDto, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    interface TodoClickListeners {
        fun onEditButtonClicked(todo: TodoDto)
        fun onDeleteButtonClicked(todo: TodoDto)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(todo, settings, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            ViewListItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class TodoViewHolder(private val binding: ViewListItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoDto, settings: UserSettingsDto, clickListener: TodoClickListeners) {

            // title
            binding.title.text = item.description

            // due date
            val showDate = item.dueDateExists() && settings.showDueDate
            binding.date.text = if (showDate) item.formattedDueDate() else null
            binding.dueDateLayout.toggleGone(showDate)

            // last update
            val showLastUpdate = settings.showLastUpdate
            binding.lastUpdateDate.text = if (showLastUpdate) item.formattedLastUpdate() else null
            binding.lastUpdateDateLayout.toggleGone(showLastUpdate)

            // created date
            val showCreatedDate = settings.showDateCreated
            binding.createdDate.text = if (showCreatedDate) item.formattedDateCreated() else null
            binding.createdDateLayout.toggleGone(showCreatedDate)

            // labels
            binding.labels.removeAllViews()
            if (item.labelDto != null && settings.showLabel) {
                val labelChip = LabelChip(binding.labels.context, item.labelDto, LabelChip.Mode.Display)
                binding.labels.addView(labelChip)
            }

            // notes
            val showNotes = settings.showNotes
            binding.notes.text = if (showNotes) item.notes else null
            binding.notes.toggleGone(showNotes && item.notes.isNotEmpty())

            binding.editBtn.setOnClickListener {
                clickListener.onEditButtonClicked(item)
            }

            binding.deleteBtn.setOnClickListener {
                clickListener.onDeleteButtonClicked(item)
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