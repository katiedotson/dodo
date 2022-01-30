package xyz.katiedotson.dodo.ui.fragments.addedit

import android.graphics.Color
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
import xyz.katiedotson.dodo.data.todo.TodoDto
import xyz.katiedotson.dodo.databinding.FragmentAddEditBinding
import xyz.katiedotson.dodo.databinding.FragmentEditLabelsBinding
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
        binding.labels.isSelectionRequired = true
        binding.labels.isSingleSelection = true

        viewModel.labels.observe(viewLifecycleOwner) { labels ->
            labels.forEach { label ->
                val chip = LabelChip(requireContext(), label, LabelChip.Mode.Choice)
                binding.labels.addView(chip)
            }
        }

        binding.labels.setOnCheckedChangeListener { _, checkedId ->
            val color = findSelectedChipColor(checkedId, binding)
            viewModel.checkedColorChanged(color)
        }

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

    private fun findSelectedChipColor(checkedId: Int, binding: FragmentAddEditBinding): String? {
        val chip = binding.labels.findViewById<Chip?>(checkedId)
        return if (chip != null) Integer.toHexString(chip.chipBackgroundColor!!.defaultColor) else null
    }

    private fun initializeFields(binding: FragmentAddEditBinding, todo: TodoDto?) {
        binding.removeDueDateBtn.toggleVisible(show = todo?.dueDateExists() == true)
        if (todo?.dueDateExists() == true) {
            binding.displayDueDate.text = getString(R.string.add_edit_format_due_date, todo.formattedDueDate())
            binding.editDueDateBtn.text = getString(R.string.add_edit_edit_due_date)
        } else {
            binding.displayDueDate.text = getString(R.string.add_edit_no_due_date)
            binding.editDueDateBtn.text = getString(R.string.add_edit_add_due_date)
        }
        binding.titleField.setText(todo?.name)
        if (todo?.labelColor != null) {
            (binding.labels.children.find { view ->
                (view as Chip).chipBackgroundColor?.defaultColor == Color.parseColor(todo.labelColor)
            } as Chip).isChecked = true
        }
    }

    companion object {
        const val TAG = "ADD_EDIT"
    }

}