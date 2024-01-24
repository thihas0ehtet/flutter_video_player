//package com.example.video_player
//
//import android.app.Activity
//import android.content.Context
//import android.content.pm.ActivityInfo
//import android.net.Uri
//import android.view.*
//import android.widget.LinearLayout
//import androidx.core.view.WindowCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat
//import androidx.media3.common.MediaItem
//import androidx.media3.datasource.DataSource
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.ui.PlayerView
//import com.google.android.exoplayer2.source.MediaSource
//import com.google.android.exoplayer2.source.hls.HlsMediaSource
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import io.flutter.plugin.platform.PlatformView
//
//
//internal class MyPlayerView(private  val context: Context, id: Int, creationParams: Map<String?, Any?>?,
//                            private  val activity: Activity) : PlatformView
//{
//    private val linerLayout: LinearLayout=LinearLayout(context);
//    private var player: ExoPlayer? = null
//    private var playWhenReady=true
//    private var currentItem=0
//    private var playBackPosition=0L
//    private val playerView: PlayerView
//
//
//    override fun getView(): View {
//        return linerLayout
//    }
//
//    override fun dispose() {
//        releasePlayer()
//    }
//
//    init {
//        var layoutParams:ViewGroup.LayoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
//        linerLayout.layoutParams=layoutParams;
//        playerView = PlayerView(context)
//        playerView.layoutParams=layoutParams
//        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        linerLayout.addView(playerView)
//        setUpPlayer(creationParams?.get("url").toString())
//    }
//
//    private fun setUpPlayer(url:String){
//      player= ExoPlayer.Builder(context).build()
//              .also { exoPlayer ->
//                  print(url)
//
//                  playerView.player=exoPlayer
//                  val uri = Uri.parse(url)
//                  val mediaSource: MediaSource = buildMediaSource(uri)
//
////                 val mediaItem=MediaItem.fromUri(url)
//                  val mediaItem=MediaItem.fromUri("https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8")
//                  exoPlayer.setMediaItem(mediaItem)
//                  exoPlayer.playWhenReady=playWhenReady
//                  exoPlayer.seekTo(currentItem,playBackPosition)
//                  exoPlayer.prepare()
//              }
//            hideSystemUi()
//    }
//
//    private fun hideSystemUi(){
//        WindowCompat.setDecorFitsSystemWindows(activity.window,false)
//        WindowInsetsControllerCompat(activity.window,playerView).let { controller->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior=WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }
//
////    private fun buildMediaSource(uri: Uri): MediaSource? {
////        val dataSourceFactory: DataSource.Factory =
////            DefaultDataSourceFactory(this, "exoplayer-codelab")
////        return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
////    }
//
//    private fun releasePlayer(){
//        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//       player?.let { exoPlayer ->
//           playBackPosition=exoPlayer.currentPosition
//           currentItem=exoPlayer.currentMediaItemIndex
//           playWhenReady=exoPlayer.playWhenReady
//           exoPlayer.release()
//       }
//        player=null
//
//    }
//}
