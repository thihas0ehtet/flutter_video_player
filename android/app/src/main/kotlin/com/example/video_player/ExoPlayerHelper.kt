//package com.example.video_player
//
//import android.content.Context
//import android.net.Uri
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.android.exoplayer2.source.MediaSource
//import com.google.android.exoplayer2.source.ProgressiveMediaSource
//import com.google.android.exoplayer2.source.ads.AdsMediaSource
//import com.google.android.exoplayer2.upstream.DataSource
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
//import com.google.android.exoplayer2.util.Util
//
//object ExoPlayerHelper {
//
//    fun initializeExoPlayer(context: Context): SimpleExoPlayer {
//        return SimpleExoPlayer.Builder(context).build()
//    }
//
//    fun buildAdsMediaSource(context: Context, videoUrl: String, adUrl: String, player: SimpleExoPlayer): MediaSource {
//
//        val dataSourceFactory: DataSource.Factory =
//                DefaultDataSourceFactory(view.context, Util.getUserAgent(playerView.context, "flios"))
//        val contentMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(Uri.parse(videoUrl))
//
//        val adTagUri = Uri.parse(adUrl)
//
//        val adsMediaSource = AdsMediaSource(
//                contentMediaSource,
//                dataSourceFactory,
//                ImaAdsLoader.Builder(context).setAdTagUri(adTagUri).build(),
//                player.overlayFrameLayout
//        )
//
//        return adsMediaSource
//    }
//
//    private fun getUserAgent(context: Context): String {
//        // Customize the user agent if needed
//        return com.google.android.exoplayer2.util.Util.getUserAgent(context, "mahar")
//    }
//}
