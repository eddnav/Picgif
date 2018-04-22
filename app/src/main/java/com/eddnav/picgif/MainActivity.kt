package com.eddnav.picgif

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.eddnav.picgif.data.gif.Data
import com.eddnav.picgif.data.gif.GifRepository
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * @author Eduardo Naveda
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var gifRepository: GifRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as PicgifApplication).applicationComponent.inject(this)

        gifRepository.random().observe(this, Observer {
            if (it?.status == Data.Status.ERROR) {
                test.text = "ERROR!"
            } else {
                test.text = it?.content?.title
            }
        })
    }
}
