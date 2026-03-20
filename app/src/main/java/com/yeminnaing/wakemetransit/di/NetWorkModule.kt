package com.yeminnaing.wakemetransit.di

import com.yeminnaing.wakemetransit.datalayer.NominatimRetrofit
import com.yeminnaing.wakemetransit.datalayer.RouterRetrofit
import com.yeminnaing.wakemetransit.datalayer.remote.NominatimApi
import com.yeminnaing.wakemetransit.datalayer.remote.RouteApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetWorkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder().addInterceptor(logging).addInterceptor { chain ->
                val request =
                    chain.request().newBuilder().header("User-Agent", "WakeMeUpTransit").build()

                chain.proceed(request)
            }.build()
    }

    @NominatimRetrofit
    @Provides
    @Singleton
    fun provideNominatimRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().client(client).baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @RouterRetrofit
    @Provides
    @Singleton
    fun provideRouteRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().client(client).baseUrl("https://router.project-osrm.org/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideNominatimApi(
        @NominatimRetrofit retrofit: Retrofit,
    ): NominatimApi {
        return retrofit.create(NominatimApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRouteRetrofitApi(
        @RouterRetrofit retrofit: Retrofit,
    ): RouteApi {
        return retrofit.create(RouteApi::class.java)
    }
}

