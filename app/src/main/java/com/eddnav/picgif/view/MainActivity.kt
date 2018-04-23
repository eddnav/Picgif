package com.eddnav.picgif.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.eddnav.picgif.PicgifApplication
import com.eddnav.picgif.R
import com.eddnav.picgif.data.gif.Data
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.presentation.TrendingViewModel
import com.eddnav.picgif.presentation.ViewModelFactory
import com.eddnav.picgif.view.gif.GifAdapter
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


/**
 * @author Eduardo Naveda
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: TrendingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = StaggeredGridLayoutManager(GRID_SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL)
        recycler.layoutManager = manager
        recycler.adapter = GifAdapter(this, mutableListOf())
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recycler.addItemDecoration(GifGridItemDecoration(spacing))

        (application as PicgifApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TrendingViewModel::class.java)

        viewModel.trendingUpdates.observe(this, Observer {
            if (it?.status == Data.Status.ERROR) {
                //error = "ERROR!"
            } else {
                val newGifs = it?.content!!
                (recycler.adapter as GifAdapter).insert(newGifs)
            }
        })

        viewModel.load()

        more.setOnClickListener { viewModel.load() }
    }

    inner class GifGridItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val size = parent.adapter.itemCount
            val layoutParams = view.layoutParams as StaggeredGridLayoutManager.LayoutParams

            if (position in 0..(size - 2)) {
                if (layoutParams.spanIndex == 0) {
                    outRect.right = spacing
                    outRect.bottom = spacing
                } else {
                    outRect.bottom = spacing
                }
            } else {
                if (layoutParams.spanIndex == 0) {
                    outRect.right = spacing
                }
            }
        }
    }

    companion object {
        const val GRID_SPAN_COUNT = 2
    }
}