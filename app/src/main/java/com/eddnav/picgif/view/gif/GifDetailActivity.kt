package com.eddnav.picgif.view.gif

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.eddnav.picgif.PicgifApplication
import com.eddnav.picgif.R
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.di.ViewModelFactory
import com.eddnav.picgif.presentation.Data
import com.eddnav.picgif.presentation.gif.DetailViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_gif_detail.*
import javax.inject.Inject

/**
 * TODO: Might want to migrate the UI of this activity to a fragment to support other screen configs.
 *
 * @author Eduardo Naveda
 */
class GifDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var player: SimpleExoPlayer
    private lateinit var dataSourceFactory: DefaultDataSourceFactory

    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_detail)

        (application as PicgifApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)

        val trackSelector = DefaultTrackSelector()
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        dataSourceFactory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), null)


        player.volume = 0f
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

            override fun onSeekProcessed() {}

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

            override fun onPlayerError(error: ExoPlaybackException?) {}

            override fun onPositionDiscontinuity(reason: Int) {}

            override fun onRepeatModeChanged(repeatMode: Int) {}

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState != Player.STATE_READY) {
                    loading.visibility = View.VISIBLE
                    playerView.visibility = View.INVISIBLE
                } else {
                    loading.visibility = View.GONE
                    playerView.visibility = View.VISIBLE
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }
        })
        player.setVideoTextureView(playerView)

        viewModel.currentUpdates.observe(this, Observer {
            if (it?.type == Data.Type.UPDATE) {
                if (it.content != null) {
                    val gif: Gif = it.content
                    header.text = gif.title
                    playVideo(gif.image.mp4Url!!, gif.image.width, gif.image.height)
                }
            } else if (it?.type == Data.Type.ERROR) {
                Toast.makeText(this, R.string.internet_problems, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.intervalUpdates.observe(this, Observer {
            time.text = it!!.toString().padStart(2, '0')
        })

        if (intent.extras != null) {
            header.text = intent.getStringExtra(INITIAL_TITLE_EXTRA)
            playVideo(intent.getStringExtra(INITIAL_URL_EXTRA),
                    intent.getIntExtra(INITIAL_WIDTH_EXTRA, 0),
                    intent.getIntExtra(INITIAL_HEIGHT_EXTRA, 0))
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    private fun playVideo(url: String, width: Int, height: Int) {
        val videoUri = Uri.parse(url)
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri)

        aspectFrame.setAspectRatio(width.toFloat() / height)
        player.prepare(videoSource)
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    companion object {
        const val INITIAL_TITLE_EXTRA = "initial_title_extra"
        const val INITIAL_URL_EXTRA = "initial_url_extra"
        const val INITIAL_WIDTH_EXTRA = "initial_width_extra"
        const val INITIAL_HEIGHT_EXTRA = "initial_height_extra"
    }
}
