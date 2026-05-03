package com.example.wishingsprite.core.model.slm

sealed interface SlmModelStatus {
  data object NotLoaded : SlmModelStatus

  data class Loading(val modelName: String? = null) : SlmModelStatus

  data class Ready(val modelName: String) : SlmModelStatus

  data class Error(val message: String) : SlmModelStatus
}
