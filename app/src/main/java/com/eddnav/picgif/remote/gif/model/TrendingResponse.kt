package com.eddnav.picgif.remote.gif.model

/**
 * @author Eduardo Naveda
 */
data class TrendingResponse(val data: List<RemoteGif>, val pagination: Pagination?, val meta: Meta)