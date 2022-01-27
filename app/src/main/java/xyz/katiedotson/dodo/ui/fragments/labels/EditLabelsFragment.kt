package xyz.katiedotson.dodo.ui.fragments.labels

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.data.label.Label
import xyz.katiedotson.dodo.ui.base.BaseFragment
import xyz.katiedotson.dodo.databinding.FragmentEditLabelsBinding
import xyz.katiedotson.dodo.ui.fragments.labels.recycler.LabelAdapter
import xyz.katiedotson.dodo.ui.views.ColorLabelChip

@AndroidEntryPoint
class EditLabelsFragment : BaseFragment(R.layout.fragment_edit_labels) {

    private val viewModel: EditLabelsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEditLabelsBinding.bind(view)

        binding.chipGroup.isSelectionRequired = true
        binding.chipGroup.isSingleSelection = true

        viewModel.colors.forEach {
            val chip = ColorLabelChip(requireContext(), it)
            binding.chipGroup.addView(chip)
        }

        val adapter = LabelAdapter(object : LabelAdapter.LabelClickListener {
            override fun onLabelChipClick(label: Label) {
                viewModel.labelSelectedForEdit(label)
                showLabelSelected(label, binding)
                setSheetExpanded(binding, true)
            }
        })
        binding.recycler.adapter = adapter

        val layoutManager = GridLayoutManager(requireContext(), 10)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val adapterList = adapter.currentList
                return if (adapterList.size >= position) {
                    val labelName = adapterList[position].name
                    when {
                        labelName.length <= 5 -> {
                            3
                        }
                        labelName.length <= 10 -> {
                            4
                        }
                        labelName.length <= 15 -> {
                            5
                        }
                        labelName.length <= 20 -> {
                            6
                        }
                        labelName.length <= 25 -> {
                            7
                        }
                        labelName.length <= 30 -> {
                            8
                        }
                        else -> {
                            layoutManager.spanCount
                        }
                    }
                } else 1
            }
        }
        binding.recycler.layoutManager = layoutManager

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

    private fun findSelectedChipColor(checkedId: Int, binding: FragmentEditLabelsBinding): String? {
        val chip = binding.root.findViewById<Chip?>(checkedId)
        return if (chip != null) Integer.toHexString(chip.chipBackgroundColor!!.defaultColor) else null
    }

    private fun showLabelSelected(label: Label, binding: FragmentEditLabelsBinding) {
        binding.nameField.setText(label.name)
        (binding.chipGroup.children.find { view ->
            (view as Chip).chipBackgroundColor?.defaultColor == Color.parseColor(label.colorHex)
        } as Chip).isChecked = true
    }

    private fun clearAddLabelForm(binding: FragmentEditLabelsBinding) {
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