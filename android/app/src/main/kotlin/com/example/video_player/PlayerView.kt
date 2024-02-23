package com.example.video_player


import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Handler
import android.os.Looper
//import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ads.AdsLoader
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
//import com.google.android.exoplayer2.C
//import com.google.android.exoplayer2.ExoPlayer
//import com.google.android.exoplayer2.MediaItem
//import com.google.android.exoplayer2.MediaItem.AdsConfiguration
//import com.google.android.exoplayer2.Player
//import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
//import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
//import com.google.android.exoplayer2.source.MediaSourceFactory
//import com.google.android.exoplayer2.source.ads.AdPlaybackState
//import com.google.android.exoplayer2.source.ads.AdsLoader
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
//import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
//import com.google.android.exoplayer2.ui.PlayerView
//import com.google.android.exoplayer2.upstream.DataSource
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.exoplayer2.util.Util
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import javax.sql.DataSource


internal class MyPlayerView(context: Context, id: Int, creationParams: Map<String?, Any?>?, messenger: BinaryMessenger,
                          private val mainActivity: MainActivity
) : PlatformView,
        MethodChannel.MethodCallHandler, Player.Listener {


    private var playerView: PlayerView
//    private var adsLoader: ImaAdsLoader? = null
    private var eventListener: AdsLoader.EventListener? = null
    private var player: ExoPlayer? = null
    private var contentUri: String? = null
    private val methodChannel: MethodChannel
    private val relativeLayout: RelativeLayout = RelativeLayout(context)
    private val handler = Handler(Looper.getMainLooper())
    private var userNumberView: TextView

    private var isShowingTrackSelectionDialog = false
    private val trackSelector: DefaultTrackSelector? = null

    override fun getView(): View {
        return relativeLayout
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


        val layoutParams: ViewGroup.LayoutParams =
                ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        // set relative layout params
        relativeLayout.layoutParams = layoutParams

        // create player view
        playerView = PlayerView(context)
        // set player view layout params
        playerView.layoutParams = layoutParams

        // create Watermark ImageView
        val watermarkView = ImageView(mainActivity)
        watermarkView.setImageResource(R.drawable.mahar_app_logo)

        val watermarkViewLayoutParams = RelativeLayout.LayoutParams(
                150,
                150
        ).apply {
            marginEnd = 80
            topMargin = 50
        }
        watermarkViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        watermarkViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)

        // set image view layout params
        watermarkView.layoutParams = watermarkViewLayoutParams


        // create User Number TextView
        userNumberView = TextView(mainActivity)

        handler.postDelayed({
            showText()
        }, 10000) //
        userNumberView.textSize= 16.0F
        relativeLayout.addView(playerView)
//        relativeLayout.addView(watermarkView)
        relativeLayout.addView(userNumberView)


//        adsLoader = ImaAdsLoader.Builder( /* context= */context).build()
//        if (Util.SDK_INT > 23) {
//            initializePlayer(id, mainActivity, creationParams, methodChannel)
//        }
        initializePlayer(id, mainActivity, creationParams, methodChannel)
    }

    private fun showText() {
        userNumberView.text="LQ3DAB"

        // Generate random margin values
        val randomMarginStart = (350..1000).random()
        val randomMarginTop = (200..800).random()

        val userNumberViewLayoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = randomMarginStart
            topMargin = randomMarginTop
        }

        userNumberViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        userNumberViewLayoutParams.addRule(RelativeLayout.ALIGN_START)

        // set image view layout params
        userNumberView.layoutParams = userNumberViewLayoutParams

        handler.postDelayed({
            hideText()
        }, 3000) // 3 seconds delay
    }

    private fun hideText() {
        userNumberView.text = ""

        handler.postDelayed({
            showText()
        }, 10000) // 10 seconds delay
    }


    private fun initializePlayer(
            id: Int,
            mainActivity: MainActivity,
            creationParams: Map<String?, Any?>?,
            methodChannel: MethodChannel
    ) {

        val params = creationParams as Map<String?, Any?>?

        // Set up track selection quality control
//        val trackSelector = DefaultTrackSelector(mainActivity)
//
//        trackSelector.parameters = trackSelector.parameters
//                .buildUpon()
//                .setMaxVideoSizeSd()
//                .setMaxVideoBitrate(500000) // Set your desired maximum bitrate
//                .build()

//        val dataSourceFactory: DataSource.Factory =
//                DefaultDataSourceFactory(view.context, Util.getUserAgent(playerView.context, "mahar"))
//
//        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
////                .setAdsLoaderProvider { adsLoader }
//                .setAdViewProvider(playerView)

//        player = ExoPlayer.Builder(view.context).setMediaSourceFactory(mediaSourceFactory).setTrackSelector(trackSelector).build()

        player = ExoPlayer.Builder(view.context).build()
        player!!.preparePlayer(playerView, player!!, mainActivity, methodChannel)

//        val backButton: ImageView = playerView.findViewById(R.id.close_iv)
//        backButton.setOnClickListener {
//            methodChannel.invokeMethod("callBack", "")
//        }

//        val videoName: TextView = playerView.findViewById(R.id.video_name)
//        videoName.text = params?.get("videoName") as String?


        playerView.player = player

//        adsLoader!!.setPlayer(player)
//        playerView.isControllerVisible

//        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
//
//        playerView.showController()
        playerView.keepScreenOn = true;
//        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
//       playerView.controllerHideOnTouch = false


        val contentUri = Uri.parse(params?.get("videoURL") as String?)
        val adTagUri = Uri.parse("https://srv.myanmarads.net/vast?z=90014")

//        var adPlaybackState = AdPlaybackState(0, 500 * C.MICROS_PER_SECOND)
//        adPlaybackState = adPlaybackState.withAvailableAdUri(0, 0, adTagUri)

//        eventListener?.onAdPlaybackState(adPlaybackState);
        val contentStart = MediaItem.Builder().setUri(contentUri)
                .setAdsConfiguration(
                        MediaItem.AdsConfiguration.Builder(adTagUri).build()).build()

        player!!.addMediaItem(contentStart)

        player!!.repeatMode = Player.REPEAT_MODE_ALL
        player!!.prepare()
        player!!.playWhenReady = true

//        val qualityButton: ImageView = playerView.findViewById(R.id.exo_quality)
//        qualityButton.setOnClickListener {
////            showQualitySelectionDialog(mainActivity)
////            if (!isShowingTrackSelectionDialog
////                    && TrackSelectionDialog.willHaveContent(trackSelector)) {
////                isShowingTrackSelectionDialog = true
////                val trackSelectionDialog: TrackSelectionDialog = TrackSelectionDialog.createForTrackSelector(
////                        trackSelector
////                )  /* onDismissListener= */
////                { dismissedDialog -> isShowingTrackSelectionDialog = false }
////                trackSelectionDialog.show(supportFragmentManager,  /* tag= */null)
////            }
//        }


        hideSystemUi(mainActivity)
    }

    private fun hideSystemUi(mainActivity: MainActivity) {
        WindowCompat.setDecorFitsSystemWindows(mainActivity.window, false)
        WindowInsetsControllerCompat(mainActivity.window, playerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun releasePlayer(mainActivity: MainActivity) {
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
//        adsLoader!!.setPlayer(null)
        playerView.keepScreenOn = false;
        playerView.player = null
        player!!.release()
        player = null
        player = null
    }
}