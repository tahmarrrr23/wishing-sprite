package com.example.wishingsprite.feature.settings.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishingsprite.core.data.slm.repository.SlmRepository
import com.example.wishingsprite.core.model.slm.SlmModelStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val slmRepository: SlmRepository) :
  ViewModel() {
  val uiState: StateFlow<SettingsUiState> =
    slmRepository
      .getModelStatusStream()
      .map { status -> status.toUiState() }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
      )

  init {
    viewModelScope.launch { slmRepository.restoreCachedModel() }
  }

  fun loadModel(uri: Uri) {
    viewModelScope.launch { slmRepository.loadModelFromUri(uri) }
  }

  fun unloadModel() {
    viewModelScope.launch { slmRepository.unloadModel() }
  }

  private fun SlmModelStatus.toUiState(): SettingsUiState =
    when (this) {
      SlmModelStatus.NotLoaded -> SettingsUiState()
      is SlmModelStatus.Loading ->
        SettingsUiState(
          modelName = modelName,
          isLoading = true,
          message = modelName?.let { "$it を読み込み中" } ?: "モデル読み込み中",
        )
      is SlmModelStatus.Ready ->
        SettingsUiState(modelName = modelName, canUnloadModel = true, message = "読み込み済み")
      is SlmModelStatus.Error -> SettingsUiState(message = message)
    }
}
