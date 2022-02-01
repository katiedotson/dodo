package xyz.katiedotson.dodo.ui.fragments.home

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import xyz.katiedotson.dodo.MainActivityViewModel
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.common.extensions.toggleVisible
import xyz.katiedotson.dodo.data.todo.TodoDto
import xyz.katiedotson.dodo.databinding.FragmentHomeBinding
import xyz.katiedotson.dodo.ui.base.BaseFragment
import xyz.katiedotson.dodo.ui.fragments.home.recycler.TodoAdapter
import xyz.katiedotson.dodo.ui.views.LabelChip
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment @Inject constructor() : BaseFragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private val args: HomeFragmentArgs by navArgs()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityViewModel.initColors()

        _binding = FragmentHomeBinding.bind(view)

        if (args.newTodoId != 0L && args.newTodoId != -1L) {
            showSuccess(binding.root)
        }

        val adapter = TodoAdapter(object : TodoAdapter.TodoClickListeners {
            override fun onEditButtonClicked(todo: TodoDto) {
                val directions = HomeFragmentDirections.actionHomeFragmentToAddEditFragment(todo.id)
                mainNavController.navigate(directions)
            }

            override fun onDeleteButtonClicked(todo: TodoDto) {
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(true)
                    .setTitle(getString(R.string.home_delete_this_item))
                    .setMessage(getString(R.string.home_delete_confirm))
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(getString(R.string.home_delete_yes)) { _, _ ->
                        viewModel.deleteTodo(todo)
                    }
                    .show()
            }
        })
        binding.recycler.adapter = adapter

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.labels.collect {labels ->
                    labels.forEach { label ->
                        val chip = LabelChip(requireContext(), label, LabelChip.Mode.Choice)
                        binding.labelFilters.addView(chip)
                    }
                }
            }
        }

        with(viewModel) {
            todos.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding.emptyStateContainer.toggleVisible(it.count() == 0)
                binding.recycler.toggleVisible(it.count() != 0)
            }

            deleteEvent.observe(viewLifecycleOwner) {
                when (it.getContentIfNotHandled()) {
                    is HomeViewModel.DeleteEvent.Success -> {
                        showSuccess(binding.root, R.string.home_item_deleted)
                    }
                    is HomeViewModel.DeleteEvent.Failure -> {
                        showError(binding.root, (it.content as HomeViewModel.DeleteEvent.Failure).error)
                    }
                    else -> {
                        // no op
                    }
                }
            }
        }

        with(binding) {
            emptyStateCta.setOnClickListener {
                val directions = HomeFragmentDirections.actionHomeFragmentToAddEditFragment(0L)
                mainNavController.navigate(directions)
            }
            searchInput.doOnTextChanged { text, _, _, _ ->
                viewModel.searchTextChanged(text)
            }
            searchInputLayout.setEndIconOnClickListener {
                searchInput.text = null
                searchInput.clearFocus()
            }
            labelFilters.setOnCheckedChangeListener { _, checkedId ->
                val selectedChipColor = findSelectedChipColor(checkedId, this)
                viewModel.labelFilterChecked(selectedChipColor)
            }
        }

    }

    private fun findSelectedChipColor(checkedId: Int, binding: FragmentHomeBinding): String? {
        val chip = binding.labelFilters.findViewById<Chip?>(checkedId)
        return if (chip != null) Integer.toHexString(chip.chipBackgroundColor!!.defaultColor) else null
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding?.recycler?.adapter = null
        _binding = null

    }

}