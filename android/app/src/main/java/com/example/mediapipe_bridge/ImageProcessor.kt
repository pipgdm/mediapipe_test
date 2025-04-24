package com.example.mediapipe_bridge

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.io.FileOutputStream

import android.content.Context


object ImageProcessor {

    fun processImage(context: Context, path: String): Any {
        val file = File(path)
        if (!file.exists()) {
            return "❌ File not found at path: $path"
        }

        val bitmap: Bitmap = BitmapFactory.decodeFile(path)
        Log.d("ImageProcessor", "✅ Bitmap loaded: ${bitmap.width}x${bitmap.height}")

        val result = faceLandmarkerHelper.detectImage(bitmap)

        return if (result != null) {
            val landmarks = result.result.faceLandmarks().firstOrNull()
            val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(outputBitmap)
            val paint = Paint().apply {
                color = Color.RED
                strokeWidth = 4f
                style = Paint.Style.FILL
            }

            if (landmarks != null) {
                for (point in landmarks) {
                    val x = point.x() * result.inputImageWidth
                    val y = point.y() * result.inputImageHeight
                    canvas.drawCircle(x, y, 8f, paint)
                }
            }

            // Optional: save to file (for testing)
            val timestamp = System.currentTimeMillis()
            val outputFile = File(context.cacheDir, "landmarked_image_$timestamp.jpg")

            val landmarkList = landmarks?.map {
                mapOf("x" to it.x(), "y" to it.y(), "z" to it.z())
            } ?: emptyList()

            val out = FileOutputStream(outputFile)
            outputBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.close()

            Log.d("FaceLandmarker", "✅ Saved landmarked image to: ${outputFile.absolutePath}")

            return mapOf(
                "path" to outputFile.absolutePath,
                "landmarks" to landmarkList
            )

        } else {
            Log.e("FaceLandmarker", "❌ No face detected")
            "❌ No face detected"
        }
    }
}
