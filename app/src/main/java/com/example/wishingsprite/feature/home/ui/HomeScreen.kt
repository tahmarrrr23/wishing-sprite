package com.example.wishingsprite.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wishingsprite.core.ui.component.MarkdownText
import com.example.wishingsprite.core.ui.component.WishingSpriteScreenLayout
import com.example.wishingsprite.core.ui.component.WishingSpriteTopAppBar
import com.example.wishingsprite.core.ui.theme.WishingSpriteTheme

@Composable
fun HomeScreen(
  onDebugClick: () -> Unit,
  onSettingsClick: () -> Unit,
  viewModel: HomeViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  HomeScreenContent(
    uiState = uiState,
    onHowdyClick = viewModel::onHowdyClick,
    onDebugClick = onDebugClick,
    onSettingsClick = onSettingsClick,
  )
}

@Composable
private fun HomeScreenContent(
  uiState: HomeUiState,
  onHowdyClick: () -> Unit,
  onDebugClick: () -> Unit,
  onSettingsClick: () -> Unit,
) {
  WishingSpriteScreenLayout(
    topBar = {
      WishingSpriteTopAppBar(
        actions = {
          IconButton(onClick = onDebugClick) {
            Icon(imageVector = Icons.Filled.BugReport, contentDescription = "Open debug")
          }
          IconButton(onClick = onSettingsClick) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "Open settings")
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
      item { HowdyPanel(uiState = uiState, onHowdyClick = onHowdyClick) }
    }
  }
}

@Composable
private fun HowdyPanel(uiState: HomeUiState, onHowdyClick: () -> Unit) {
  Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Button(
      enabled = uiState.isModelReady && !uiState.isGenerating && !uiState.isModelLoading,
      onClick = onHowdyClick,
    ) {
      if (uiState.isGenerating) {
        CircularProgressIndicator(
          modifier = Modifier.size(18.dp),
          strokeWidth = 2.dp,
          color = MaterialTheme.colorScheme.onPrimary,
        )
      } else {
        Icon(imageVector = Icons.Filled.WavingHand, contentDescription = null)
      }
      Text(text = "Howdy", modifier = Modifier.padding(start = 8.dp))
    }

    uiState.greeting?.let { greeting ->
      MarkdownText(markdown = greeting, style = MaterialTheme.typography.titleMedium)
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
  WishingSpriteTheme {
    HomeScreenContent(
      uiState = HomeUiState(isModelReady = true),
      onHowdyClick = {},
      onDebugClick = {},
      onSettingsClick = {},
    )
  }
}
