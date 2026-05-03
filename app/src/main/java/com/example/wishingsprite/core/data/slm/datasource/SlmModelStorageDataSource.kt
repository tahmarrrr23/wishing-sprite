package com.example.wishingsprite.core.data.slm.datasource

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val MODEL_DATA_STORE_NAME = "slm_model"
private const val MODEL_NAME_PREFERENCE = "model_name"
private val MODEL_NAME_KEY = stringPreferencesKey(MODEL_NAME_PREFERENCE)

private val Context.slmModelDataStore by preferencesDataStore(name = MODEL_DATA_STORE_NAME)

@Singleton
class SlmModelStorageDataSource
@Inject
constructor(@param:ApplicationContext private val context: Context) {
  private val modelDirectory: File = File(context.filesDir, MODEL_DIRECTORY_NAME)
  private val cachedModelFile: File = File(modelDirectory, CACHED_MODEL_FILE_NAME)

  suspend fun getCachedModel(): CachedSlmModel? =
    withContext(Dispatchers.IO) {
      val modelName =
        context.slmModelDataStore.data.map { preferences -> preferences[MODEL_NAME_KEY] }.first()
          ?: return@withContext null
      if (!cachedModelFile.isFile) return@withContext null

      CachedSlmModel(name = modelName, file = cachedModelFile)
    }

  suspend fun cacheModel(uri: Uri): CachedSlmModel =
    withContext(Dispatchers.IO) {
      val modelName = getDisplayName(uri) ?: CACHED_MODEL_FILE_NAME
      modelDirectory.mkdirs()

      val temporaryFile = File(modelDirectory, TEMPORARY_MODEL_FILE_NAME)
      temporaryFile.delete()

      context.contentResolver.openInputStream(uri).use { input ->
        requireNotNull(input) { "モデルファイルを開けませんでした。" }
        temporaryFile.outputStream().use { output -> input.copyTo(output) }
      }

      if (cachedModelFile.exists()) {
        cachedModelFile.delete()
      }
      check(temporaryFile.renameTo(cachedModelFile)) { "モデルファイルを保存できませんでした。" }

      context.slmModelDataStore.edit { preferences -> preferences[MODEL_NAME_KEY] = modelName }
      CachedSlmModel(name = modelName, file = cachedModelFile)
    }

  suspend fun clearCachedModel() =
    withContext(Dispatchers.IO) {
      cachedModelFile.delete()
      File(modelDirectory, TEMPORARY_MODEL_FILE_NAME).delete()
      context.slmModelDataStore.edit { preferences -> preferences.clear() }
    }

  suspend fun clearLiteRtCache() =
    withContext(Dispatchers.IO) { liteRtCacheDirectoryFile.deleteRecursively() }

  fun getLiteRtCacheDirectory(): File = liteRtCacheDirectoryFile

  private val liteRtCacheDirectoryFile: File
    get() = File(context.cacheDir, LITERT_CACHE_DIRECTORY_NAME)

  private fun getDisplayName(uri: Uri): String? {
    if (uri.scheme == "file") return uri.lastPathSegment

    return context.contentResolver
      .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
      ?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
      }
      ?.takeIf { it.isNotBlank() }
  }

  companion object {
    private const val MODEL_DIRECTORY_NAME = "slm"
    private const val CACHED_MODEL_FILE_NAME = "model.litertlm"
    private const val TEMPORARY_MODEL_FILE_NAME = "model.tmp"
    private const val LITERT_CACHE_DIRECTORY_NAME = "litertlm"
  }
}

data class CachedSlmModel(val name: String, val file: File)
