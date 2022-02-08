package xyz.katiedotson.dodo.ui.fragments.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.data.dto.DodoError
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

        viewModel.userSettingsLoadEvent.observe(viewLifecycleOwner) {
            when (it.getContentIfNotHandled()) {
                is SettingsViewModel.UserSettingsLoadEvent.Success -> {
                    val value = it.content as SettingsViewModel.UserSettingsLoadEvent.Success
                    setInitialState(value, binding)
                }
                is SettingsViewModel.UserSettingsLoadEvent.Failure -> {
                    showError(binding.root, DodoError.DATABASE_ERROR)
                }
                else -> {
                    // no op
                }
            }
        }
    }

    private fun setInitialState(value: SettingsViewModel.UserSettingsLoadEvent.Success, binding: FragmentSettingsBinding) {
        val id = sortSettingToRadioId()[value.userSettingsDto.sortSetting]
        binding.radioGroup.check(id!!)
        binding.filteringByLabelsCheckBox.isChecked = value.userSettingsDto.allowFilteringByLabels
        binding.showDueDateCheckBox.isChecked = value.userSettingsDto.showDueDate
        binding.showDateCreatedCheckBox.isChecked = value.userSettingsDto.showDateCreated
        binding.showLastUpdateCheckBox.isChecked = value.userSettingsDto.showLastUpdate
        binding.showNotesCheckBox.isChecked = value.userSettingsDto.showNotes
        binding.showLabelCheckBox.isChecked = value.userSettingsDto.showLabel

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