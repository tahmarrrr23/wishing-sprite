package com.example.wishingsprite.core.data.slm.repository

import android.net.Uri
import com.example.wishingsprite.core.model.slm.SlmInferenceRequest
import com.example.wishingsprite.core.model.slm.SlmModelStatus
import kotlinx.coroutines.flow.Flow

interface SlmRepository {
  fun getModelStatusStream(): Flow<SlmModelStatus>

  suspend fun restoreCachedModel()

  suspend fun loadModelFromUri(uri: Uri)

  suspend fun unloadModel()

  fun generateTextStream(request: SlmInferenceRequest): Flow<String>
}
