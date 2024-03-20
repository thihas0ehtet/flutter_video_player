package com.example.video_player

import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity: FlutterActivity() {

    private val channel = "mahar.com/exoplayer"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler {
                call, result ->

            if (call.method == "PlayerView") {
                val streamUrl: String? = call.argument("streamUrl")
                val videoTitle: String? = call.argument("videoName")
                val userNumber: String? = call.argument("userNumber")
                val currentTimestamp: String? = call.argument("currentTimestamp")
                val previousId: String? = call.argument("previousId")
                val nextId: String? = call.argument("nextId")
                val adsStreaming: String?= call.argument("adsStreaming")


                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("streamUrl", streamUrl)
                    putExtra("videoTitle", videoTitle)
                    putExtra("userNumber", userNumber)
                    putExtra("currentTimestamp", currentTimestamp)
                    putExtra("previousId", previousId)
                    putExtra("nextId", nextId)
                    putExtra("adsStreaming", adsStreaming)
                }

                MessengerHolder.binaryMessenger = flutterEngine.dartExecutor.binaryMessenger
                startActivity(intent)

                result.success(null)
            }else
             {
                result.notImplemented()
            }
        }
    }

}