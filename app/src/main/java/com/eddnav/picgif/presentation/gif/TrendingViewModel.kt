package com.eddnav.picgif.presentation.gif

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.data.gif.repository.GifRepository
import com.eddnav.picgif.presentation.Data
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author Eduardo Naveda
 */
class TrendingViewModel @Inject constructor(private val gifRepository: GifRepository) : ViewModel() {

    var loadObservable: Disposable? = null

    private var totalOffset = 0
    private val current: MutableList<Gif> = mutableListOf()

    val trendingUpdates: MutableLiveData<Data<List<Gif>>> = MutableLiveData()
    val isLoading: MutableLiveData<LoadingEvent> = MutableLiveData()

    fun initialize() {
        load(0)
    }

    fun initializeCurrent() {
        trendingUpdates.value = Data(current, Data.Type.NEW)
    }

    fun load() {
        load(null)
    }

    private fun load(from: Int? = null) {
        if (isAvailable()) {
            val offset = from ?: totalOffset
            val isInitial = offset == 0
            isLoading.value = LoadingEvent(true, isInitial)
            loadObservable = gifRepository.trending(offset, LIMIT_PER_PAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    // Filter elements that were pushed down by new trending gifs.
                    .map {
                        it.filter { g ->
                            !current.any {
                                it.id == g.id
                            }
                        }
                    }.subscribe(
                            {
                                // Keep increasing the non-filtered total we have requested to
                                // avoid 0 size filtered lists endless loops.
                                totalOffset += LIMIT_PER_PAGE
                                current.addAll(it)
                                isLoading.value = LoadingEvent(false, isInitial)
                                trendingUpdates.value = Data(it, Data.Type.UPDATE)

                                // If we get a filtered, smaller update (under half) due to items being pushed down in
                                // the trending list, automatically start a new load request.
                                if (it.size < LIMIT_PER_PAGE / 2 && it.isNotEmpty()) load()
                            },
                            {
                                isLoading.value = LoadingEvent(false, isInitial)
                                trendingUpdates.value = Data(null, Data.Type.ERROR)
                            }
                    )
        }
    }

    fun isAvailable() = loadObservable == null || loadObservable?.isDisposed == true

    data class LoadingEvent(val state: Boolean, val initial: Boolean)

    override fun onCleared() {
        loadObservable?.dispose()
        super.onCleared()
    }

    companion object {
        const val LIMIT_PER_PAGE = 31
    }
}