package com.example.video_player

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

//        flutterEngine
//            .platformViewsController
//            .registry
//            .registerViewFactory("NativeUI", NativeViewFactory(flutterEngine.dartExecutor.binaryMessenger,this))

        flutterEngine
                .platformViewsController
                .registry
                .registerViewFactory("MyPlayerView", MyPlayerViewFactory(this))
    }

    override fun onStop() {
        super.onStop()
    }

}