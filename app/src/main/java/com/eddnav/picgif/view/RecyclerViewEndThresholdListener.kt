package com.eddnav.picgif.view

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

/**
 * Just works with [StaggeredGridLayoutManager] for now.
 *
 * @author Eduardo Naveda
 */
abstract class RecyclerViewEndThresholdListener(private val layoutManager: StaggeredGridLayoutManager, private val threshold: Int) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastItemPosition = layoutManager.findLastVisibleItemPositions(null).max()

        if (lastItemPosition ?: 0 >= layoutManager.itemCount - threshold) onThresholdReached()
    }

    abstract fun onThresholdReached()
}