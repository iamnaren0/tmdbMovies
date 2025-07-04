package com.example.tmdbmovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tmdbmovies.ui.theme.TmdbMoviesTheme
import com.example.tmdbmovies.ui.theme.composables.BookmarksScreen
import com.example.tmdbmovies.ui.theme.composables.DetailsScreen
import com.example.tmdbmovies.ui.theme.composables.HomeScreen
import com.example.tmdbmovies.ui.theme.composables.SearchScreen
import com.example.tmdbmovies.ui.theme.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Details : Screen("details/{movieId}") {
        fun createRoute(movieId: Int) = "details/$movieId"
    }
    object Bookmarks : Screen("bookmarks")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startKoin {
            androidContext(this@MainActivity)
            modules(modules = appModule)
        }
        setContent {
            TmdbMoviesTheme {
                val navController = rememberNavController()
                val viewModel: MovieViewModel = koinViewModel()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = viewModel,
                                onMovieClick = { movieId ->
                                    navController.navigate(Screen.Details.createRoute(movieId))
                                },
                                onBookmarksClick = {
                                    navController.navigate(Screen.Bookmarks.route)
                                },
                                onSearchClick = {
                                     navController.navigate(Screen.Search.route)
                                }
                            )
                        }
                        composable(Screen.Details.route) { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                            if (movieId != null) {
                                DetailsScreen(
                                    movieId = movieId,
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() },
                                    onBookmark = { viewModel.bookmarkMovie(it) },
                                    onRemoveBookmark = { viewModel.removeBookmark(it) }
                                )
                            }
                        }
                        composable(Screen.Bookmarks.route) {
                            BookmarksScreen(
                                viewModel = viewModel,
                                onMovieClick = { movieId ->
                                    navController.navigate(Screen.Details.createRoute(movieId))
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Search.route) {
                            SearchScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
