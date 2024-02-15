package com.example.video_player

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.*
import android.widget.ImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

@SuppressLint("SourceLockedOrientationActivity")
fun ExoPlayer.preparePlayer(playerView: PlayerView,player: ExoPlayer,
                            mainActivity: MainActivity, methodChannel: io.flutter.plugin.common.MethodChannel) {
    (playerView.context as Context).apply {
        val playerViewFullscreen = PlayerView(this)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        playerViewFullscreen.layoutParams = layoutParams
        playerViewFullscreen.visibility = View.GONE
        playerViewFullscreen.setBackgroundColor(Color.TRANSPARENT)
        (playerView.rootView as ViewGroup).apply { addView(playerViewFullscreen, childCount) }


        val backwardButton: ImageView = playerView.findViewById(R.id.exo_rew)
        backwardButton.setOnClickListener {
            player.seekTo(player.currentPosition - 10000)
        }

        val forwardButton: ImageView = playerView.findViewById(R.id.exo_ffwd)
        forwardButton.setOnClickListener {
            player.seekTo(player.currentPosition + 10000)
        }


//        val qualityButton: ImageView = playerView.findViewById(R.id.exo_setting_icon)
//            qualityButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_settings))
//            qualityButton.setOnClickListener {
//        }

        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}
