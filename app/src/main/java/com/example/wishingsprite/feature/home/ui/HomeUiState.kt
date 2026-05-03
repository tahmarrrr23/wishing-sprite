package com.example.wishingsprite.feature.home.ui

data class HomeUiState(
  val isModelReady: Boolean = false,
  val isModelLoading: Boolean = false,
  val isGenerating: Boolean = false,
  val greeting: String? = null,
)
