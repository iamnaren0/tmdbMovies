package com.example.tmdbmovies.ui.theme.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.tmdbmovies.MovieViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MovieViewModel, onBack: () -> Unit, navController: NavController) {
    var query by remember { mutableStateOf("") }
    var searchJob: Job? by remember { mutableStateOf(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TopAppBar(
            title = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Search Movies".toUpperCase(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            },
        )
        val isLoading by viewModel.isLoading.collectAsState()

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            BasicTextField(
                value = query,
                onValueChange = {
                    query = it
                    searchJob?.cancel()
                    searchJob = viewModel.viewModelScope.launch {
                        delay(500) // Wait for 500ms before making the network call
                        viewModel.searchMovies(query)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray)
                    .padding(8.dp),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black)
            )
            Spacer(modifier = Modifier.height(16.dp))
            val searchResults by viewModel.searchResults.collectAsState()
            searchResults?.results?.let { movies ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(movies) { movie ->
                        MovieListItem(movie = movie, onClick = {
                            navController.navigate("details/${movie.id}")
                        })
                    }
                }
            }
        }
    }
}
