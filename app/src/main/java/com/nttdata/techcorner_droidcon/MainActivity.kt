package com.nttdata.techcorner_droidcon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nttdata.techcorner_droidcon.feature.home.data.data_source.remote.HomeRemoteDataSourceImpl
import com.nttdata.techcorner_droidcon.feature.home.data.repository.HomeRepositoryImpl
import com.nttdata.techcorner_droidcon.feature.home.domain.use_case.GetMovies
import com.nttdata.techcorner_droidcon.feature.home.presentation.home.HomeListView
import com.nttdata.techcorner_droidcon.feature.home.presentation.home.HomeViewModel
import com.nttdata.techcorner_droidcon.ui.theme.TechCornerDroidconTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val vm = HomeViewModel(
            getMoviesUseCase = GetMovies(
                repository = HomeRepositoryImpl(
                    dataSource = HomeRemoteDataSourceImpl()
                )
            )
        )

        super.onCreate(savedInstanceState)
        setContent {
            TechCornerDroidconTheme {
                HomeListView(vm = vm)
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TechCornerDroidconTheme {
        Greeting("Android")
    }
}