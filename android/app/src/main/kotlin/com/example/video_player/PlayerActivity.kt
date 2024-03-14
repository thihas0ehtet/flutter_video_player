package com.example.video_player

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel


object MessengerHolder {
    var binaryMessenger: BinaryMessenger? = null
}

class PlayerActivity() : Activity() {

    private lateinit var playerView: PlayerView
    private var player: ExoPlayer? = null

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

        if (methodChannel != null) {
            initializePlayer(streamUrl,videoTitle,currentTimestamp,methodChannel)
        }

        relativeLayout.addView(playerView)
        relativeLayout.addView(logoView)
        relativeLayout.addView(userNumberView)


        setContentView(relativeLayout)

    }

    private fun initializePlayer(
        streamUrl:String,
        videoTitle:String,
        currentTimestamp:String,
       methodChannel: MethodChannel
    ) {

        val trackSelector = DefaultTrackSelector(this)


        trackSelector.parameters = trackSelector.parameters
            .buildUpon()
            .setMaxVideoSizeSd()
            .setMaxVideoBitrate(500000) // Set your desired maximum bitrate
            .build()



        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(this, Util.getUserAgent(playerView.context, "mahar"))

        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
            //    .setAdsLoaderProvider { adsLoader }
            .setAdViewProvider(playerView)

        player = ExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build()

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
            showQualitySelectionDialog()
        }

        //currentTimestamp
        val timestamp = (currentTimestamp as String?)?.toLong()
        if (timestamp != null) {
            player!!.seekTo( timestamp*1000)
        }


        val contentUri = Uri.parse(streamUrl)
        val contentStart = MediaItem.Builder().setUri(contentUri).build()

        player!!.addMediaItem(contentStart)
        player!!.repeatMode = Player.REPEAT_MODE_ALL
        player!!.prepare()
        player!!.playWhenReady = true
    }

    private fun showQualitySelectionDialog(context: Context, trackSelector: DefaultTrackSelector) {
        val availableTracks = trackSelector.currentMappedTrackInfo

        if (availableTracks != null) {
            val trackSelectionDialogBuilder = AlertDialog.Builder(context)
            trackSelectionDialogBuilder.setTitle("Select Video Quality")

            val qualityOptions = mutableListOf<String>()

            for (i in 0 until availableTracks.rendererCount) {
                val trackType = availableTracks.getRendererType(i)
                if (trackType == C.TRACK_TYPE_VIDEO) {
                    for (j in 0 until availableTracks.getTrackGroups(i).length) {
                        val trackGroup = availableTracks.getTrackGroups(i).get(j)
                        for (k in 0 until trackGroup.length) {
                            val trackFormat = trackGroup.getFormat(k)
                            // Add track quality to the list
                            qualityOptions.add(trackFormat.height.toString() + "p")
                        }
                    }
                }
            }

            val qualityArray = qualityOptions.toTypedArray()

            trackSelectionDialogBuilder.setItems(qualityArray) { dialog, which ->
                // Find the corresponding track selection index based on the selected quality
                var selectedIndex = 0
                for (i in 0 until availableTracks.rendererCount) {
                    val trackType = availableTracks.getRendererType(i)
                    if (trackType == C.TRACK_TYPE_VIDEO) {
                        for (j in 0 until availableTracks.getTrackGroups(i).length) {
                            val trackGroup = availableTracks.getTrackGroups(i).get(j)
                            for (k in 0 until trackGroup.length) {
                                val trackFormat = trackGroup.getFormat(k)
                                if (qualityArray[which].contains(trackFormat.height.toString())) {
                                    selectedIndex = k
                                }
                            }
                        }
                    }
                }

                // Set the selected track
                trackSelector.parameters = trackSelector.buildUponParameters()
                    .setSelectionOverride(
                        /* rendererIndex= */ 0,
                        availableTracks.getTrackGroups(/* rendererIndex= */ 0),
                        DefaultTrackSelector.SelectionOverride(/* groupIndex= */ 0, selectedIndex)
                    )
                    .build()

                dialog.dismiss()
            }

            trackSelectionDialogBuilder.show()
        } else {
            // Handle case when no track info is available
            Toast.makeText(context, "No video tracks available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showQualitySelectionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Video Quality")
        val qualityOptions =
            arrayOf("Auto", "240p","480p","720p", "1080p") // Define your quality options here
        builder.setItems(qualityOptions) { dialog, which -> // Update track selection based on user input
            when (which) {
                0 -> updateTrackSelection(0)
                1 -> updateTrackSelection(1)
                2 -> updateTrackSelection(2)
                3 -> updateTrackSelection(3)
                4 -> updateTrackSelection(4)
            }
        }
        val dialog = builder.create()
        dialog.show()
    }


    // Method to update track selection based on user input
    private fun updateTrackSelection(selectedQualityIndex: Int) {

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

}
