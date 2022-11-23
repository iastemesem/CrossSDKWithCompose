package com.nttdata.techcorner_droidcon.feature.home.presentation.home

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.nttdata.techcorner_droidcon.feature.home.domain.entity.Movie

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
@Composable
fun HomeListView(
    vm: HomeViewModel,
    talkbackEnabled: Boolean
) {
    val cartCounter: String? by vm.cartCounter.observeAsState(null)
    val movieList: List<Movie>? by vm.movies.observeAsState(null)
    val showAddSnackBar: Pair<Boolean, String?> by vm.showAddSnackBar.observeAsState(Pair(false, null))
    val showRemoveSnackBar: Pair<Boolean, String?> by vm.showRemoveSnackBar.observeAsState(Pair(false, null))

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(
                        0xFFFFC107
                    )
                ),
                title = {
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = "Movies", style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = TextUnit(value = 28f, type = TextUnitType.Sp)
                        )
                    )
                },
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

                        IconButton(onClick = {
                            Log.d("GIAN", "HomeListView: ")
                            vm.findOtherDevices()
                        }) {
                            Icon(
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .semantics {
                                        onClick(label = "per condividere la tua sessione") {
                                            Log.d("GIAN", "on share clicked")
                                            vm.findOtherDevices()
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
                        return@onClick true
                    }
                },
                containerColor = Color(0xFFFFAB40)
            ) {
                BadgedBox(badge = {
                    Badge() {
                        Text(text = cartCounter.toString())
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Carrello",
                        tint = Color.White
                    )
                }
            }
        }
    ) { padding ->
        LaunchedEffect(Unit, block = {
            vm.getMovies()
        })

        Column() {
            LazyColumn(
                modifier = Modifier.padding(
                    top = padding.calculateTopPadding(),
                    end = 20.dp,
                    start = 20.dp
                )
            ) {
                items(movieList!!.size) { index ->
                    MovieItemView(movieList!![index], vm)
                }
            }
            val toast = Toast.makeText(
                LocalContext.current,
                "",
                Toast.LENGTH_SHORT
            )
            if (showAddSnackBar.first) {
                Log.d("TECH_CORNER", "HomeListView: showAddSnackBar")
                toast.cancel()
                toast.setText("${showAddSnackBar.second} è stato aggiunto!")
                toast.show()
            }

            if (showRemoveSnackBar.first) {
                Log.d("TECH_CORNER", "HomeListView: showAddSnackBar")
                toast.cancel()
                toast.setText("${showRemoveSnackBar.second} è stato rimosso!")
                toast.show()
            }
        }
    }
}