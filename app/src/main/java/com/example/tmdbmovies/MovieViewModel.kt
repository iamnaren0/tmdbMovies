package com.example.tmdbmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Movie
import com.example.data.MovieResponse
import com.example.data.usecase.GetMoviesUseCase
import com.example.data.usecase.GetTrendingMoviesUseCase
import com.example.data.usecase.GetNowPlayingMoviesUseCase
import com.example.data.usecase.GetMovieDetailsUseCase
import com.example.data.usecase.SearchMoviesUseCase
import com.example.data.room.MovieDao
import com.example.data.room.MovieEntity
import com.example.data.room.BookmarkedMovieDao
import com.example.data.room.BookmarkedMovieEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.toString

class MovieViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val movieDao: MovieDao, // Restored constructor parameter for MovieDao
    private val bookmarkedMovieDao: BookmarkedMovieDao // Dao for bookmarked movies
) : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> get() = _movies

    private val _trendingMovies = MutableStateFlow<MovieResponse?>(null)
    val trendingMovies: StateFlow<MovieResponse?> get() = _trendingMovies

    private val _nowPlayingMovies = MutableStateFlow<MovieResponse?>(null)
    val nowPlayingMovies: StateFlow<MovieResponse?> get() = _nowPlayingMovies

    private val _movieDetails = MutableStateFlow<Movie?>(null)
    val movieDetails: StateFlow<Movie?> get() = _movieDetails

    private val _searchResults = MutableStateFlow<MovieResponse?>(null)
    val searchResults: StateFlow<MovieResponse?> get() = _searchResults

    // Bookmarked movies state
    private val _bookmarkedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val bookmarkedMovies: StateFlow<List<Movie>> get() = _bookmarkedMovies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchMovies() {
        viewModelScope.launch {
            _movies.value = getMoviesUseCase.execute()
        }
    }

    fun fetchTrendingMovies(page: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = getTrendingMoviesUseCase.execute(page)
                _trendingMovies.value = response
                response?.results?.let { movies ->
                    movieDao.clearMovies() // Clear old data
                    movieDao.insertMovies(movies.map { MovieEntity(it.id, it.title, it.overview,
                        it.poster_path.toString(), it.releaseDate, it.voteAverage) })
                } ?: setUpTendingFromDB()
            } catch (e: Exception) {
                setUpTendingFromDB()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun setUpTendingFromDB(){
        _trendingMovies.value = MovieResponse(
            page = 1,
            results = movieDao.getMovies().map { Movie(it.id, it.title, it.overview, it.poster_path.toString(), it.releaseDate, it.voteAverage) },
            total_results = 0,
            total_pages = 1
        )
    }

    fun fetchNowPlayingMovies(page: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = getNowPlayingMoviesUseCase.execute(page)
                _nowPlayingMovies.value = response
                response?.results?.let { movies ->
                    movieDao.clearMovies() // Clear old data
                    movieDao.insertMovies(movies.map { MovieEntity(it.id, it.title, it.overview,
                        it.poster_path.toString(), it.releaseDate, it.voteAverage) })
                } ?: setUpNowPlayingFromDB()
            } catch (e: Exception) {
                setUpNowPlayingFromDB()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun setUpNowPlayingFromDB(){
        _nowPlayingMovies.value = MovieResponse(
            page = 1,
            results = movieDao.getMovies().map { Movie(it.id, it.title, it.overview,
                it.poster_path.toString(), it.releaseDate, it.voteAverage) },
            total_results = 0,
            total_pages = 1
        )
    }


    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _movieDetails.value = getMovieDetailsUseCase.execute(movieId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchMovies(query: String, page: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _searchResults.value = searchMoviesUseCase.execute(query, page)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // In-memory bookmark logic (replace with DB for persistence)
    fun bookmarkMovie(movieId: Int) {
        val movie = _movieDetails.value
        if (movie != null) {
            viewModelScope.launch {
                bookmarkedMovieDao.insertBookmarkedMovie(BookmarkedMovieEntity(
                    id = movie.id,
                    title = movie.title,
                    overview = movie.overview,
                    poster_path = movie.poster_path,
                    releaseDate = movie.releaseDate,
                    voteAverage = movie.voteAverage
                ))
                _bookmarkedMovies.value = _bookmarkedMovies.value + movie
            }
        }
    }

    fun removeBookmark(movieId: Int) {
        viewModelScope.launch {
            val movieToRemove = _bookmarkedMovies.value.find { it.id == movieId }
            if (movieToRemove != null) {
                bookmarkedMovieDao.deleteBookmarkedMovie(BookmarkedMovieEntity(
                    id = movieToRemove.id,
                    title = movieToRemove.title,
                    overview = movieToRemove.overview,
                    poster_path = movieToRemove.poster_path,
                    releaseDate = movieToRemove.releaseDate,
                    voteAverage = movieToRemove.voteAverage
                ))
                _bookmarkedMovies.value = _bookmarkedMovies.value.filterNot { it.id == movieId }
            }
        }
    }
}