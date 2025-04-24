package com.example.mediapipe_bridge

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.util.Log


lateinit var faceLandmarkerHelper: FaceLandmarkerHelper



class MainActivity : FlutterActivity() {
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        faceLandmarkerHelper = FaceLandmarkerHelper(context = this)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "mediapipe_bridge/channel")
            .setMethodCallHandler { call, result ->
                if (call.method == "processImage") {
                    val path = call.argument<String>("path")
                    if (path != null) {
                        val response = ImageProcessor.processImage(this, path)
                        if (response is Map<*, *>) {
                            result.success(response)
                        } else {
                            result.success(response.toString())
                        }
                    } else {
                        result.error("INVALID_ARGUMENT", "Image path is null", null)
                    }
                } else {
                    result.notImplemented()
                }
            }
    }
}

