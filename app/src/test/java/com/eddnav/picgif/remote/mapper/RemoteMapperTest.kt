package com.eddnav.picgif.remote.mapper

import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.data.gif.model.Image
import com.eddnav.picgif.remote.gif.mapper.RemoteMapper
import com.eddnav.picgif.remote.gif.model.Images
import com.eddnav.picgif.remote.gif.model.RemoteGif
import com.eddnav.picgif.remote.gif.model.RemoteImage
import org.junit.Test

/**
 * @author Eduardo Naveda
 */
class RemoteMapperTest {

    @Test
    fun toData_shouldMapToGif() {

        val expectedGif = Gif("test", "test",
                Image("test_fws.gif", "test_fws.mp4", 1, 2),
                Image("test_o.gif", "test_o.mp4", 7, 8))

        val gif = RemoteMapper.toData(
                RemoteGif("test", "test",
                        Images(
                                RemoteImage("test_fws.gif", "test_fws.mp4", 1, 2),
                                RemoteImage("test_fw.gif", "test_fw.mp4", 3, 4),
                                RemoteImage("test_fwd.gif", "test_fwd.mp4", 5, 6),
                                RemoteImage("test_o.gif", "test_o.mp4", 7, 8))))

        assert(gif == expectedGif)
    }


    @Test
    fun toData_shouldMapToImage() {

        val expectedImage = Image("test_o.gif", "test_o.mp4", 7, 8)

        val image = RemoteMapper.toData(RemoteImage("test_o.gif", "test_o.mp4", 7, 8))

        assert(image == expectedImage)
    }
}