package com.example.mediapipe_bridge

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class FaceLandmarkerHelper(
    var minFaceDetectionConfidence: Float = DEFAULT_FACE_DETECTION_CONFIDENCE,
    var minFaceTrackingConfidence: Float = DEFAULT_FACE_TRACKING_CONFIDENCE,
    var minFacePresenceConfidence: Float = DEFAULT_FACE_PRESENCE_CONFIDENCE,
    var maxNumFaces: Int = DEFAULT_NUM_FACES,
    var currentDelegate: Int = DELEGATE_CPU,
    val context: Context
) {
    private var faceLandmarker: FaceLandmarker? = null

    init {
        setupFaceLandmarker()
    }

    fun detectImage(image: Bitmap): ResultBundle? {
        val startTime = SystemClock.uptimeMillis()
        val mpImage = BitmapImageBuilder(image).build()

        return faceLandmarker?.detect(mpImage)?.let { result ->
            val inferenceTimeMs = SystemClock.uptimeMillis() - startTime
            ResultBundle(result, inferenceTimeMs, image.height, image.width)
        }
    }

    private fun setupFaceLandmarker() {
        try {
            val baseOptions = BaseOptions.builder()
                .setDelegate(if (currentDelegate == DELEGATE_GPU) Delegate.GPU else Delegate.CPU)
                .setModelAssetPath(MP_FACE_LANDMARKER_TASK)
                .build()

            val options = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinFaceDetectionConfidence(minFaceDetectionConfidence)
                .setMinTrackingConfidence(minFaceTrackingConfidence)
                .setMinFacePresenceConfidence(minFacePresenceConfidence)
                .setNumFaces(maxNumFaces)
                .setOutputFaceBlendshapes(true)
                .setRunningMode(RunningMode.IMAGE)
                .build()

            faceLandmarker = FaceLandmarker.createFromOptions(context, options)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize FaceLandmarker: ${e.message}")
        }
    }

    companion object {
        private const val MP_FACE_LANDMARKER_TASK = "face_landmarker.task"
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_FACE_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_FACE_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_FACE_PRESENCE_CONFIDENCE = 0.5F
        const val DEFAULT_NUM_FACES = 1
        const val TAG = "FaceLandmarkerHelper"
    }

    data class ResultBundle(
        val result: FaceLandmarkerResult,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )
}
