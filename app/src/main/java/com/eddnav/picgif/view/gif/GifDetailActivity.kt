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
import com.eddnav.picgif.data.gif.Data
import com.eddnav.picgif.data.gif.model.Gif
import com.eddnav.picgif.presentation.ViewModelFactory
import com.eddnav.picgif.presentation.gif.DetailViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
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

    private val someUrl = "https://media1.giphy.com/media/kFMBjFbBObj8lleCBk/giphy.mp4?cid=e1bb72ff5ade038b2e72556f495fb127"
    private val someUrl2 = "https://media1.giphy.com/media/kFMBjFbBObj8lleCBk/giphy-downsized-small.mp4?cid=e1bb72ff5ade038b2e72556f495fb127"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_detail)

        (application as PicgifApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(null)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
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
                if (playbackState != Player.STATE_READY) loading.visibility = View.VISIBLE else loading.visibility = View.GONE
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }
        })
        playerView.player = player

        header.text = "Some title"
        playVideo(someUrl)

        viewModel.currentUpdates.observe(this, Observer {
            if (it?.status == Data.Status.OK) {
                if (it.content != null) {
                    val gif: Gif = it.content
                    header.text = gif.title
                    playVideo(gif.image.url)
                }
            }
        })

        viewModel.intervalUpdates.observe(this, Observer {
            time.text = it!!.toString().padStart(2, '0')
        })

        if (savedInstanceState == null) viewModel.initialize()
    }

    private fun playVideo(url: String) {
        val videoUri = Uri.parse(url)
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri)

        player.prepare(videoSource)
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}
