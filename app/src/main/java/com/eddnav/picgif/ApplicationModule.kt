package com.eddnav.picgif

import dagger.Module
import dagger.Provides

/**
 * @author Eduardo Naveda
 */
@Module
class ApplicationModule(val application: PicgifApplication) {

    @Provides
    fun Application() : PicgifApplication {
        return application
    }
}