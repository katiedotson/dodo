package xyz.katiedotson.dodo.ui.fragments.labels

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.common.DodoFieldError
import xyz.katiedotson.dodo.common.extensions.toggleVisible
import xyz.katiedotson.dodo.data.dto.DodoError
import xyz.katiedotson.dodo.data.label.LabelDto
import xyz.katiedotson.dodo.databinding.FragmentEditLabelsBinding
import xyz.katiedotson.dodo.ui.base.BaseFragment
import xyz.katiedotson.dodo.ui.fragments.labels.recycler.LabelChipAdapter
import xyz.katiedotson.dodo.ui.views.LabelChip

@AndroidEntryPoint
class EditLabelsFragment : BaseFragment(R.layout.fragment_edit_labels) {

    private val viewModel: EditLabelsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEditLabelsBinding.bind(view)

        binding.chipGroup.isSelectionRequired = true
        binding.chipGroup.isSingleSelection = true

        val adapter = LabelChipAdapter(object : LabelChipAdapter.LabelClickListener {
            override fun onLabelChipClick(label: LabelDto) {
                viewModel.labelSelectedForEdit(label)
                showLabelSelected(label, binding)
                setSheetExpanded(binding, true)
            }
        })

        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        with(viewModel) {
            colors.observe(viewLifecycleOwner) { colors ->
                colors?.forEach { dodoColor ->
                    val chip = LabelChip(requireContext(), dodoColor, LabelChip.Mode.Choice)
                    binding.chipGroup.addView(chip)
                }
            }
            labels.observe(viewLifecycleOwner) { labels ->
                adapter.submitList(labels)
            }
            labelCreatedEvent.observe(viewLifecycleOwner) {
                when (it.getContentIfNotHandled()) {
                    is EditLabelsViewModel.LabelCreatedEvent.Success -> {
                        clearLabelForm(binding)
                        showSuccess(binding.root)
                    }
                    is EditLabelsViewModel.LabelCreatedEvent.Failure -> {
                        showError(binding.root, (it.content as EditLabelsViewModel.LabelCreatedEvent.Failure).error)
                    }
                    is EditLabelsViewModel.LabelCreatedEvent.InvalidFields -> {
                        showFieldErrors(binding, (it.content as EditLabelsViewModel.LabelCreatedEvent.InvalidFields).validation)
                        showError(binding.bottomSheet, DodoError.VALIDATION_ERROR)
                    }
                    else -> {
                        // no op
                    }
                }
            }
            viewState.observe(viewLifecycleOwner) {
                when (it) {
                    is EditLabelsViewModel.EditLabelsViewState.NewLabel -> {
                        binding.bottomSheetTitle.text = getString(R.string.edit_label_add_label_heading)
                    }
                    is EditLabelsViewModel.EditLabelsViewState.EditLabel -> {
                        binding.bottomSheetTitle.text = getString(R.string.edit_label_edit_label_heading)
                    }
                }
            }
        }

        with(binding) {
            saveBtn.setOnClickListener {
                viewModel.saveNewLabel()
            }
            clearBtn.setOnClickListener {
                viewModel.clearNewLabel()
                clearLabelForm(binding)
                setSheetExpanded(binding, false)
            }
            nameField.addTextChangedListener {
                viewModel.nameFieldChanged(it.toString())
            }
            chipGroup.setOnCheckedChangeListener { _, checkedId ->
                val color = findSelectedChipColor(checkedId, binding)
                viewModel.checkedColorChanged(color)
            }
            nameField.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setSheetExpanded(binding, true)
                }
            }
        }

    }

    private fun showFieldErrors(binding: FragmentEditLabelsBinding, validation: EditLabelsViewModel.Validation) {
        if (validation.colorError != null) {
            binding.colorError.toggleVisible(true)
            binding.colorError.text = "A color choice is required."
        }
        if (validation.titleError != null) {
            when (validation.titleError) {
                DodoFieldError.Empty, DodoFieldError.TooShort -> {
                    binding.nameLayout.error = "Label title is required."
                }
                DodoFieldError.TooLong -> {
                    binding.nameLayout.error = "Label title is limited to 30 characters."
                }
            }
        }
    }

    private fun findSelectedChipColor(checkedId: Int, binding: FragmentEditLabelsBinding): String? {
        val chip = binding.root.findViewById<Chip?>(checkedId)
        return if (chip != null) Integer.toHexString(chip.chipBackgroundColor!!.defaultColor) else null
    }

    private fun showLabelSelected(label: LabelDto, binding: FragmentEditLabelsBinding) {
        binding.nameField.setText(label.labelName)
        (binding.chipGroup.children.find { view ->
            (view as Chip).chipBackgroundColor?.defaultColor == Color.parseColor(label.colorHex)
        } as Chip).isChecked = true
    }

    private fun clearLabelForm(binding: FragmentEditLabelsBinding) {
        binding.nameField.setText("")
        binding.nameField.clearFocus()
        binding.chipGroup.clearCheck()
        setSheetExpanded(binding, false)
    }

    private fun setSheetExpanded(binding: FragmentEditLabelsBinding, expand: Boolean) {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = if (expand) STATE_EXPANDED else STATE_COLLAPSED
    }

}