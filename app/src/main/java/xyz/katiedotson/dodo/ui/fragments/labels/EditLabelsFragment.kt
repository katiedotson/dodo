package xyz.katiedotson.dodo.ui.fragments.labels

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.data.dto.LabelColor
import xyz.katiedotson.dodo.data.label.Label
import xyz.katiedotson.dodo.ui.base.BaseFragment
import xyz.katiedotson.dodo.databinding.FragmentEditLabelsBinding
import xyz.katiedotson.dodo.ui.fragments.labels.recycler.LabelAdapter

@AndroidEntryPoint
class EditLabelsFragment : BaseFragment(R.layout.fragment_edit_labels) {

    private val viewModel: EditLabelsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEditLabelsBinding.bind(view)

        binding.chipGroup.isSelectionRequired = true
        binding.chipGroup.isSingleSelection = true

        viewModel.colors.forEach {
            val chip = Chip(requireContext())
            chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(it.hex))
            chip.text = it.colorName
            chip.isCheckable = true
            if (it.useWhiteText) {
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if(it.useBorder) {

            }
            binding.chipGroup.addView(chip)
        }

        val adapter = LabelAdapter(object : LabelAdapter.LabelClickListener {
            override fun onLabelChipClick(label: Label) {
                viewModel.labelSelectedForEdit(label)
                showLabelSelected(label, binding)
                setSheetExpanded(binding, false)
            }
        })
        binding.recycler.adapter = adapter

        with(viewModel) {
            labels.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
            labelCreatedEvent.observe(viewLifecycleOwner) {
                when (it.getContentIfNotHandled()) {
                    is EditLabelsViewModel.LabelCreatedEvent.Success -> {
                        clearAddLabelForm(binding)
                        Snackbar.make(binding.root, "Success!", Snackbar.LENGTH_SHORT).show()
                    }
                    is EditLabelsViewModel.LabelCreatedEvent.Failure -> {
                        Snackbar.make(binding.root, "Something went wrong.", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        // no op
                    }
                }
            }
            newLabelClearedEvent.observe(viewLifecycleOwner) {
                viewModel.formCleared()
                clearAddLabelForm(binding)
            }
            viewState.observe(viewLifecycleOwner) {
                when (it) {
                    is EditLabelsViewModel.EditLabelsViewState.NewLabel -> {
                        binding.bottomSheetTitle.text = "Add a new label"
                    }
                    is EditLabelsViewModel.EditLabelsViewState.EditLabel -> {
                        binding.bottomSheetTitle.text = "Edit label"
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

    private fun findSelectedChipColor(checkedId: Int, binding: FragmentEditLabelsBinding): Int? {
        val chip = binding.root.findViewById<Chip?>(checkedId)
        return chip?.chipBackgroundColor?.defaultColor
    }

    private fun showLabelSelected(label: Label, binding: FragmentEditLabelsBinding) {
        binding.nameField.setText(label.name)
        (binding.chipGroup.children.find { view ->
            (view as Chip).chipBackgroundColor?.defaultColor == label.color
        } as Chip).isChecked = true
    }

    private fun clearAddLabelForm(binding: FragmentEditLabelsBinding) {
        binding.nameField.setText("")
        binding.nameField.clearFocus()
        binding.chipGroup.clearCheck()
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = STATE_COLLAPSED
    }

    private fun setSheetExpanded(binding: FragmentEditLabelsBinding, expand: Boolean) {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = if (expand) STATE_EXPANDED else STATE_COLLAPSED
    }

}