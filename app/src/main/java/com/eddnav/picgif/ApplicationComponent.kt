package com.eddnav.picgif

import com.eddnav.picgif.data.gif.GifRepository
import com.eddnav.picgif.remote.RemoteModule
import dagger.Component

/**
 * @author Eduardo Naveda
 */
@Application
@Component(modules = [(ApplicationModule::class), (RemoteModule::class)])
interface ApplicationComponent {

    val gifRepository: GifRepository

    fun inject(activity: MainActivity)
}