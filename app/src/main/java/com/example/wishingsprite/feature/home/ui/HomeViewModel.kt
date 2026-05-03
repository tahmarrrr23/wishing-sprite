package com.example.wishingsprite.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishingsprite.core.data.slm.repository.SlmRepository
import com.example.wishingsprite.core.model.slm.SlmInferenceRequest
import com.example.wishingsprite.core.model.slm.SlmModelStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(private val slmRepository: SlmRepository) : ViewModel() {
  private val greetingState = MutableStateFlow(GreetingState())
  private var greetingJob: Job? = null

  val uiState: StateFlow<HomeUiState> =
    combine(slmRepository.getModelStatusStream(), greetingState) { modelStatus, greetingState ->
        modelStatus.toUiState(greetingState)
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
      )

  init {
    viewModelScope.launch { slmRepository.restoreCachedModel() }
  }

  fun onHowdyClick() {
    if (greetingJob?.isActive == true) return

    greetingJob =
      viewModelScope.launch {
        greetingState.value = GreetingState(isGenerating = true, greeting = "")
        runCatching {
            slmRepository.generateTextStream(HOWDY_REQUEST).collect { chunk ->
              greetingState.update { state ->
                state.copy(greeting = state.greeting.orEmpty() + chunk)
              }
            }
          }
          .onFailure { greetingState.value = GreetingState(isGenerating = false) }
          .onSuccess { greetingState.update { state -> state.copy(isGenerating = false) } }
      }
  }

  private fun SlmModelStatus.toUiState(greetingState: GreetingState): HomeUiState =
    when (this) {
      SlmModelStatus.NotLoaded ->
        HomeUiState(
          greeting = greetingState.greeting?.takeIf { it.isNotBlank() },
          isGenerating = greetingState.isGenerating,
        )
      is SlmModelStatus.Loading ->
        HomeUiState(
          greeting = greetingState.greeting?.takeIf { it.isNotBlank() },
          isGenerating = greetingState.isGenerating,
          isModelLoading = true,
        )
      is SlmModelStatus.Ready ->
        HomeUiState(
          greeting = greetingState.greeting?.takeIf { it.isNotBlank() },
          isGenerating = greetingState.isGenerating,
          isModelReady = true,
        )
      is SlmModelStatus.Error ->
        HomeUiState(
          greeting = greetingState.greeting?.takeIf { it.isNotBlank() },
          isGenerating = greetingState.isGenerating,
        )
    }

  private data class GreetingState(val isGenerating: Boolean = false, val greeting: String? = null)

  private companion object {
    val HOWDY_REQUEST =
      SlmInferenceRequest(prompt = "今日の一言をお願いします。", systemInstruction = "あなたは文学者です。日本語で回答してください。")
  }
}
