package com.eddnav.picgif.presentation.gif

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.eddnav.picgif.RxTestScheduler
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.data.gif.model.Image
import com.eddnav.picgif.data.gif.repository.GifRepository
import com.eddnav.picgif.presentation.Data
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

/**
 * @author Eduardo Naveda
 */
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {

    @Rule
    @JvmField
    var archTestSchedulerRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var rxTestSchedulerRule = RxTestScheduler()

    @Mock
    private lateinit var currentObserver: Observer<Data<Gif>>

    @Mock
    private lateinit var intervalObserver: Observer<Long>

    @Mock
    private lateinit var repository: GifRepository

    private lateinit var viewModel: DetailViewModel
    private lateinit var testScheduler: TestScheduler

    private var isSetupDone = false

    @Before
    fun setupAll() {
        if (isSetupDone) {
            return
        }

        `when`(repository.random()).thenReturn(Single.create {
            it.onSuccess(Gif("0", "test",
                    Image("test_fws.gif", "test_fws.mp4", 0, 0),
                    Image("test_o.gif", "test_o.mp4", 0, 0)))
        })

        isSetupDone = true
    }

    @Before
    fun setup() {
        viewModel = DetailViewModel(repository)

        testScheduler = TestScheduler()
        // For the interval observable, runs on the computation scheduler.
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
    }

    @Test
    fun start_shouldFetchRandomIfNextIsNull() {

        // First time start, no next available.
        viewModel.start()

        assertTrue(viewModel.next != null)
    }

    @Test
    fun start_shouldInitializeIntervalOfNextChange() {

        viewModel.intervalUpdates.observeForever(intervalObserver)
        viewModel.start()

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(intervalObserver).onChanged(DetailViewModel.SECONDS_PER_CHANGE)
    }

    @Test
    fun start_shouldPostCurrentUpdateOnSecondsPerChangePlusOneOfInterval() {

        viewModel.currentUpdates.observeForever(currentObserver)
        viewModel.start()

        testScheduler.advanceTimeBy(DetailViewModel.SECONDS_PER_CHANGE + 1, TimeUnit.SECONDS)

        verify(currentObserver).onChanged(Data(viewModel.next, Data.Type.UPDATE))
    }

    @Test
    fun start_shouldPrefetchNextRandomOnSecondsPerChangePlusOneOfInterval() {

        viewModel.currentUpdates.observeForever(currentObserver)
        viewModel.start()

        val previous = viewModel.next

        `when`(repository.random()).thenReturn(Single.create {
            it.onSuccess(Gif("1", "test1",
                    Image("test_fws1.gif", "test_fws1.mp4", 1, 1),
                    Image("test_o1.gif", "test_o1.mp4", 1, 1)))
        })

        testScheduler.advanceTimeBy(DetailViewModel.SECONDS_PER_CHANGE + 1, TimeUnit.SECONDS)

        assertNotEquals(previous, viewModel.next)
    }

    @Test
    fun start_shouldNotPrefetchNextRandomOnSecondsPerChangePlusOneOfIntervalIfAlreadyFetching() {
        viewModel.currentUpdates.observeForever(currentObserver)
        viewModel.start()

        val previous = viewModel.next

        `when`(repository.random()).thenReturn(Single.create {})

        testScheduler.advanceTimeBy(DetailViewModel.SECONDS_PER_CHANGE + 1, TimeUnit.SECONDS)

        assertEquals(previous, viewModel.next)
    }

    @Test
    fun start_shouldPostErrorOnFetchRandomError() {
        `when`(repository.random()).thenReturn(Single.create {
            it.onError(Exception())
        })

        viewModel.currentUpdates.observeForever(currentObserver)
        viewModel.start()

        verify(currentObserver).onChanged(Data(null, Data.Type.ERROR))
    }

    @Test
    fun pause_shouldStopInterval() {
        viewModel.currentUpdates.observeForever(currentObserver)
        viewModel.start()

        val initial = 5L
        testScheduler.advanceTimeBy(initial, TimeUnit.SECONDS)
        viewModel.pause()
        testScheduler.advanceTimeBy(DetailViewModel.SECONDS_PER_CHANGE + 1 - initial, TimeUnit.SECONDS)

        verify(currentObserver, never()).onChanged(Data(viewModel.next, Data.Type.UPDATE))
    }
}