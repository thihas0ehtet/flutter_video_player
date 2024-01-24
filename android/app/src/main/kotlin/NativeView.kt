package com.example.video_player

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.*
import android.widget.LinearLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.platform.PlatformView


internal class NativeView(context: Context, id: Int, creationParams: Map<String?, Any?>?, messenger: BinaryMessenger,
                          private val mainActivity: MainActivity
) : PlatformView
//    MethodChannel.MethodCallHandler
{

    private val linerLayout: LinearLayout = LinearLayout(context);
    private val playerView: PlayerView
    private var adsLoader: ImaAdsLoader? = null
    private var eventListener : AdsLoader.EventListener? = null
    private var player: ExoPlayer? = null
    private var contentUri : String? = null
//    private val methodChannel: MethodChannel
    override fun getView(): View {
        return playerView
    }

    override fun dispose() {
        releasePlayer()

    }

//    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
//        when (call.method) {
//            "loadUrl" -> {
//                contentUri = call.arguments.toString()
//
//            }
//            "pauseVideo" -> {
//                player!!.pause()
//            }
//            "resumeVideo" -> {
//            }
//            else -> result.notImplemented()
//        }
//    }

    init {
        var layoutParams:ViewGroup.LayoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        linerLayout.layoutParams=layoutParams;
        playerView = PlayerView(context)
        playerView.layoutParams=layoutParams
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
//        linerLayout.addView(playerView)
        playerView.layoutParams=params
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
//        linerLayout.addView(playerView)
//        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
//        linerLayout.addView(playerView)
        setUpPlayer(context,creationParams?.get("url").toString())
    }

    private fun setUpPlayer(context: Context,url:String){
        player= ExoPlayer.Builder(context).build()
                .also { exoPlayer ->
                    print(url)

                    playerView.player=exoPlayer
                    val uri = Uri.parse(url)
//                    val mediaSource: MediaSource = buildMediaSource(uri)

//                 val mediaItem=MediaItem.fromUri(url)
//                    val mediaItem=MediaItem.fromUri("https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8")
                    val mediaItem=MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
//                    https://storage.googleapis.com/gvabox/media/samples/stock.mp4
//https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady=true
//                    exoPlayer.seekTo(currentItem,playBackPosition)
                    exoPlayer.prepare()
                }
        hideSystemUi(mainActivity)
    }



//    init {
//        var layoutParams:ViewGroup.LayoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
//        linerLayout.layoutParams=layoutParams;
//
//        methodChannel = MethodChannel(messenger, "bms_video_player")
//        methodChannel.setMethodCallHandler(this)
//
//        playerView = PlayerView(context)
//
//        playerView.layoutParams=layoutParams
//
//        adsLoader = ImaAdsLoader.Builder( /* context= */context).build()
//        if (Util.SDK_INT > 23) {
//            initializePlayer(id,mainActivity,creationParams,methodChannel)
//        }
//
//    }

//    private fun initializePlayer(
//        id: Int,
//        mainActivity: MainActivity,
//        creationParams: Map<String?, Any?>?,
//        methodChannel: MethodChannel
//    ) {
//
//         mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        val dataSourceFactory: DataSource.Factory =
//            DefaultDataSourceFactory(view.context, Util.getUserAgent(playerView.context, "mahar"))
//        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
////            .setAdsLoaderProvider { adsLoader }
//            .setAdViewProvider(playerView)
//
//        player = ExoPlayer.Builder(view.context).setMediaSourceFactory(mediaSourceFactory).build()
//
//         player!!.preparePlayer(playerView, true,mainActivity,methodChannel)
//        playerView.player = player
////        adsLoader!!.setPlayer(player)
//        playerView.isControllerVisible
//
//        playerView.setShowNextButton(false)
//        playerView.setShowPreviousButton(false)
//        playerView.showController()
//        playerView.keepScreenOn=true
//        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
//        playerView.controllerHideOnTouch=true
//
//
//        val url = creationParams as Map<String?, Any?>?
//        val contentUri = Uri.parse(url?.get("videoURL") as String?)
//        val adTagUri = Uri.parse("https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_preroll_skippable&sz=640x480&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=")
//
//        var adPlaybackState = AdPlaybackState(0, 500 * C.MICROS_PER_SECOND)
////        adPlaybackState = adPlaybackState.withAdUri(0, 0, adTagUri)
////        adPlaybackState = adPlaybackState.withAvailableAdUri(0, 0, adTagUri)
//
//        eventListener?.onAdPlaybackState(adPlaybackState);
//        val mediaItem = MediaItem.Builder().setUri(contentUri).build()
//        val contentStart = MediaItem.Builder().setUri(contentUri)
//            .setAdsConfiguration(
//                AdsConfiguration.Builder(adTagUri).build()).build()
//
//        player!!.addMediaItem(contentStart)
//
//        player!!.repeatMode = Player.REPEAT_MODE_ALL
//        player!!.prepare()
//        player!!.playWhenReady = true
//
//        hideSystemUi(mainActivity)
//    }

    private fun hideSystemUi(mainActivity: MainActivity){
        WindowCompat.setDecorFitsSystemWindows(mainActivity.window,false)
        WindowInsetsControllerCompat(mainActivity.window,playerView).let { controller->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior=WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun releasePlayer(){
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        playerView.keepScreenOn=false
        adsLoader!!.setPlayer(null)
        playerView.player = null
        player!!.release()
        player = null
        player=null
    }
}
