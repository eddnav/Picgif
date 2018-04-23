package com.eddnav.picgif.view.gif

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.eddnav.picgif.GlideApp
import com.eddnav.picgif.R
import com.eddnav.picgif.data.gif.model.Gif

/**
 * @author Eduardo Naveda
 */
class GifAdapter(private val context: Context, private val gifs: MutableList<Gif>) : RecyclerView.Adapter<GifAdapter.GifViewHolder>() {

    override fun getItemCount(): Int {
        return gifs.size
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        holder.bind(gifs[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        val gif = gifs[position]
        val aspect = gif.image.width.toDouble() / gif.image.height
        return when {
            aspect >= 1.6 -> 2
            aspect >= 1.33 -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        when (viewType) {
            0 -> {
                val view125 = LayoutInflater.from(parent.context)
                        .inflate(R.layout.gif_grid_item_125, parent, false)
                return GifViewHolder(view125)
            }
            1 -> {
                val view133 = LayoutInflater.from(parent.context)
                        .inflate(R.layout.gif_grid_item_133, parent, false)
                return GifViewHolder(view133)
            }
            else -> {
                val view177 = LayoutInflater.from(parent.context)
                        .inflate(R.layout.gif_grid_item_160, parent, false)
                return GifViewHolder(view177)
            }
        }
    }

    fun insert(newGifs: List<Gif>) {
        val top = gifs.size
        gifs.addAll(newGifs)
        this.notifyItemRangeInserted(top, newGifs.size)
    }

    fun getPlaceholderColor(position: Int): Int {
        val type = position % PLACEHOLDER_TYPE_COUNT
        return when(type) {
            0 -> R.color.placeholder0
            1 -> R.color.placeholder1
            2 -> R.color.placeholder2
            3 -> R.color.placeholder3
            4 -> R.color.placeholder4
            else -> {
                throw IllegalArgumentException("Placeholder type $type unknown")
            }
        }
    }

    inner class GifViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(gif: Gif, position: Int) {
            GlideApp.with(context)
                    .load(gif.preview.url)
                    .placeholder(getPlaceholderColor(position))
                    .centerCrop()
                    .into(view as ImageView)
            view.setOnClickListener { println("It does nothing yet, sob...") }
        }
    }

    companion object {
        const val PLACEHOLDER_TYPE_COUNT = 5
    }
}