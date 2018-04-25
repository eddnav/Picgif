package com.eddnav.picgif.presentation.gif

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.eddnav.picgif.RxTestScheduler
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.data.gif.model.Image
import com.eddnav.picgif.data.gif.repository.GifRepository
import com.eddnav.picgif.presentation.Data
import com.eddnav.picgif.presentation.gif.TrendingViewModel.Companion.LIMIT_PER_PAGE
import io.reactivex.Single
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author Eduardo Naveda
 */
@RunWith(MockitoJUnitRunner::class)
class TrendingViewModelTest {

    @Rule
    @JvmField
    var archTestSchedulerRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var rxTestSchedulerRule = RxTestScheduler()

    @Mock
    private lateinit var loadingObserver: Observer<TrendingViewModel.LoadingEvent>

    @Mock
    private lateinit var trendingObserver: Observer<Data<List<Gif>>>

    @Mock
    private lateinit var repository: GifRepository

    private lateinit var viewModel: TrendingViewModel

    private var isSetupDone = false

    private val testGifs by lazy {
        (0..TrendingViewModel.LIMIT_PER_PAGE).map {
            Gif(it.toString(), "test",
                    Image("test_fws$it.gif", "test_fws$it.mp4", it, it),
                    Image("test_o$it.gif", "test_o$it.mp4", it, it))
        }
    }

    @Before
    fun setupAll() {
        if (isSetupDone) {
            return
        }

        `when`(repository.trending(0, TrendingViewModel.LIMIT_PER_PAGE)).thenReturn(Single.create {
            it.onSuccess(testGifs)
        })

        isSetupDone = true
    }

    @Before
    fun setup() {
        viewModel = TrendingViewModel(repository)
    }

    @Test
    fun initialize_shouldPostLoadingInitial() {
        viewModel.isLoading.observeForever(loadingObserver)

        viewModel.initialize()

        verify(loadingObserver).onChanged(TrendingViewModel.LoadingEvent(true, true))
    }

    @Test
    fun initializeCurrentCurrent_shouldPostCurrentNew() {
        viewModel.initialize() // Will call load.

        viewModel.trendingUpdates.observeForever(trendingObserver)
        viewModel.initializeCurrent()

        verify(trendingObserver).onChanged(Data(testGifs, Data.Type.NEW))
    }

    @Test
    fun load_shouldPostLoading() {
        viewModel.isLoading.observeForever(loadingObserver)

        viewModel.load()

        verify(loadingObserver).onChanged(TrendingViewModel.LoadingEvent(true, true))
    }

    @Test
    fun load_shouldPostUpdate() {
        viewModel.trendingUpdates.observeForever(trendingObserver)
        viewModel.load()

        verify(trendingObserver).onChanged(Data(testGifs, Data.Type.UPDATE))
    }

    @Test
    fun load_shouldPostFilteredUpdateIfDuplicatesAreFound() {
        viewModel.trendingUpdates.observeForever(trendingObserver)
        viewModel.load()

        val moreTestGifs = ((TrendingViewModel.LIMIT_PER_PAGE - 5)..(TrendingViewModel.LIMIT_PER_PAGE * 2 - 5)).map {
            Gif(it.toString(), "test",
                    Image("test_fws$it.gif", "test_fws$it.mp4", it, it),
                    Image("test_o$it.gif", "test_o$it.mp4", it, it))
        }

        val expectedGifs = moreTestGifs.subList(6, moreTestGifs.size)

        `when`(repository.trending(anyInt(), anyInt())).thenReturn(Single.create {
            it.onSuccess(moreTestGifs)
        })

        viewModel.load()

        verify(trendingObserver).onChanged(Data(expectedGifs, Data.Type.UPDATE))
    }

    @Test
    fun load_shouldReloadIfUnderHalfAPage() {
        viewModel.trendingUpdates.observeForever(trendingObserver)

        val testGifs = (0..(TrendingViewModel.LIMIT_PER_PAGE / 2 - 2)).map {
            Gif(it.toString(), "test",
                    Image("test_fws$it.gif", "test_fws$it.mp4", it, it),
                    Image("test_o$it.gif", "test_o$it.mp4", it, it))
        }

        val moreTestGifs = (LIMIT_PER_PAGE..LIMIT_PER_PAGE * 2).map {
            Gif(it.toString(), "test",
                    Image("test_fws$it.gif", "test_fws$it.mp4", it, it),
                    Image("test_o$it.gif", "test_o$it.mp4", it, it))
        }

        `when`(repository.trending(0, LIMIT_PER_PAGE)).thenReturn(Single.create {
            it.onSuccess(testGifs)
        })

        `when`(repository.trending(LIMIT_PER_PAGE, LIMIT_PER_PAGE)).thenReturn(Single.create {
            it.onSuccess(moreTestGifs)
        })

        viewModel.load()


        verify(trendingObserver, times(2)).onChanged(Data(any(), Data.Type.UPDATE))
    }

    @Test
    fun load_shouldNotPostUpdateIfNotAvailable() {
        val viewModelSpy = spy(viewModel)

        doReturn(false).`when`(viewModelSpy).isAvailable()

        viewModelSpy.trendingUpdates.observeForever(trendingObserver)
        viewModelSpy.load()

        verify(trendingObserver, never()).onChanged(any())
    }

    @Test
    fun load_shouldBeUnavailableDuringProgress() {
        viewModel.trendingUpdates.observeForever(trendingObserver)

        `when`(repository.trending(anyInt(), anyInt())).thenReturn(Single.create {})

        viewModel.load()

        assertFalse(viewModel.isAvailable())
    }

    @Test
    fun load_shouldBeAvailableAfterFinishing() {
        viewModel.load()
        assertTrue(viewModel.isAvailable())
    }

    @Test
    fun load_shouldPostErrorOnError() {
        viewModel.trendingUpdates.observeForever(trendingObserver)

        `when`(repository.trending(0, LIMIT_PER_PAGE)).thenReturn(Single.create {
            it.onError(Exception())
        })

        viewModel.load()

        verify(trendingObserver).onChanged(Data(null, Data.Type.ERROR))
    }
}