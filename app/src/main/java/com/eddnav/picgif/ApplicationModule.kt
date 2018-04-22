package com.eddnav.picgif

import dagger.Module
import dagger.Provides

/**
 * @author Eduardo Naveda
 */
@Module
class ApplicationModule(private val application: PicgifApplication) {

    @Provides
    fun application(): PicgifApplication = application
}