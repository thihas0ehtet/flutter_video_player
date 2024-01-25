package com.example.video_player

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.video_player.MainActivity
import com.example.video_player.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView

@SuppressLint("SourceLockedOrientationActivity")
fun ExoPlayer.preparePlayer(playerView: PlayerView,player: ExoPlayer, forceLandscape:Boolean = false,
                            mainActivity: MainActivity, methodChannel: io.flutter.plugin.common.MethodChannel) {
    (playerView.context as Context).apply {
        val playerViewFullscreen = PlayerView(this)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        playerViewFullscreen.layoutParams = layoutParams
        playerViewFullscreen.visibility = View.GONE
        playerViewFullscreen.setBackgroundColor(Color.TRANSPARENT)
        (playerView.rootView as ViewGroup).apply { addView(playerViewFullscreen, childCount) }
//        val forward10sButton: ImageView = playerView.findViewById(R.id.exo_10s_forward_button)
//        forward10sButton.setOnClickListener{
//            player.seekTo(player.currentPosition + 10000)
//        }

        val rewButton: ImageView = playerView.findViewById(R.id.exo_rew)
        rewButton.setOnClickListener {
            player.seekTo(player.currentPosition - 10000)
        }

        val ffwdButton: ImageView = playerView.findViewById(R.id.exo_ffwd)
        ffwdButton.setOnClickListener {
            player.seekTo(player.currentPosition + 10000)
        }


//        val qualityButton: ImageView = playerView.findViewById(R.id.exo_setting_icon)
//            qualityButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_settings))
//            qualityButton.setOnClickListener {
//        }

//        val fullScreenButton: ImageView = playerView.findViewById(R.id.exo_fullscreen_icon)
//        val normalScreenButton: ImageView = playerViewFullscreen.findViewById(R.id.exo_fullscreen_icon)
//        fullScreenButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_open))
//        normalScreenButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_close))
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        fullScreenButton.setOnClickListener {
//            player.seekTo(player.currentPosition + 10000)
////            if (forceLandscape)
////               mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
////            playerView.visibility = View.VISIBLE
////            playerViewFullscreen.visibility = View.VISIBLE
////            methodChannel.invokeMethod("fullScreen",0)
////            PlayerView.switchTargetView(this@preparePlayer, playerView, playerViewFullscreen)
////            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
////            playerView.player = this@preparePlayer
//        }
//        normalScreenButton.setOnClickListener {
//            player.seekTo(player.currentPosition + 10000)
//////            if (forceLandscape)
//////                mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
////            normalScreenButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_close))
////            playerView.visibility = View.VISIBLE
////            playerViewFullscreen.visibility = View.GONE
////            methodChannel.invokeMethod("normalScreen",0)
////            PlayerView.switchTargetView(this@preparePlayer, playerViewFullscreen, playerView)
////            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
////            playerView.player = this@preparePlayer
//        }
    }
}
