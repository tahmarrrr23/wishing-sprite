package com.example.wishingsprite.core.model.slm

data class SlmInferenceRequest(
  val prompt: String,
  val systemInstruction: String? = null,
  val topK: Int = 40,
  val topP: Double = 0.95,
  val temperature: Double = 0.9,
  val seed: Int? = null,
)
