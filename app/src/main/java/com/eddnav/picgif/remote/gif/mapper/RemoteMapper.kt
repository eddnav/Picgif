package com.eddnav.picgif.remote.gif.mapper

import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.remote.gif.model.RemoteGif

/**
 * @author Eduardo Naveda
 */
class RemoteMapper {

    companion object {
        fun toData(gif: RemoteGif): Gif {
            return Gif(gif.title, gif.images.fixed_width_downsampled.webp, gif.images.fixed_width.mp4)
        }
    }
}