package com.eddnav.picgif.di

import com.eddnav.picgif.data.gif.repository.GifRepository
import com.eddnav.picgif.view.gif.MainActivity
import com.eddnav.picgif.view.gif.GifDetailActivity
import dagger.Component
import okhttp3.OkHttpClient

/**
 * @author Eduardo Naveda
 */
@Application
@Component(modules = [ApplicationModule::class, RemoteModule::class, ViewModelModule::class])
interface ApplicationComponent {

    val viewModelFactory: ViewModelFactory

    val okHttp: OkHttpClient

    val gifRepository: GifRepository

    fun inject(activity: MainActivity)

    fun inject(activity: GifDetailActivity)
}