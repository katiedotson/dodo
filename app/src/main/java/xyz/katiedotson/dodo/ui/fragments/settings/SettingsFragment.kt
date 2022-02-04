package xyz.katiedotson.dodo.ui.fragments.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.data.usersettings.SortSetting
import xyz.katiedotson.dodo.databinding.FragmentSettingsBinding
import xyz.katiedotson.dodo.ui.base.BaseFragment

@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: FragmentSettingsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSettingsBinding.bind(view)

        viewModel.userSettings.observe(viewLifecycleOwner) {
            val id = sortSettingToRadioId()[it.sortSetting]
            binding.radioGroup.check(id!!)
            binding.filteringByLabelsCheckBox.isChecked = it.allowFilteringByLabels
            binding.showDueDateCheckBox.isChecked = it.showDueDate
            binding.showDateCreatedCheckBox.isChecked = it.showDateCreated
            binding.showNotesCheckBox.isChecked = it.showNotes
            binding.showLabelCheckBox.isChecked = it.showLabel
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val sortSetting = sortSettingToRadioId().entries.find { it.value == checkedId }
            viewModel.onSortSettingChanged(sortSetting!!.key)
        }

        binding.filteringByLabelsCheckBox.setOnCheckedChangeListener { _, checked ->
            viewModel.onAllowFilterByLabelsChanged(checked)
        }

        binding.showDueDateCheckBox.setOnCheckedChangeListener { _, checked ->
            viewModel.onShowDueDateChanged(checked)
        }

        binding.showDateCreatedCheckBox.setOnCheckedChangeListener { _, checked ->
            viewModel.onShowDateCreated(checked)
        }

        binding.showLastUpdateCheckBox.setOnCheckedChangeListener { _, checked ->
            viewModel.onShowLastUpdateChanged(checked)
        }

        binding.showLabelCheckBox.setOnCheckedChangeListener { _, checked ->
            viewModel.onShowLabelChanged(checked)
        }

        binding.showNotesCheckBox.setOnCheckedChangeListener { _, checked ->
            viewModel.onShowNotesChanged(checked)
        }


    }

    private fun sortSettingToRadioId() =
        mapOf(
            SortSetting.Alphabetical to binding.alphabeticalSortRadio.id,
            SortSetting.DueDate to binding.dueDateSortRadio.id,
            SortSetting.DateCreated to binding.dateCreatedSortRadio.id,
            SortSetting.LastUpdate to binding.lastUpdateRecentSortRadio.id
        )


}