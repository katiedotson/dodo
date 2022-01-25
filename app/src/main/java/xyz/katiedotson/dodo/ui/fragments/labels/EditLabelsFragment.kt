package xyz.katiedotson.dodo.ui.fragments.labels

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import xyz.katiedotson.dodo.R
import xyz.katiedotson.dodo.ui.base.BaseFragment
import xyz.katiedotson.dodo.databinding.FragmentEditLabelsBinding

class EditLabelsFragment : BaseFragment(R.layout.fragment_edit_labels) {

    private val viewModel: EditLabelsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEditLabelsBinding.bind(view)
    }

}