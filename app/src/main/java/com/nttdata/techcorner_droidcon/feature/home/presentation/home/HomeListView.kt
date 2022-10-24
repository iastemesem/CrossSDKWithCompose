package com.nttdata.techcorner_droidcon.feature.home.presentation.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeListView(vm: HomeViewModel) {
    LaunchedEffect(Unit, block = {
        vm.getMovies()
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Movies")
                },
            )
        },
        containerColor = Color.LightGray,
    ) { innerPadding ->
        LazyColumn(Modifier.padding(top = innerPadding.calculateTopPadding())) {
            items(vm.movies.size) { index ->
                BuildFilmItemView(vm, index)
            }
        }

    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun BuildFilmItemView(
    vm: HomeViewModel,
    index: Int
) {
    val movie = vm.movies[index]
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(all = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                Image(
                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/original${movie.posterImageUrl}"),
                    contentDescription = null,
                    modifier = Modifier.size(width = 150.dp, height = 150.dp)
                )
                Column(modifier = Modifier.padding(start = 4.dp)) {
                    Text(
                        text = movie.title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(value = 20f, type = TextUnitType.Sp)
                        )
                    )
                    Text(
                        text = movie.description,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(movie.voteAverage)
                    Text(movie.voteCount)
                    Text(movie.releaseDate)
                    FloatingActionButton(onClick = { Log.d("GIAN", "BuildFilmItemView: ") }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                    }
                }
            }
        }
    }
}