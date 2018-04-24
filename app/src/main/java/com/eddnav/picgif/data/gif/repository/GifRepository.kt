package com.eddnav.picgif.data.gif.repository

import com.eddnav.picgif.data.gif.Data
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.remote.GiphyService
import com.eddnav.picgif.remote.gif.mapper.RemoteMapper
import io.reactivex.Single
import javax.inject.Inject

/**
 * // TODO: Add local database for trending result caching.
 * @author Eduardo Naveda
 */
class GifRepository @Inject constructor(private val api: GiphyService) {

    fun trending(offset: Int, limit: Int): Single<List<Gif>> {
        return api.trending(offset, limit)
                .map { it.data.map { RemoteMapper.toData(it) } }
    }

    fun random(): Single<Gif> {
        return api.random()
                .map { RemoteMapper.toData(it.data) }
    }
}