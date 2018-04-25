package com.eddnav.picgif.data.repository

import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.data.gif.model.Image
import com.eddnav.picgif.data.gif.repository.GifRepository
import com.eddnav.picgif.remote.GiphyService
import com.eddnav.picgif.remote.gif.model.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author Eduardo Naveda
 */
@RunWith(MockitoJUnitRunner::class)
class GifRepositoryTest {

    @Mock
    private lateinit var api: GiphyService

    private var isSetupDone = false

    @Before
    fun setup()  {
        if (isSetupDone) {
            return
        }

        Mockito.`when`(api.trending(0,0)).thenReturn(Single.create {
            it.onSuccess(TrendingResponse(listOf(RemoteGif("0", "test",
                    Images(
                            RemoteImage("test_fws.gif", "test_fws.mp4", 1, 2),
                            RemoteImage("test_fw.gif", "test_fw.mp4", 3, 4),
                            RemoteImage("test_fwd.gif", "test_fwd.mp4", 5, 6),
                            RemoteImage("test_o.gif", "test_o.mp4", 7, 8))))))
        })

        Mockito.`when`(api.random()).thenReturn(Single.create {
            it.onSuccess(RandomResponse(RemoteGif("0", "test",
                    Images(
                            RemoteImage("test_fws.gif", "test_fws.mp4", 1, 2),
                            RemoteImage("test_fw.gif", "test_fw.mp4", 3, 4),
                            RemoteImage("test_fwd.gif", "test_fwd.mp4", 5, 6),
                            RemoteImage("test_o.gif", "test_o.mp4", 7, 8)))))
        })


        isSetupDone = true
    }



    @Test
    fun trending_shouldMapToDataOnSuccess() {
        val repository = GifRepository(api)

        val list = listOf(Gif("0", "test",
                Image("test_fws.gif", "test_fws.mp4", 1, 2),
                Image("test_o.gif", "test_o.mp4", 7, 8)))

        repository.trending(0, 0).test().assertValue { it.containsAll(list) }
    }


    @Test
    fun random_shouldMapToDataOnSuccess() {
        val repository = GifRepository(api)

        val gif = Gif("0", "test",
                Image("test_fws.gif", "test_fws.mp4", 1, 2),
                Image("test_o.gif", "test_o.mp4", 7, 8))

        repository.random().test().assertResult(gif)
    }
}