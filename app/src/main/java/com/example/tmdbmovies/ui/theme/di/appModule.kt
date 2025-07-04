package com.example.tmdbmovies.ui.theme.di

import com.example.data.MovieApiService
import com.example.data.repo.MovieRepository
import com.example.data.repo.MovieRepositoryImpl
import com.example.data.usecase.GetMoviesUseCase
import com.example.data.usecase.GetTrendingMoviesUseCase
import com.example.data.usecase.GetNowPlayingMoviesUseCase
import com.example.data.usecase.GetMovieDetailsUseCase
import com.example.data.usecase.SearchMoviesUseCase
import com.example.tmdbmovies.MovieViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.room.Room
import com.example.data.room.AppDatabase
import com.example.data.room.MovieDao
import com.example.data.room.BookmarkedMovieDao


val appModule
    get() = module {
        single { androidApplication() }

        // OkHttpClient with logging and auth header
        single {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyZDIwODlmNTY0ZDQyZWRjMDMyMjU4MThjOWVmYmY0MiIsIm5iZiI6MTc1MTQ1OTQxNS4wMTQwMDAyLCJzdWIiOiI2ODY1MjY1NzczYWJlOTdiNWZlZWY4NGQiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.Qab0t9CJB-ASxfvUJ-1oqBURNLCPRC8fseDxPDHl1kg")
                        .build()
                    chain.proceed(request)
                }
                .build()
        }
        // Retrofit and API service
        single {
            Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .client(get())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        single<MovieApiService> { get<Retrofit>().create(MovieApiService::class.java) }

        // Repository
        single<MovieRepository> { MovieRepositoryImpl(get()) }

        // Use cases
        single { GetMoviesUseCase(get()) }
        single { GetTrendingMoviesUseCase(get()) }
        single { GetNowPlayingMoviesUseCase(get()) }
        single { GetMovieDetailsUseCase(get()) }
        single { SearchMoviesUseCase(get()) }


        // Room database
        single<AppDatabase> {
            Room.databaseBuilder(
                get(),
                AppDatabase::class.java,
                "movies_database"
            ).build()
        }
        single<MovieDao> { get<AppDatabase>().movieDao() }
        single<BookmarkedMovieDao> { get<AppDatabase>().bookmarkedMovieDao() }


        // ViewModel
        viewModel { MovieViewModel(get(), get(), get(), get(), get(), get(), get()) }

    }
