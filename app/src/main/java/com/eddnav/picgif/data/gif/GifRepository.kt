package com.eddnav.picgif.data.gif

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.remote.GiphyService
import com.eddnav.picgif.remote.gif.mapper.RemoteMapper
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * // TODO: Add local database for trending result caching.
 * @author Eduardo Naveda
 */
class GifRepository @Inject constructor(private val api: GiphyService) {

    fun trending(): LiveData<Data<List<Gif>>> {
        return LiveDataReactiveStreams.fromPublisher(
                api.trending()
                        .map { Data(it.data.map { RemoteMapper.toData(it) }, Data.Status.OK) }
                        .onErrorReturn { Data<List<Gif>>(null, Data.Status.ERROR) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toFlowable(BackpressureStrategy.DROP))
    }

    fun random(): LiveData<Data<Gif>> {
        return LiveDataReactiveStreams.fromPublisher(
                api.random()
                        .map { Data(RemoteMapper.toData(it.data), Data.Status.OK) }
                        .onErrorReturn { Data<Gif>(null, Data.Status.ERROR) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toFlowable(BackpressureStrategy.DROP))
    }
}