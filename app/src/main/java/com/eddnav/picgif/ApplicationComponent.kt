package com.eddnav.picgif

import com.eddnav.picgif.data.gif.repository.GifRepository
import com.eddnav.picgif.presentation.ViewModelFactory
import com.eddnav.picgif.presentation.ViewModelModule
import com.eddnav.picgif.remote.RemoteModule
import com.eddnav.picgif.view.MainActivity
import dagger.Component

/**
 * @author Eduardo Naveda
 */
@Application
@Component(modules = [ApplicationModule::class, RemoteModule::class, ViewModelModule::class])
interface ApplicationComponent {

    val viewModelFactory: ViewModelFactory

    val gifRepository: GifRepository

    fun inject(activity: MainActivity)
}