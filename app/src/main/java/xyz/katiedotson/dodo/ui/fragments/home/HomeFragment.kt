package xyz.katiedotson.dodo.ui.fragments.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.ui.base.BaseFragment
import xyz.katiedotson.dodo.data.todo.Todo
import xyz.katiedotson.dodo.databinding.FragmentHomeBinding
import xyz.katiedotson.dodo.ui.fragments.home.recycler.TodoAdapter
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment @Inject constructor() : BaseFragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private val args: HomeFragmentArgs by navArgs()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        if (args.newTodoId != 0L) {
            showSuccess(binding.root)
        }

        val adapter = TodoAdapter(object : TodoAdapter.TodoClickListeners {
            override fun onEditButtonClicked(todo: Todo) {
                val directions = HomeFragmentDirections.actionHomeFragmentToAddEditFragment(todo.id)
                mainNavController.navigate(directions)
            }

            override fun onDeleteButtonClicked(todo: Todo) {
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(true)
                    .setTitle("Delete this item?")
                    .setMessage("Are you sure you want to delete this item?")
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Yes, Delete") { _, _ ->
                        viewModel.deleteTodo(todo)
                    }
                    .show()
            }
        })
        binding.recycler.adapter = adapter


        viewModel.todos.observe(viewLifecycleOwner) { todos ->
            adapter.submitList(todos)
        }
        viewModel.deleteEvent.observe(viewLifecycleOwner) {
            when (it.getContentIfNotHandled()) {
                is HomeViewModel.DeleteEvent.Success -> {
                    Snackbar.make(binding.root, "a dodo was deleted", Snackbar.LENGTH_SHORT).show()
                }
                is HomeViewModel.DeleteEvent.Failure -> {
                    Snackbar.make(binding.root, "Something went wrong.", Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    // no op
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding?.recycler?.adapter = null
        _binding = null

    }

}