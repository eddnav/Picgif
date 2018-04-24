package com.eddnav.picgif.presentation.gif

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eddnav.picgif.data.gif.Data
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.data.gif.repository.GifRepository
import io.reactivex.Observable
import io.reactivex.Observer
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
    private val interval = Observable.interval(1, TimeUnit.SECONDS)

    private var fetching = false
    private var current: Gif? = null

    val currentUpdates: MutableLiveData<Data<Gif>> = MutableLiveData()
    val intervalUpdates: MutableLiveData<Long> = MutableLiveData()

    fun initialize() {
        fetchRandom()
        interval.observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : Observer<Long> {
                    override fun onComplete() {}

                    override fun onSubscribe(disposable: Disposable) {
                        disposables.add(disposable)
                    }

                    override fun onNext(n: Long) {
                        // This will take an extra second, but the user sees 10 and 0 and it's
                        // clearer than finishing at 1.
                        val seconds = SECONDS_PER_CHANGE - n % (SECONDS_PER_CHANGE + 1)
                        if (seconds == 0L) {
                            currentUpdates.value = Data(current, Data.Status.OK)
                            fetchRandom()
                        }
                        intervalUpdates.value = seconds
                    }

                    override fun onError(e: Throwable) {}
                })
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
                            current = gif
                        }

                        override fun onError(e: Throwable) {
                            fetching = false
                            currentUpdates.value = Data(null, Data.Status.ERROR)
                        }
                    })
        }
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    companion object {
        const val SECONDS_PER_CHANGE = 10
    }
}