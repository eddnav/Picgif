package com.eddnav.picgif

import android.app.Application

/**
 * @author Eduardo Naveda
 */
class PicgifApplication : Application() {

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
                .build()
    }
}