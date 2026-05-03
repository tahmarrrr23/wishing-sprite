package com.example.wishingsprite.core.data.slm.datasource

import com.example.wishingsprite.core.model.slm.SlmInferenceRequest
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.LogSeverity
import com.google.ai.edge.litertlm.SamplerConfig
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Singleton
class LiteRtLmDataSource @Inject constructor() {
  private val engineMutex = Mutex()
  private var engine: Engine? = null
  private var loadedModelPath: String? = null

  suspend fun initialize(modelFile: File, cacheDirectory: File) =
    engineMutex.withLock {
      if (loadedModelPath == modelFile.absolutePath && engine?.isInitialized() == true) {
        return@withLock
      }

      closeEngine()
      cacheDirectory.mkdirs()

      withContext(Dispatchers.Default) {
        Engine.setNativeMinLogSeverity(LogSeverity.ERROR)
        val initializedEngine =
          Engine(
            EngineConfig(
              modelPath = modelFile.absolutePath,
              backend = Backend.CPU(),
              cacheDir = cacheDirectory.absolutePath,
            )
          )
        initializedEngine.initialize()
        engine = initializedEngine
        loadedModelPath = modelFile.absolutePath
      }
    }

  fun generateTextStream(request: SlmInferenceRequest): Flow<String> =
    flow {
        engineMutex.withLock {
          val currentEngine = requireNotNull(engine) { "モデルが読み込まれていません。" }
          check(currentEngine.isInitialized()) { "モデルの準備が完了していません。" }

          val conversationConfig =
            ConversationConfig(
              systemInstruction = request.systemInstruction?.let(Contents::of),
              samplerConfig =
                SamplerConfig(
                  topK = request.topK,
                  topP = request.topP,
                  temperature = request.temperature,
                  seed = request.seed ?: Random.nextInt(from = 1, until = Int.MAX_VALUE),
                ),
            )

          currentEngine.createConversation(conversationConfig).use { conversation ->
            conversation.sendMessageAsync(request.prompt).collect { message ->
              emit(message.toString())
            }
          }
        }
      }
      .flowOn(Dispatchers.Default)

  suspend fun close() = engineMutex.withLock { closeEngine() }

  private fun closeEngine() {
    engine?.close()
    engine = null
    loadedModelPath = null
  }
}
