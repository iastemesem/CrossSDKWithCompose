package com.nttdata.techcorner_droidcon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nttdata.techcorner_droidcon.feature.home.data.data_source.remote.HomeRemoteDataSourceImpl
import com.nttdata.techcorner_droidcon.feature.home.data.repository.HomeRepositoryImpl
import com.nttdata.techcorner_droidcon.feature.home.domain.use_case.GetMovies
import com.nttdata.techcorner_droidcon.feature.home.presentation.home.HomeListView
import com.nttdata.techcorner_droidcon.feature.home.presentation.home.HomeViewModel
import com.nttdata.techcorner_droidcon.feature.home.presentation.model.SessionTwoUsersMovieManager
import com.nttdata.techcorner_droidcon.ui.theme.TechCornerDroidconTheme

class MainActivity : ComponentActivity() {

    private lateinit var vm: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = HomeViewModel(
            getMoviesUseCase = GetMovies(
                repository = HomeRepositoryImpl(
                    dataSource = HomeRemoteDataSourceImpl()
                )
            )
        )

        val accessibilityManager =
            this.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        setContent {
            TechCornerDroidconTheme {
                HomeListView(
                    vm = vm,
                    talkbackEnabled = (accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled)
                )
            }
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        Log.d("GIAN     ", "handleIntent: ")
        intent?.let {
            Log.d("GIAN", "handleIntent: ${intent.action}")
            if (SessionTwoUsersMovieManager._action.equals(intent.action, ignoreCase = true)) {
                //sessionManager.acceptSessionInvitation(intent)
            }
        }
    }
}