package com.eddnav.picgif.view.gif

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.eddnav.picgif.PicgifApplication
import com.eddnav.picgif.R
import com.eddnav.picgif.di.ViewModelFactory
import com.eddnav.picgif.presentation.Data
import com.eddnav.picgif.presentation.gif.TrendingViewModel
import com.eddnav.picgif.view.RecyclerViewEndThresholdListener
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * TODO: Might want to migrate the UI of this activity to a fragment to support other screen configs.
 * TODO: Add pull to refresh.
 * TODO: Add 'list is empty' view.
 *
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
        recycler.addItemDecoration(GifGridItemDecoration(resources.getDimensionPixelSize(R.dimen.grid_spacing)))

        (application as PicgifApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TrendingViewModel::class.java)

        viewModel.trendingUpdates.observe(this, Observer {
            when (it?.type) {
                Data.Type.NEW -> (recycler.adapter as GifAdapter).replace(it.content!!)
                Data.Type.UPDATE -> (recycler.adapter as GifAdapter).insert(it.content!!)
                Data.Type.ERROR -> Toast.makeText(this, R.string.internet_problems, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.isLoading.observe(this, Observer {
            if (it?.initial == true) {
                if (it.state) initializing.visibility = VISIBLE else initializing.visibility = GONE
            } else {
                // Might want to make this loading indicator part of the list in some way.
                if (it?.state == true) loading.visibility = VISIBLE else loading.visibility = GONE
            }
        })

        if (savedInstanceState == null) viewModel.initialize()
        else viewModel.initializeCurrent()

        recycler.addOnScrollListener(object : RecyclerViewEndThresholdListener(manager, 5) {
            override fun onThresholdReached() {
                viewModel.load()
            }
        })
    }

    inner class GifGridItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

        /**
         * Note: removing the bottom spacing for the N-[GRID_SPAN_COUNT]..N items creates an issue with
         * [StaggeredGridLayoutManager] when adding new items, where the spacing is missing and makes
         * it look as if two images were joined together. As it is unlikely that an user would go
         * through every item in the infinite scrolling to see the spacing at the bottom of the last
         * item, it makes sense just to leave it as it is.
         */
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            val layoutParams = view.layoutParams as StaggeredGridLayoutManager.LayoutParams

            if (layoutParams.spanIndex == 0) {
                outRect.right = spacing
                outRect.bottom = spacing
            } else {
                outRect.bottom = spacing
            }
        }
    }

    companion object {
        const val GRID_SPAN_COUNT = 2
    }
}