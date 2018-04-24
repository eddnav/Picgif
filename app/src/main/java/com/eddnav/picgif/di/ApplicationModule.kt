package com.eddnav.picgif.di

import com.eddnav.picgif.PicgifApplication
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