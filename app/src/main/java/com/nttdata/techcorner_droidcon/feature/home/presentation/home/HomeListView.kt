package com.nttdata.techcorner_droidcon.feature.home.presentation.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
@Composable
fun HomeListView(
    vm: HomeViewModel,
    talkbackEnabled: Boolean
) {
    LaunchedEffect(Unit, block = {
        vm.getMovies()
    })

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(
                        0xFFFFC107
                    )
                ),
                title = {},
                actions = {
                    Row() {
                        if (talkbackEnabled)
                            IconButton(onClick = { Log.d("GIAN", "on add to cart clicked") }) {
                                Icon(
                                    modifier = Modifier
                                        .padding(all = 8.dp)
                                        .semantics {
                                            onClick(label = "per visualizzare il carrello") {
                                                Log.d("GIAN", "on cart clicked")
                                                return@onClick true
                                            }
                                        },
                                    tint = Color.White,
                                    imageVector = Icons.Filled.ShoppingCart,
                                    contentDescription = "carrello"
                                )
                            }

                        IconButton(onClick = { Log.d("GIAN", "HomeListView: ") }) {
                            Icon(
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .semantics {
                                        onClick(label = "per condividere la tua sessione") {
                                            Log.d("GIAN", "on share clicked")
                                            return@onClick true
                                        }
                                    },
                                tint = Color.White,
                                imageVector = Icons.Filled.Share,
                                contentDescription = "condividi"
                            )
                        }

                    }
                },
            )

        },
        containerColor = Color(0xFFFFC107),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { Log.d("GIAN", "on cart clicked") },
                modifier = Modifier.semantics {
                    onClick(label = "per visualizzare il carrello") {
                        Log.d("GIAN", "on cart clicked")
                        return@onClick true
                    }
                },
                containerColor = Color(0xFFFFAB40)
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Carrello",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                end = 20.dp,
                start = 20.dp
            )
        ) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = "Movies", style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = TextUnit(value = 28f, type = TextUnitType.Sp)
                )
            )
            LazyColumn() {
                items(vm.movies.size) { index ->
                    MovieItemView(vm.movies[index], vm)
                }
            }
        }
    }
}