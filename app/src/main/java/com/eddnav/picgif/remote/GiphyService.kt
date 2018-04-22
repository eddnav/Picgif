package com.eddnav.picgif.remote

import com.eddnav.picgif.remote.animations.RandomResponse
import com.eddnav.picgif.remote.animations.TrendingResponse
import retrofit2.http.GET

/**
 * @author Eduardo Naveda
 */
interface GiphyService {

    @GET("/v1/gifs/trending")
    fun trending() : TrendingResponse

    @GET("/v1/gifs/random")
    fun random() : RandomResponse

    companion object {
        const val BASE_URL = "api.giphy.com"
        const val API_KEY_PARAM = "api_key"
        const val API_KEY = "dc6zaTOxFJmzC"
    }
}