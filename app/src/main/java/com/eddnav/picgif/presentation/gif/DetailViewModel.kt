package com.eddnav.picgif.presentation.gif

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.data.gif.repository.GifRepository
import com.eddnav.picgif.presentation.Data
import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author Eduardo Naveda
 */
class DetailViewModel @Inject constructor(private val gifRepository: GifRepository) : ViewModel() {

    private val disposables = CompositeDisposable()
    private var intervalDisposable: Disposable? = null

    private var interval = 0L
    private var fetching = false
    var next: Gif? = null
        private set

    val intervalUpdates: MutableLiveData<Long> = MutableLiveData()
    val currentUpdates: MutableLiveData<Data<Gif>> = MutableLiveData()

    fun start() {
        if (next == null) fetchRandom()
        val interval = Observable.interval(1, TimeUnit.SECONDS)
        intervalDisposable = interval
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // This will take an extra second, but the user sees 10 and 0 and it's
                    // clearer than finishing at 1.
                    val seconds = SECONDS_PER_CHANGE - this.interval % (SECONDS_PER_CHANGE + 1)
                    if (seconds == 0L) {
                        currentUpdates.value = Data(next, Data.Type.UPDATE)
                        fetchRandom()
                    }
                    intervalUpdates.value = seconds
                    this.interval++
                })
    }

    fun pause() {
        intervalDisposable?.dispose()
    }

    private fun fetchRandom() {
        if (!fetching) {
            fetching = true
            gifRepository.random()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : SingleObserver<Gif> {
                        override fun onSubscribe(disposable: Disposable) {
                            disposables.add(disposable)
                        }

                        override fun onSuccess(gif: Gif) {
                            fetching = false
                            next = gif
                        }

                        override fun onError(e: Throwable) {
                            fetching = false
                            currentUpdates.value = Data(null, Data.Type.ERROR)
                        }
                    })
        }
    }

    override fun onCleared() {
        intervalDisposable?.dispose()
        disposables.dispose()
        super.onCleared()
    }

    companion object {
        const val SECONDS_PER_CHANGE = 10L
    }
}