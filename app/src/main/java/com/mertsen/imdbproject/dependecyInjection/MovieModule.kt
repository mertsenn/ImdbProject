package com.mertsen.imdbproject.dependecyInjection

import com.mertsen.imdbproject.service.MovieService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

// SingletonComponent, uygulamanın genelinde tek bir örnek oluşturan Hilt bileşeni türünü temsil eder
@InstallIn(SingletonComponent::class)
@Module
class MovieModule {

    // MovieRetrofit bağımlılığının sağlanması
    @Provides
    @Named("movieRetrofit")
    fun provideMovieRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/") // API'nin temel URL'si
            .addConverterFactory(GsonConverterFactory.create()) // JSON dönüşümü için Gson kullanımı
            .build()
    }

    // MovieService bağımlılığının sağlanması
    @Provides
    @Named("movieService")
    fun provideMyMovieService(@Named("movieRetrofit") retrofit: Retrofit): MovieService {
        return retrofit.create(MovieService::class.java)
    }

    // MovieService bağımlılığının direkt sağlanması
    @Provides
    fun provideMovieService(@Named("movieService") movieService: MovieService): MovieService {
        return movieService
    }
}
