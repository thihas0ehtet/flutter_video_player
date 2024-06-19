package com.example.video_player

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.WindowCompat
import com.example.video_player.MessengerHolder.binaryMessenger
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel


object MessengerHolder {
    var binaryMessenger: BinaryMessenger? = null
}

class PlayerActivity() : Activity() {

    private lateinit var playerView: PlayerView
    private var player: ExoPlayer? = null
    private var selectedQualityIndex: Int = 0
    private var adsLoader: ImaAdsLoader? = null
    val methodChannel = binaryMessenger?.let { MethodChannel(it, "mahar.com/exoplayer") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

         val relativeLayout = RelativeLayout(this)


         val userNumberView= TextView(this)

        val handler = Handler(Looper.getMainLooper())


        val binaryMessenger = MessengerHolder.binaryMessenger
         val userNumber = intent.getStringExtra("userNumber") ?: ""
        val currentTimestamp = intent.getStringExtra("currentTimestamp") ?: ""
        val videoTitle = intent.getStringExtra("videoTitle") ?: ""
        val streamUrl = intent.getStringExtra("streamUrl") ?: ""
        val previousId = intent.getStringExtra("previousId") ?: ""
        val nextId = intent.getStringExtra("nextId") ?: ""
        val adsStreaming = intent.getStringExtra("adsStreaming") ?: ""

        val methodChannel = binaryMessenger?.let { MethodChannel(it, "mahar.com/exoplayer") }

        window.requestFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(this.window, false)


        // Hide the system bars (status bar and navigation bar)
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        // create relative layout
        val layoutParams: ViewGroup.LayoutParams =
            ViewGroup.LayoutParams(

                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        relativeLayout.layoutParams = layoutParams
        relativeLayout.setBackgroundColor(Color.BLACK)

        // create Logo ImageView
        val logoView = ImageView(this)
        logoView.setImageResource(R.drawable.mahar_app_logo)
        val logoViewLayoutParams = RelativeLayout.LayoutParams(
            150,
            150
        ).apply {
            marginEnd = 50
            topMargin = 50
        }
        logoViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        logoViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
        logoView.layoutParams = logoViewLayoutParams


        // create User Number TextView
        fun showText(userNumber:String) {
            userNumberView.text=userNumber;

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
                userNumberView.text = ""

                handler.postDelayed({
                    showText(userNumber)
                }, 5000) // 10 seconds delay
            }, 3000) // 3 seconds delay
        }

        handler.postDelayed({
            showText(userNumber)
        }, 5000)

        userNumberView.textSize= 16.0F

        //create Player View
        playerView = PlayerView(this)
        playerView.layoutParams = layoutParams

        //Ads
        adsLoader = ImaAdsLoader.Builder( this).build()


        if (methodChannel != null) {
            initializePlayer(streamUrl,videoTitle,currentTimestamp,previousId,nextId,adsStreaming,methodChannel)
        }


        //Replay Button
        val replayButtonView = ImageView(this)
        replayButtonView.setImageResource(R.drawable.mahar_app_logo)
        val replayButtonViewLayoutParams = RelativeLayout.LayoutParams(
            150,
            150
        )
        replayButtonViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        replayButtonViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        replayButtonView.layoutParams = replayButtonViewLayoutParams


        relativeLayout.addView(playerView)
        relativeLayout.addView(logoView)
//        relativeLayout.addView(replayButtonView)
        relativeLayout.addView(userNumberView)

