package com.eddnav.picgif.presentation

/**
 * @author Eduardo Naveda
 */
data class Data<out T>(val content: T?, val type: Type) {
    enum class Type {
        NEW, UPDATE, ERROR
    }
}