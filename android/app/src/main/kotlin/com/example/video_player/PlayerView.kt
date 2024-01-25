package com.example.video_player

import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.ads.interactivemedia.v3.internal.it
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.AdsConfiguration
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.TracksInfo
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ads.AdPlaybackState
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionOverrides
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

internal class PlayerView(context: Context, id: Int, creationParams: Map<String?, Any?>?, messenger: BinaryMessenger,
                          private val mainActivity: MainActivity
) : PlatformView,
    MethodChannel.MethodCallHandler, Player.Listener, AppCompatActivity() {

    private var qualityPopUp: PopupMenu?=null
    private var trackSelector: DefaultTrackSelector?=null
    private var qualityList = ArrayList<Pair<String, TrackSelectionOverrides.Builder>>()

    private val playerView: PlayerView
    private var adsLoader: ImaAdsLoader? = null
    private var eventListener : AdsLoader.EventListener? = null
    private var player: ExoPlayer? = null
    private var contentUri : String? = null
    private val methodChannel: MethodChannel

//    private lateinit var exoQuality: ImageView

    private var trackDialog: Dialog? = null

    override fun getView(): View {
        return playerView
    }

    override fun dispose() {
        releasePlayer(mainActivity)

    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "loadUrl" -> {
                contentUri = call.arguments.toString()

            }
            "pauseVideo" -> {
                player!!.pause()
            }
            "resumeVideo" -> {
            }
            else -> result.notImplemented()
        }
    }


    init {
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        methodChannel = MethodChannel(messenger, "bms_video_player")
        methodChannel.setMethodCallHandler(this)
        playerView = PlayerView(context)


        adsLoader = ImaAdsLoader.Builder( /* context= */context).build()
        if (Util.SDK_INT > 23) {
            initializePlayer(id,mainActivity,creationParams,methodChannel)
        }

      initListener()
    }

    private fun initListener() {
       val exoQuality: ImageView = playerView.findViewById(R.id.exo_quality)
        exoQuality.setOnClickListener {
            player!!.seekTo(player!!.currentPosition + 10000)
//            if(trackDialog == null) initPopupQuality()
            trackDialog?.show()
        }
    }


    private fun initializePlayer(
        id: Int,
        mainActivity: MainActivity,
        creationParams: Map<String?, Any?>?,
        methodChannel: MethodChannel
    ) {

 //not working    trackSelector = DefaultTrackSelector(this, AdaptiveTrackSelection.Factory())

//        trackSelector = DefaultTrackSelector(this)
//        // When player is initialized it'll be played with a quality of MaxVideoSize to prevent loading in 1080p from the start
//        trackSelector!!.setParameters(
//                trackSelector!!.buildUponParameters().setMaxVideoSize(10, 10)
//        )

        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(view.context, Util.getUserAgent(playerView.context, "flios"))
        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
//            .setAdsLoaderProvider { unusedAdTagUri: AdsConfiguration? -> adsLoader }
            .setAdViewProvider(playerView)

        player = ExoPlayer.Builder(view.context).setMediaSourceFactory(mediaSourceFactory).build()
        player!!.preparePlayer(playerView, player!!, true,mainActivity,methodChannel)
        playerView.player = player
//        adsLoader!!.setPlayer(player)
        playerView.isControllerVisible

        playerView.setShowNextButton(true)
        playerView.setShowPreviousButton(true)
        playerView.showController()
        playerView.keepScreenOn=true;
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        playerView.controllerHideOnTouch=true

        val url = creationParams as Map<String?, Any?>?
        val contentUri = Uri.parse(url?.get("videoURL") as String?)
        val adTagUri = Uri.parse("https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_preroll_skippable&sz=640x480&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=")

        var adPlaybackState = AdPlaybackState(0, 500 * C.MICROS_PER_SECOND)
//        adPlaybackState = adPlaybackState.withAdUri(0, 0, adTagUri)
//        adPlaybackState = adPlaybackState.withAvailableAdUri(0, 0, adTagUri)

        eventListener?.onAdPlaybackState(adPlaybackState);
        val mediaItem = MediaItem.Builder().setUri(contentUri).build()
        val contentStart = MediaItem.Builder().setUri(contentUri)
            .setAdsConfiguration(
                AdsConfiguration.Builder(adTagUri).build()).build()

        player!!.addMediaItem(contentStart)

        player!!.repeatMode = Player.REPEAT_MODE_ALL
        player!!.prepare()
        player!!.playWhenReady = true

        //Listener on player
//        player!!.addListener(object : Player.Listener {
//            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                if(playbackState == Player.STATE_READY) {
//                    exoQuality.visibility = View.VISIBLE
//                }
//            }
//        })

        hideSystemUi(mainActivity)
    }

//    override fun onTracksInfoChanged(tracksInfo: TracksInfo) {
//        println("TRACK CHANGED")
//        println(tracksInfo.trackGroupInfos)
//    }

//    override fun onPlaybackStateChanged(playbackState: Int) {
//        if (playbackState==Player.STATE_READY){
//
//            trackSelector?.generateQualityList()?.let {
//                qualityList = it
//                setUpQualityList()
//            }
//        }
//    }

//    override fun onStart() {
//        super.onStart()
//        if (Util.SDK_INT >= 24) {
//            initializePlayer()
//        }
//    }

//    override fun onStop() {
//        super.onStop()
//        if (Util.SDK_INT >= 24) {
//            releasePlayer(mainActivity)
//        }
//    }

//    override fun onResume() {
//        super.onResume()
//        if (Util.SDK_INT < 24) {
//            initializePlayer()
//        }
//    }
//    override fun onPause() {
//        super.onPause()
//        if (Util.SDK_INT < 24) {
//            releasePlayer(mainActivity)
//        }
//    }

//    222

    private fun initPopupQuality() {
        val trackSelectionDialogBuilder = TrackSelectionDialogBuilder(
                this,
                "Select Quality",
                trackSelector!!,
                C.TRACK_TYPE_VIDEO
        )
        trackSelectionDialogBuilder.setTrackNameProvider {
           " Select Quality dd"
        }
        trackDialog = trackSelectionDialogBuilder.build()
    }

    private fun hideSystemUi(mainActivity: MainActivity){
        WindowCompat.setDecorFitsSystemWindows(mainActivity.window,false)
        WindowInsetsControllerCompat(mainActivity.window,playerView).let { controller->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior=WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun releasePlayer(mainActivity: MainActivity){
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //adsLoader!!.setPlayer(null)
        playerView.keepScreenOn=false;
        playerView.player = null
        player!!.release()
        player = null
        player=null
    }
}