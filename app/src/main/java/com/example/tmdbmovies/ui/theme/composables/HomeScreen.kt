package com.example.tmdbmovies.ui.theme.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tmdbmovies.MovieViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit,
    onBookmarksClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val trending by viewModel.trendingMovies.collectAsState()
    val nowPlaying by viewModel.nowPlayingMovies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTrendingMovies()
        viewModel.fetchNowPlayingMovies()
    }

    if (isLoading) {
        Loader()
    } else {
        // Use LazyColumn for the whole content to enable scrolling of all content
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            item {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Welcome to Movie Apps".toUpperCase(),
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.TopStart)
                            )
                        }
                    },
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TRENDING MOVIES",
                        modifier = Modifier.weight(1f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(onClick = onBookmarksClick) {
                        Icon(Icons.Default.Bookmarks, contentDescription = "Bookmarks")
                    }
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
                trending?.results?.let { movies ->
                    AutoScrollingRow(movies = movies, onMovieClick = onMovieClick)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "NOW PLAYING MOVIES",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            nowPlaying?.results?.let { movies ->
                items(movies) { movie ->
                    MovieListItem(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
        }
    }
}

@Composable
fun Loader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun MovieListItem(movie: com.example.data.Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(250.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
        ) {
            val posterUrl = movie.poster_path?.takeIf { it.isNotBlank() }?.let { "https://image.tmdb.org/t/p/w500$it" }

            Image(
                painter = rememberAsyncImagePainter(
                    model = posterUrl,
                    error = painterResource(id = android.R.drawable.ic_menu_report_image),
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                ),
                contentDescription = "Movie Poster",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.title ?: "Unknown Title",
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(start = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun AutoScrollingRow(movies: List<com.example.data.Movie>, onMovieClick: (Int) -> Unit) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextScroll = (scrollState.value + 500).coerceAtMost(scrollState.maxValue)
            scrollState.animateScrollTo(nextScroll)
        }
    }

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .fillMaxWidth()
    ) {
        movies.forEach { movie ->
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(260.dp)
                    .background(color = Color.White)
                    .padding(8.dp)
                    .clickable { onMovieClick(movie.id) },
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                            error = painterResource(id = android.R.drawable.ic_menu_report_image),
                            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                        ),
                        contentDescription = "Movie Poster",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(215.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = movie.title ?: "Unknown Title",
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(start = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
