package xyz.katiedotson.dodo.ui.fragments.addedit

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.common.extensions.toggleVisible
import xyz.katiedotson.dodo.ui.base.BaseFragment
import xyz.katiedotson.dodo.data.todo.Todo
import xyz.katiedotson.dodo.databinding.FragmentAddEditBinding

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
                .setTitleText(getString(R.string.add_edit_select_due_date))
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
        binding.removeDueDateBtn.toggleVisible(show = todo?.dueDateExists() == true)
        if (todo?.dueDateExists() == true) {
            binding.displayDueDate.text = getString(R.string.add_edit_format_due_date, todo.formattedDueDate())
            binding.editDueDateBtn.text = getString(R.string.add_edit_edit_due_date)
        } else {
            binding.displayDueDate.text = getString(R.string.add_edit_no_due_date)
            binding.editDueDateBtn.text = getString(R.string.add_edit_add_due_date)
        }
        binding.titleField.setText(todo?.name)
    }

    companion object {
        const val TAG = "ADD_EDIT"
    }

}