package xyz.katiedotson.dodo.ui.fragments.addedit

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.common.extensions.toggleVisible
import xyz.katiedotson.dodo.data.dto.DodoError
import xyz.katiedotson.dodo.data.todo.TodoDto
import xyz.katiedotson.dodo.databinding.FragmentAddEditBinding
import xyz.katiedotson.dodo.ui.base.BaseFragment
import xyz.katiedotson.dodo.ui.views.LabelChip

@AndroidEntryPoint
class AddEditFragment : BaseFragment(R.layout.fragment_add_edit) {

    private val viewModel: AddEditViewModel by viewModels()
    private val args: AddEditFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadTodo(args.todoId)

        val binding = FragmentAddEditBinding.bind(view)
        binding.labels.isSingleSelection = true

        with(binding) {
            toolbar.title = if (args.todoId != 0L) "Edit" else "Add"
            toolbar.setNavigationOnClickListener {
                mainNavController.popBackStack()
            }

            labels.setOnCheckedChangeListener { _, checkedId ->
                val labelId = findSelectedLabelId(checkedId, binding)
                viewModel.checkedLabelChanged(labelId)
            }

            descriptionField.addTextChangedListener {
                viewModel.descriptionChanged(it.toString())
            }

            notesField.addTextChangedListener {
                viewModel.notesChanged(it.toString())
            }

            submitBtn.setOnClickListener {
                viewModel.submit()
            }

            editDueDateBtn.setOnClickListener {
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

            removeDueDateBtn.setOnClickListener {
                viewModel.dueDateChanged(newDueDate = null)
            }
        }

        with(viewModel) {
            labels.observe(viewLifecycleOwner) { labels ->
                labels.forEach { label ->
                    val chip = LabelChip(requireContext(), label, LabelChip.Mode.Choice)
                    binding.labels.addView(chip)
                }
            }

            viewState.observe(viewLifecycleOwner) {
                when (it) {
                    is AddEditViewModel.AddEditViewState.InitialState -> {
                        updateDateAndLabelFields(binding, it.todo)
                        binding.descriptionField.setText(it.todo?.description)
                        binding.notesField.setText(it.todo?.notes)
                    }
                    is AddEditViewModel.AddEditViewState.EditedState -> {
                        updateDateAndLabelFields(binding, it.todo)
                    }
                    is AddEditViewModel.AddEditViewState.ErrorState -> {
                        showError(binding.root, DodoError.DATABASE_ERROR)
                    }
                    is AddEditViewModel.AddEditViewState.SuccessState -> {
                        mainNavController.navigate(AddEditFragmentDirections.actionAddEditFragmentToHomeFragment(it.id))
                    }
                }
            }

            validationState.observe(viewLifecycleOwner) {
                if (it.passed) {
                    clearFieldErrors(binding)
                } else {
                    showFieldErrors(binding, it)
                }
            }
        }

    }

    private fun clearFieldErrors(binding: FragmentAddEditBinding) {
        binding.titleLayout.error = null
    }

    private fun showFieldErrors(binding: FragmentAddEditBinding, validation: AddEditViewModel.Validation?) {
        if (validation?.descriptionValidation != null) {
            binding.titleLayout.error = getString(R.string.add_edit_error_description_required)
        } else {
            binding.titleLayout.error = null
        }
    }

    private fun findSelectedLabelId(checkedId: Int, binding: FragmentAddEditBinding): Long? {
        val chip = binding.labels.findViewById<LabelChip?>(checkedId)
        return chip?.labelId
    }

    private fun updateDateAndLabelFields(binding: FragmentAddEditBinding, todo: TodoDto?) {
        binding.removeDueDateBtn.toggleVisible(show = todo?.dueDateExists() == true)
        if (todo?.dueDateExists() == true) {
            binding.displayDueDate.text = getString(R.string.add_edit_format_due_date, todo.formattedDueDate())
            binding.editDueDateBtn.text = getString(R.string.add_edit_edit_due_date)
        } else {
            binding.displayDueDate.text = getString(R.string.add_edit_no_due_date)
            binding.editDueDateBtn.text = getString(R.string.add_edit_add_due_date)
        }
        if (todo?.labelId != null) {
            (binding.labels.children.find { view ->
                (view as LabelChip).labelId == todo.labelId
            } as Chip).isChecked = true
        }
    }

    companion object {
        const val TAG = "ADD_EDIT"
    }

}