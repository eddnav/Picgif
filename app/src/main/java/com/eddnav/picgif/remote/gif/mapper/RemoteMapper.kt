package com.eddnav.picgif.remote.gif.mapper

import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.data.gif.model.Image
import com.eddnav.picgif.remote.gif.model.RemoteGif
import com.eddnav.picgif.remote.gif.model.RemoteImage

/**
 * @author Eduardo Naveda
 */
class RemoteMapper {

    companion object {

        fun toData(gif: RemoteGif): Gif {
            return Gif(gif.id, gif.title, toData(gif.images.fixed_width_downsampled), toData(gif.images.original, gif.images.original.mp4))
        }

        private fun toData(image: RemoteImage, url: String? = null): Image {
            return Image(url ?: image.url, image.width, image.height)
        }
    }
}