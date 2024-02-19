package com.example.video_player

import android.os.Bundle
import android.os.PersistableBundle
import com.google.android.exoplayer2.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

class MainActivity: FlutterActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        flutterEngine
            .platformViewsController
            .registry
            .registerViewFactory("PlayerView", PlayerViewFactory(flutterEngine.dartExecutor.binaryMessenger,this))
    }

    override fun onStop() {
        super.onStop()
    }

}