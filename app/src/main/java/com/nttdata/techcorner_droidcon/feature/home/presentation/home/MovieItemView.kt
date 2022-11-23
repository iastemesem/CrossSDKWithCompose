package com.nttdata.techcorner_droidcon.feature.home.presentation.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nttdata.techcorner_droidcon.feature.home.domain.entity.Movie

@OptIn(ExperimentalUnitApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MovieItemView(movie: Movie, vm: HomeViewModel) {
    val moviesCartId = vm.moviesCartId
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
        border = if (moviesCartId.isNotEmpty() && moviesCartId.contains(movie.id)) BorderStroke(
            1.dp,
            color = Color.Green
        ) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        onClick = {

        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/original${movie.posterImageUrl}"),
                contentDescription = null,
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Column(modifier = Modifier
                .padding(all = 12.dp)
                .semantics(mergeDescendants = true) {}) {

                Text(
                    text = movie.title,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = TextUnit(value = 25f, type = TextUnitType.Sp)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = movie.description,
                    modifier = Modifier.clearAndSetSemantics { this.contentDescription = "" },
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = TextUnit(value = 18f, type = TextUnitType.Sp)
                    )
                )
            }

            Spacer(modifier = Modifier.height(height = 4.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                .semantics(mergeDescendants = true) {}) {
                MovieItemDetail(
                    content = movie.voteAverage,
                    mContentDescription = "Media voto ${movie.voteAverage}",
                    icon = Icons.Filled.Star
                )
                MovieItemDetail(
                    content = movie.voteCount,
                    mContentDescription = "Conteggio voti ${movie.voteCount}",
                    icon = Icons.Filled.Person
                )
                MovieItemDetail(
                    content = movie.releaseDate,
                    mContentDescription = "Data di rilascio ${movie.releaseDate}",
                    icon = Icons.Filled.DateRange
                )
            }

            Spacer(modifier = Modifier.fillMaxWidth())

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp, start = 12.dp, bottom = 12.dp)
            ) {
                Button(
                    modifier = Modifier.semantics {
                        onClick("per rimuovere ${movie.title} dal carrello") {
                            vm.onRemoveClicked(movie.id)
                            return@onClick true
                        }
                    },
                    enabled = moviesCartId.isNotEmpty() && moviesCartId.contains(movie.id),
                    onClick = {
                        vm.onRemoveClicked(movie.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFAB40)
                    ),
                ) {
                    Text(text = "Rimuovi")
                }
                Spacer(modifier = Modifier.width(width = 8.dp))
                Button(
                    modifier = Modifier.semantics {
                        onClick("per aggiungere ${movie.title} al carrello") {
                            vm.onAddClicked(movie.id)
                            return@onClick true
                        }
                    },
                    enabled = moviesCartId.isEmpty() || !moviesCartId.contains(movie.id),
                    onClick = {
                        vm.onAddClicked(movie.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFAB40)
                    )
                ) {
                    Text(text = "Aggiungi")
                }

            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun MovieItemDetail(
    content: String,
    mContentDescription: String,
    icon: ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(width = 25.dp, height = 25.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            modifier = Modifier.semantics {
                contentDescription = mContentDescription
            }, text = content,
            style = TextStyle(
                fontWeight = FontWeight.Light,
                fontSize = TextUnit(value = 16f, type = TextUnitType.Sp)
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
    }
}