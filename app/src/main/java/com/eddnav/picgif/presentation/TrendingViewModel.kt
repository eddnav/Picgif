package com.eddnav.picgif.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eddnav.picgif.data.gif.Data
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.data.gif.repository.GifRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author Eduardo Naveda
 */
class TrendingViewModel @Inject constructor(private val gifRepository: GifRepository) : ViewModel() {

    private val disposables: CompositeDisposable = CompositeDisposable()

    val trending: MutableLiveData<Data<List<Gif>>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isLoading.value = false
    }

    fun load() {
        if (isLoading.value == false) {
            isLoading.value = true
            val current = trending.value?.content
            val disposable = gifRepository.trending(current?.size ?: 0, LIMIT_PER_PAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {
                        isLoading.value = false
                        trending.value = Data(current?.plus(it) ?: it, Data.Status.OK)
                    }
                    .doOnError({
                        isLoading.value = false
                        trending.value = Data(trending.value?.content, Data.Status.ERROR)
                    })
                    .subscribe()

            disposables.add(disposable)
        }
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    companion object {
        const val LIMIT_PER_PAGE = 30
    }
}