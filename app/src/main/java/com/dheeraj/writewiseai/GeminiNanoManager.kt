
package com.dheeraj.writewiseai

import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation

class GeminiNanoManager {
    private val model = Generation.getClient()

    suspend fun checkAvailability(): String {
        return try {
            when (model.checkStatus()) {
                FeatureStatus.AVAILABLE ->
                    "Gemini Nano is available!"

                FeatureStatus.DOWNLOADABLE ->
                    "Gemini Nano can be downloaded."

                FeatureStatus.DOWNLOADING ->
                    "Gemini Nano is downloading."

                FeatureStatus.UNAVAILABLE ->
                    "Gemini Nano is unavailable on this device."

                else ->
                    "Unknown Gemini Nano status."
            }
        } catch (e: Exception) {
            "Availability check failed: ${e.message}"
        }
    }

    fun close() {
        model.close()
    }
}
