package com.example.wishingsprite.core.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishingSpriteTopAppBar(
  title: @Composable () -> Unit = {},
  navigationIcon: (@Composable () -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
) {
  TopAppBar(title = title, navigationIcon = { navigationIcon?.invoke() }, actions = actions)
}
