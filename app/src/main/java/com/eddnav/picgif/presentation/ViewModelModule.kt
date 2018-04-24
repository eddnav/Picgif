package com.eddnav.picgif.presentation

import android.arch.lifecycle.ViewModel
import com.eddnav.picgif.presentation.gif.DetailViewModel
import com.eddnav.picgif.presentation.gif.TrendingViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * @author Eduardo Naveda
 */
@Module
abstract class ViewModelModule {

    @SuppressWarnings("unused")
    @Binds
    @IntoMap
    @ViewModelKey(TrendingViewModel::class)
    abstract fun trendingViewModel(viewModel: TrendingViewModel): ViewModel


    @SuppressWarnings("unused")
    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun detailViewmodel(viewModel: DetailViewModel): ViewModel
}