        setContentView(relativeLayout)

    }

    private fun initializePlayer(
        streamUrl:String,
        videoTitle:String,
        currentTimestamp:String,
        previousId:String,
        nextId:String,
        adsStreaming:String,
       methodChannel: MethodChannel
    ) {

        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(this, Util.getUserAgent(playerView.context, "mahar"))

        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
              .setAdsLoaderProvider { adsLoader }
            .setAdViewProvider(playerView)

        val trackSelector = DefaultTrackSelector(this)

//        trackSelector.parameters = trackSelector.parameters
//            .buildUpon()
//            .setMaxVideoBitrate(400000) // Set your desired maximum bitrate
//            .build()

        player = ExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).setTrackSelector(trackSelector).build()

        //Ads
        adsLoader!!.setPlayer(player)

        playerView.player = player
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
        playerView.keepScreenOn = true;
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        playerView.controllerHideOnTouch = true

        //Video Name
        val videoName: TextView = playerView.findViewById(R.id.video_name)
        videoName.text = videoTitle



        // Back Button
        val backButton: ImageView = playerView.findViewById(R.id.close_iv)
        backButton.setOnClickListener {
            methodChannel.invokeMethod("callBack",(player!!.currentPosition/ 1000L).toString()+","+(player!!.duration/ 1000L).toString())
            this.finish()
        }

        // Setting Button
        val settingButton: ImageView = playerView.findViewById(R.id.exo_settings)
        settingButton.setOnClickListener {
            showQualitySelectionDialog(trackSelector)
        }

         // Previous Button
         val previousButton: ImageView = playerView.findViewById(R.id.previous)
         if(previousId==""){
             previousButton.visibility=View.INVISIBLE
         }
         previousButton.setOnClickListener {
             methodChannel.invokeMethod("previousAction",(player!!.currentPosition/ 1000L).toString()+","+(player!!.duration/ 1000L).toString()+","+previousId)
         }
 
         // Next Button
         val nextButton: ImageView = playerView.findViewById(R.id.next)
         if(nextId==""){
             nextButton.visibility=View.INVISIBLE
         }
         nextButton.setOnClickListener {
             methodChannel.invokeMethod("nextAction",(player!!.currentPosition/ 1000L).toString()+","+(player!!.duration/ 1000L).toString()+","+nextId)
         }

        //currentTimestamp
        val timestamp = (currentTimestamp as String?)?.toLong()
        if (timestamp != null) {
            player!!.seekTo( timestamp*1000)
        }

        //Ads Setup
        val gson = Gson()
        val dataListType = object : TypeToken<List<Map<String, Any>>>() {}.type
        val adsList: List<Map<String, Any>> = gson.fromJson(adsStreaming, dataListType)

        val contentUri = Uri.parse(streamUrl)

        if(adsList.isNotEmpty()){
            val adTagUri = Uri.parse(adsList[0]["adsStreamingUrl"].toString())
            val contentStart = MediaItem.Builder().setUri(contentUri).setAdsConfiguration(
                MediaItem.AdsConfiguration.Builder(adTagUri).build()).build()
            player!!.addMediaItem(contentStart)
        }else{
            val contentStart = MediaItem.Builder().setUri(contentUri).build()
            player!!.addMediaItem(contentStart)
        }

        player!!.repeatMode = Player.REPEAT_MODE_ALL
        player!!.prepare()
        player!!.playWhenReady = true

        player!!.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_ENDED) {

                    player!!.playWhenReady = false
                    playerView.hideController()
                }
            }

        })
    }


    private fun showQualitySelectionDialog(trackSelector: DefaultTrackSelector) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Video Quality")
        val qualityOptions =
            arrayOf("Auto", "240p","480p","720p", "1080p") // Define your quality options here


        builder.setSingleChoiceItems(
            qualityOptions, selectedQualityIndex
        ) { dialog, which -> // Update track selection based on user input
            updateTrackSelection(which,trackSelector)
            dialog.dismiss()
        }
        builder.create().show()
    }


    private fun updateTrackSelection(index: Int,trackSelector: DefaultTrackSelector) {
        selectedQualityIndex=index;

        val videoBitrate = when (index) {
            0 -> 0
            1 -> 400000
            2 -> 1000000
            3 -> 2500000
            4 -> 5000000
            else -> 0 // default to Auto if unknown quality
        }

        val parametersBuilder = trackSelector.buildUponParameters()

        if (videoBitrate == 0) {
            parametersBuilder.clearOverridesOfType(C.TRACK_TYPE_VIDEO)
        } else {
            parametersBuilder.setMaxVideoBitrate(videoBitrate)
        }

        trackSelector.parameters = parametersBuilder.build()
    }


    private fun releasePlayer() {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        playerView.keepScreenOn = false;
        playerView.player = null
        player!!.release()
        player = null
        player = null
    }

 
    override fun onStop() {
        player!!.pause()
        super.onStop()
    }

    override fun onPause() {
        player!!.pause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player!!.pause()
        releasePlayer()
    }

    override fun onBackPressed() {
        binaryMessenger?.let { MethodChannel(it, "mahar.com/exoplayer").invokeMethod("callBack", (player!!.currentPosition / 1000L).toString() + "," + (player!!.duration / 1000L).toString()) }
        super.onBackPressed()
    }

}
