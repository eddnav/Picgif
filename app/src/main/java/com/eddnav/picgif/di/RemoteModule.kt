package com.eddnav.picgif.di

import com.eddnav.picgif.remote.GiphyService
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Eduardo Naveda
 */
@Module
class RemoteModule {

    @Application
    @Provides
    fun okHttpClient(): OkHttpClient {
        val apiKeyInterceptor = Interceptor({
            val original = it.request()
            val originalUrl = original.url()

            if (originalUrl.host() == GiphyService.HOST) {
                val url = original.url().newBuilder()
                        .addQueryParameter(GiphyService.API_KEY_PARAM, GiphyService.API_KEY)
                        .build()
                it.proceed(original.newBuilder().url(url).build())
            } else {
                it.proceed(original)
            }
        })
        return OkHttpClient().newBuilder()
                .addInterceptor(apiKeyInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
    }

    @Application
    @Provides
    fun retrofit(okhttp: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(okhttp)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(GiphyService.BASE_URL).build()
    }

    @Application
    @Provides
    fun giphyService(retrofit: Retrofit): GiphyService = retrofit.create(GiphyService::class.java)
}