package xyz.katiedotson.dodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadingActivity : AppCompatActivity() {

    private val activityViewModel: LoadingActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        activityViewModel.mediatorLiveData.observe(this) {
            if (it.colorsHandled && it.userSettingsHandled) {
                startActivity(Intent(this, MainActivity::class.java).also { intent ->
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }
}