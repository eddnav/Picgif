package com.eddnav.picgif.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.eddnav.picgif.PicgifApplication
import com.eddnav.picgif.R
import com.eddnav.picgif.data.gif.Data
import com.eddnav.picgif.presentation.TrendingViewModel
import com.eddnav.picgif.presentation.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * @author Eduardo Naveda
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var vm: TrendingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as PicgifApplication).applicationComponent.inject(this)
        vm = ViewModelProviders.of(this, viewModelFactory).get(TrendingViewModel::class.java)

        vm.trending.observe(this, Observer {
            if (it?.status == Data.Status.ERROR) {
                test.text = "ERROR!"
            } else {
                test.text = it?.content?.size.toString()
            }
        })

        more.setOnClickListener { vm.load() }
        vm.load()
    }


}
