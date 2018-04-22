package com.eddnav.picgif.remote

import com.eddnav.picgif.Application
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author Eduardo Naveda
 */
@Module
class RemoteModule {

    @Application
    @Provides
    fun OkHttpClient(): OkHttpClient {
        val apiKeyInterceptor = Interceptor({
            val original = it.request()
            val originalUrl = original.url()

            if (originalUrl.host() == GiphyService.BASE_URL) {
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
                .build()
    }

    @Application
    @Provides
    fun Retrofit(okhttp: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(okhttp)
                .baseUrl(GiphyService.BASE_URL).build()
    }

    @Application
    @Provides
    fun GiphyService(retrofit: Retrofit): GiphyService {
        return retrofit.create(GiphyService::class.java)
    }
}