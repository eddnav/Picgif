package com.eddnav.picgif

import com.eddnav.picgif.remote.RemoteModule
import dagger.Component

/**
 * @author Eduardo Naveda
 */
@Component(modules = [(ApplicationModule::class), (RemoteModule::class)])
class ApplicationComponent {

}