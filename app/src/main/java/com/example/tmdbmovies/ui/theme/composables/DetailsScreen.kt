package com.example.tmdbmovies.ui.theme.composables

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tmdbmovies.MovieViewModel
import androidx.compose.material3.CircularProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    movieId: Int,
    viewModel: MovieViewModel,
    onBack: () -> Unit,
    onBookmark: (Int) -> Unit,
    onRemoveBookmark: (Int) -> Unit
) {
    val movie by viewModel.movieDetails.collectAsState()
    LaunchedEffect(movieId) {
        viewModel.fetchMovieDetails(movieId)
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val context = LocalContext.current

        TopAppBar(
            title = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = movie?.title?.toUpperCase() ?: "MOVIE DETAILS",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                val isBookmarked by viewModel.bookmarkedMovies.collectAsState()
                val bookmarked = isBookmarked.any { it.id == movie?.id }

                IconButton(onClick = {
                    movie?.id?.let {
                        if (bookmarked) {
                            onRemoveBookmark(it)
                        } else {
                            onBookmark(it)
                        }
                    }
                }) {
                    Icon(
                        Icons.Default.Bookmark,
                        contentDescription = "Bookmark",
                        tint = if (bookmarked) Color.Yellow else Color.Gray
                    )
                }
                IconButton(onClick = {
                    movie?.let {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Check out this movie: ${it.title}. Link: https://example.com/movie/${it.id}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Movie"))
                    }
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        )
        if (movie == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val posterUrl = movie?.poster_path?.takeIf { it.isNotBlank() }?.let { "https://image.tmdb.org/t/p/w500$it" }

            if (posterUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = posterUrl,
                        error = painterResource(id = android.R.drawable.ic_menu_report_image),
                        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                    ),
                    contentDescription = "Movie Poster",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(movie?.overview ?: "Loading...")
        }
    }
}