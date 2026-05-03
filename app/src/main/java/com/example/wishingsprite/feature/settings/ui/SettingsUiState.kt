package com.example.wishingsprite.feature.settings.ui

data class SettingsUiState(
  val modelName: String? = null,
  val isLoading: Boolean = false,
  val canUnloadModel: Boolean = false,
  val message: String = "モデル未読み込み",
)
