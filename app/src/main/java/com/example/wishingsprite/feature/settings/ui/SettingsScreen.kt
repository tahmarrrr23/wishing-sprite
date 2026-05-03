package com.example.wishingsprite.feature.settings.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wishingsprite.core.ui.component.WishingSpriteScreenLayout
import com.example.wishingsprite.core.ui.component.WishingSpriteTopAppBar
import com.example.wishingsprite.core.ui.theme.WishingSpriteTheme

@Composable
fun SettingsScreen(onBackClick: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val modelPicker =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
      if (uri != null) {
        viewModel.loadModel(uri)
      }
    }

  SettingsScreenContent(
    uiState = uiState,
    onBackClick = onBackClick,
    onLoadModelClick = { modelPicker.launch(arrayOf("*/*")) },
    onUnloadModelClick = viewModel::unloadModel,
  )
}

@Composable
private fun SettingsScreenContent(
  uiState: SettingsUiState,
  onBackClick: () -> Unit,
  onLoadModelClick: () -> Unit,
  onUnloadModelClick: () -> Unit,
) {
  WishingSpriteScreenLayout(
    topBar = {
      WishingSpriteTopAppBar(
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        }
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      item {
        SlmModelSettings(
          uiState = uiState,
          onLoadModelClick = onLoadModelClick,
          onUnloadModelClick = onUnloadModelClick,
        )
      }
    }
  }
}

@Composable
private fun SlmModelSettings(
  uiState: SettingsUiState,
  onLoadModelClick: () -> Unit,
  onUnloadModelClick: () -> Unit,
) {
  Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(text = "SLM model", style = MaterialTheme.typography.titleMedium)
    Text(text = uiState.modelName ?: "未選択", style = MaterialTheme.typography.bodyLarge)
    Text(
      text = uiState.message,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = !uiState.isLoading,
        onClick = onLoadModelClick,
      ) {
        Icon(imageVector = Icons.Filled.UploadFile, contentDescription = null)
        Text(text = "モデルを読み込む", modifier = Modifier.padding(start = 8.dp))
      }

      OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        enabled = uiState.canUnloadModel && !uiState.isLoading,
        onClick = onUnloadModelClick,
      ) {
        Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = null)
        Text(text = "モデルをアンロード", modifier = Modifier.padding(start = 8.dp))
      }

      if (uiState.isLoading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
  WishingSpriteTheme {
    SettingsScreenContent(
      uiState =
        SettingsUiState(
          modelName = "Gemma3-1B-IT.litertlm",
          canUnloadModel = true,
          message = "読み込み済み",
        ),
      onBackClick = {},
      onLoadModelClick = {},
      onUnloadModelClick = {},
    )
  }
}
