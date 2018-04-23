package com.eddnav.picgif.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eddnav.picgif.data.gif.Data
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.data.gif.repository.GifRepository
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author Eduardo Naveda
 */
class TrendingViewModel @Inject constructor(private val gifRepository: GifRepository) : ViewModel() {

    private val disposables: CompositeDisposable = CompositeDisposable()

    val current: MutableList<Gif> = mutableListOf()

    val trendingUpdates: MutableLiveData<Data<List<Gif>>> = MutableLiveData()
    val isLoading: MutableLiveData<LoadingEvent> = MutableLiveData()

    init {
        isLoading.value = LoadingEvent(false, false)
    }

    fun initialize() {
        if (current.isEmpty()) load(0)
        else trendingUpdates.value = Data(current, Data.Status.OK)
    }

    fun load(from: Int? = null) {
        if (!isLoading.value!!.state) {
            // Giphy API's offset is inclusive (from, rather from next of),
            // so we add +1 as not to fetch an image we already have.
            val offset = from ?: current.size + 1
            val isInitial = offset == 0
            isLoading.value = LoadingEvent(true, isInitial)
            gifRepository.trending(offset, LIMIT_PER_PAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    // Filter images that were pushed down by new trending gifs.
                    .map { it.filter { g ->
                            !current.any {
                                it.id == g.id
                            }
                        }
                    }
                    .subscribeWith(object : SingleObserver<List<Gif>> {
                        override fun onSubscribe(disposable: Disposable) {
                            disposables.add(disposable)
                        }

                        override fun onSuccess(update: List<Gif>) {
                            current.addAll(update)
                            isLoading.value = LoadingEvent(false, isInitial)
                            trendingUpdates.value = Data(update, Data.Status.OK)
                            // If we get a filtered, smaller update due to items being pushed down in
                            // the trending list, automatically start a new load request.
                            if (update.size < LIMIT_PER_PAGE / 2) load()
                        }

                        override fun onError(e: Throwable) {
                            isLoading.value = LoadingEvent(false, isInitial)
                            trendingUpdates.value = Data(null, Data.Status.ERROR)
                        }
                    })
        }
    }

    data class LoadingEvent (val state: Boolean, val initial: Boolean)

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    companion object {
        const val LIMIT_PER_PAGE = 26
    }
}