package com.eddnav.picgif

import android.app.Application
import com.eddnav.picgif.di.ApplicationComponent
import com.eddnav.picgif.di.DaggerApplicationComponent

/**
 * @author Eduardo Naveda
 */
class PicgifApplication : Application() {

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
                .build()
    }
}