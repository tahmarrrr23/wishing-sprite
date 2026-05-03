package com.example.wishingsprite.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wishingsprite.core.ui.theme.WishingSpriteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onDebugClick: () -> Unit, onSettingsClick: () -> Unit) {
  val homeMessages =
    List(36) { index ->
      "Home message ${index + 1}: Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
        "Integer wishes drift through the morning light, and every small note keeps the " +
        "screen pleasantly full for scrolling."
    }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        actions = {
          IconButton(onClick = onDebugClick) {
            Icon(imageVector = Icons.Filled.BugReport, contentDescription = "Open debug")
          }
          IconButton(onClick = onSettingsClick) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "Open settings")
          }
        },
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      items(homeMessages) { message ->
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
  WishingSpriteTheme { HomeScreen(onDebugClick = {}, onSettingsClick = {}) }
}
