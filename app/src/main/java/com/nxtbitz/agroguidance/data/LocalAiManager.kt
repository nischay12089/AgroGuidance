package com.nxtbitz.agroguidance.data

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class LocalAiManager(private val context: Context) {
    private var llmInference: LlmInference? = null
    private val TAG = "LocalAiManager"

    private val modelPath: String by lazy {
        File(context.filesDir, "model.bin").absolutePath
    }

    val isModelAvailable: Boolean
        get() = File(modelPath).exists() || isModelInAssets()

    private fun isModelInAssets(): Boolean {
        return try {
            context.assets.list("")?.contains("model.bin") == true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun copyModelFromAssets() = withContext(Dispatchers.IO) {
        val targetFile = File(modelPath)
        
        // If file exists, we check if it's likely corrupted (e.g., 0 bytes)
        // or if we should force a re-copy. 
        // For simplicity, we only copy if it doesn't exist.
        if (targetFile.exists() && targetFile.length() > 0) return@withContext

        Log.d(TAG, "Copying model from assets to ${targetFile.absolutePath}...")
        try {
            context.assets.open("model.bin").use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d(TAG, "Model copy complete. Size: ${targetFile.length()} bytes")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy model from assets", e)
            if (targetFile.exists()) targetFile.delete()
            throw e
        }
    }

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (llmInference != null) return@withContext
        
        if (!isModelAvailable) {
            throw Exception("Model file not found. Please add 'model.bin' to app/src/main/assets/ or upload it to the app's files directory.")
        }

        // Ensure file is in internal storage as MediaPipe requires a direct path
        if (isModelInAssets()) {
            copyModelFromAssets()
        }

        val targetFile = File(modelPath)
        if (!targetFile.exists() || targetFile.length() == 0L) {
            throw Exception("Model file is missing or empty at $modelPath")
        }

        Log.d(TAG, "Initializing LlmInference with model at $modelPath (Size: ${targetFile.length()})")

        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(512)
                .setTemperature(0.7f)
                .setRandomSeed(42)
                .build()

            llmInference = LlmInference.createFromOptions(context, options)
            Log.d(TAG, "LlmInference initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize LlmInference", e)
            // The "Length and offset too large" error often means the file is truncated.
            // If initialization fails, we might want to delete the file so it can be re-copied next time.
            if (e.message?.contains("Length and offset too large") == true) {
                Log.w(TAG, "Detected truncated model file. Deleting for re-copy.")
                targetFile.delete()
            }
            throw Exception("Failed to initialize AI session: ${e.message}. If this persists, please ensure your model file is not corrupted and is fully copied.")
        }
    }

    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            if (llmInference == null) {
                initialize()
            }
            llmInference?.generateResponse(prompt) ?: "Error: Inference engine not initialized."
        } catch (e: Exception) {
            Log.e(TAG, "Generation failed", e)
            "Error: ${e.message}"
        }
    }
}
