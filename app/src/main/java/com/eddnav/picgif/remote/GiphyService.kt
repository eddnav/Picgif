package com.eddnav.picgif.remote

import com.eddnav.picgif.remote.gif.model.RandomResponse
import com.eddnav.picgif.remote.gif.model.TrendingResponse
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * @author Eduardo Naveda
 */
interface GiphyService {

    @GET("/v1/gifs/trending")
    fun trending(): Observable<TrendingResponse>

    @GET("/v1/gifs/random")
    fun random(): Observable<RandomResponse>

    companion object {
        const val BASE_URL = "https://api.giphy.com"
        const val HOST = "api.giphy.com"
        const val API_KEY_PARAM = "api_key"
        const val API_KEY = "dc6zaTOxFJmzC"
    }
}