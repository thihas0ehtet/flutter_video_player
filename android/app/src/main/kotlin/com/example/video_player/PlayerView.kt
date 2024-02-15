package com.example.video_player


import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.AdsConfiguration
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ads.AdPlaybackState
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
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


    private var playerView: PlayerView
    private var adsLoader: ImaAdsLoader? = null
    private var eventListener : AdsLoader.EventListener? = null
    private var player: ExoPlayer? = null
    private var contentUri : String? = null
    private val methodChannel: MethodChannel


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
        methodChannel = MethodChannel(messenger, "native_player")
        methodChannel.setMethodCallHandler(this)
        playerView = PlayerView(context)

        adsLoader = ImaAdsLoader.Builder( /* context= */context).build()
        if (Util.SDK_INT > 23) {
            initializePlayer(id,mainActivity,creationParams,methodChannel)
        }
    }

    private fun initializePlayer(
        id: Int,
        mainActivity: MainActivity,
        creationParams: Map<String?, Any?>?,
        methodChannel: MethodChannel
    ) {

        val params = creationParams as Map<String?, Any?>?

        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(view.context, Util.getUserAgent(playerView.context, "flios"))

        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
            .setAdsLoaderProvider { adsLoader }
            .setAdViewProvider(playerView)

        player = ExoPlayer.Builder(view.context).setMediaSourceFactory(mediaSourceFactory).build()

        player!!.preparePlayer(playerView, player!!,mainActivity,methodChannel)

        val backButton: ImageView = playerView.findViewById(R.id.close_iv)
        backButton.setOnClickListener {
            methodChannel.invokeMethod("callBack","")
        }

        val videoName: TextView = playerView.findViewById(R.id.video_name)
        videoName.text=params?.get("videoName") as String?


        playerView.player = player

        adsLoader!!.setPlayer(player)
        playerView.isControllerVisible

        playerView.setShowNextButton(true)
        playerView.setShowPreviousButton(true)
        playerView.showController()
        playerView.keepScreenOn=true;
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        playerView.controllerHideOnTouch=true



        val contentUri = Uri.parse(params?.get("videoURL") as String?)
        val adTagUri = Uri.parse("https://srv.myanmarads.net/vast?z=90014")

        var adPlaybackState = AdPlaybackState(0, 500 * C.MICROS_PER_SECOND)
        adPlaybackState = adPlaybackState.withAdUri(0, 0, adTagUri)


        eventListener?.onAdPlaybackState(adPlaybackState);
        val contentStart = MediaItem.Builder().setUri(contentUri)
            .setAdsConfiguration(
                AdsConfiguration.Builder(adTagUri).build()).build()

        player!!.addMediaItem(contentStart)

        player!!.repeatMode = Player.REPEAT_MODE_ALL
        player!!.prepare()
        player!!.playWhenReady = true

        hideSystemUi(mainActivity)
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
        adsLoader!!.setPlayer(null)
        playerView.keepScreenOn=false;
        playerView.player = null
        player!!.release()
        player = null
        player=null
    }
}