package com.eddnav.picgif.presentation

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * @author Eduardo Naveda
 */
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(TrendingViewModel::class)
    abstract fun trendingViewModel(viewModel: TrendingViewModel): ViewModel
}