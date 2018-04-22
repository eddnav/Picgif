package com.eddnav.picgif.data.gif

/**
 * @author Eduardo Naveda
 */
data class Data<out T>(val content: T?, val status: Status) {
    enum class Status {
        OK, ERROR
    }
}