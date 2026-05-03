package com.example.wishingsprite.core.data.slm.repository

import android.net.Uri
import com.example.wishingsprite.core.data.slm.datasource.LiteRtLmDataSource
import com.example.wishingsprite.core.data.slm.datasource.SlmModelStorageDataSource
import com.example.wishingsprite.core.model.slm.SlmInferenceRequest
import com.example.wishingsprite.core.model.slm.SlmModelStatus
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class DefaultSlmRepository
@Inject
constructor(
  private val storageDataSource: SlmModelStorageDataSource,
  private val liteRtLmDataSource: LiteRtLmDataSource,
) : SlmRepository {
  private val modelMutex = Mutex()
  private val modelStatus = MutableStateFlow<SlmModelStatus>(SlmModelStatus.NotLoaded)

  override fun getModelStatusStream(): Flow<SlmModelStatus> = modelStatus.asStateFlow()

  override suspend fun restoreCachedModel() =
    modelMutex.withLock {
      if (
        modelStatus.value is SlmModelStatus.Ready || modelStatus.value is SlmModelStatus.Loading
      ) {
        return@withLock
      }

      val cachedModel = storageDataSource.getCachedModel() ?: return@withLock
      modelStatus.value = SlmModelStatus.Loading(cachedModel.name)
      runCatching {
          liteRtLmDataSource.initialize(
            cachedModel.file,
            storageDataSource.getLiteRtCacheDirectory(),
          )
        }
        .onSuccess { modelStatus.value = SlmModelStatus.Ready(cachedModel.name) }
        .onFailure { error -> modelStatus.value = SlmModelStatus.Error(error.userMessage()) }
    }

  override suspend fun loadModelFromUri(uri: Uri) =
    modelMutex.withLock {
      modelStatus.value = SlmModelStatus.Loading()
      runCatching {
          val cachedModel = storageDataSource.cacheModel(uri)
          liteRtLmDataSource.initialize(
            cachedModel.file,
            storageDataSource.getLiteRtCacheDirectory(),
          )
          cachedModel
        }
        .onSuccess { cachedModel -> modelStatus.value = SlmModelStatus.Ready(cachedModel.name) }
        .onFailure { error ->
          liteRtLmDataSource.close()
          storageDataSource.clearCachedModel()
          modelStatus.value = SlmModelStatus.Error(error.userMessage())
        }
      Unit
    }

  override suspend fun unloadModel() =
    modelMutex.withLock {
      runCatching {
          liteRtLmDataSource.close()
          storageDataSource.clearCachedModel()
          storageDataSource.clearLiteRtCache()
        }
        .onSuccess { modelStatus.value = SlmModelStatus.NotLoaded }
        .onFailure { error -> modelStatus.value = SlmModelStatus.Error(error.userMessage()) }
      Unit
    }

  override fun generateTextStream(request: SlmInferenceRequest): Flow<String> =
    liteRtLmDataSource
      .generateTextStream(request)
      .onStart {
        if (modelStatus.value !is SlmModelStatus.Ready) {
          error("設定画面でモデルを読み込んでください。")
        }
      }
      .catch { error -> throw IllegalStateException(error.userMessage(), error) }

  private fun Throwable.userMessage(): String = message?.takeIf { it.isNotBlank() } ?: "処理に失敗しました。"
}
