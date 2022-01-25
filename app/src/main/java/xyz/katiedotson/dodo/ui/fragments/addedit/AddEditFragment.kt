package xyz.katiedotson.dodo.ui.fragments.addedit

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.ui.base.BaseFragment
import xyz.katiedotson.dodo.data.todo.Todo
import xyz.katiedotson.dodo.databinding.FragmentAddEditBinding
import xyz.katiedotson.dodo.ui.extensions.toggleVisible

@AndroidEntryPoint
class AddEditFragment : BaseFragment(R.layout.fragment_add_edit) {

    private val viewModel: AddEditViewModel by viewModels()
    private val args: AddEditFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadTodo(args.todoId)

        val binding = FragmentAddEditBinding.bind(view)

        binding.titleField.addTextChangedListener {
            viewModel.titleChanged(it.toString())
        }

        binding.submitBtn.setOnClickListener {
            viewModel.submit()
        }

        binding.editDueDateBtn.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Select a due date")
                .setSelection(MaterialDatePicker.thisMonthInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener {
                viewModel.dueDateChanged(it)
            }

            datePicker.show(childFragmentManager, TAG)
        }

        binding.removeDueDateBtn.setOnClickListener {
            viewModel.dueDateChanged(newDueDate = null)
        }

        viewModel.viewState.observe(viewLifecycleOwner) {
            when (it) {
                is AddEditViewModel.AddEditViewState.InitialState -> {
                    initializeFields(binding, it.todo)
                }
                is AddEditViewModel.AddEditViewState.EditedState -> {
                    initializeFields(binding, it.todo)
                }
                is AddEditViewModel.AddEditViewState.ErrorState -> {
                    // todo show errors on fields
                    showError(binding.root, it.error)
                }
                is AddEditViewModel.AddEditViewState.ValidState -> {
                    // todo clear errors
                }
                is AddEditViewModel.AddEditViewState.SuccessState -> {
                    mainNavController.navigate(AddEditFragmentDirections.actionAddEditFragmentToHomeFragment(it.id))
                }
            }
        }

    }

    private fun initializeFields(binding: FragmentAddEditBinding, todo: Todo?) {
        binding.displayDueDate.text = "Due " + todo?.formattedDueDate()
        binding.titleField.setText(todo?.name)
        binding.removeDueDateBtn.toggleVisible(show = todo?.dueDateExists() == true)
        if (todo?.dueDateExists() != true) {
            binding.editDueDateBtn.text = "Add Due Date"
        } else {
            binding.editDueDateBtn.text = "Edit Due Date"
        }
    }

    companion object {
        const val TAG = "ADD_EDIT"
    }

}