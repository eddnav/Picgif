package com.eddnav.picgif.remote

import com.eddnav.picgif.remote.gif.model.RandomResponse
import com.eddnav.picgif.remote.gif.model.TrendingResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Eduardo Naveda
 */
interface GiphyService {

    @GET("/v1/gifs/trending")
    fun trending(@Query("offset") offset: Int, @Query("limit") limit: Int): Single<TrendingResponse>

    @GET("/v1/gifs/random")
    fun random(): Single<RandomResponse>

    companion object {

        const val BASE_URL = "https://api.giphy.com"
        const val HOST = "api.giphy.com"
        const val API_KEY_PARAM = "api_key"
        const val API_KEY = "dc6zaTOxFJmzC"
    }
}