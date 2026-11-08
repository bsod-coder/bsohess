package com.bsshod.chess.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class Destination(val label: String, val symbol: String)
private val destinations = listOf(
    Destination("Play", "♞"), Destination("Analyze", "◷"),
    Destination("Coach", "✦"), Destination("Profile/Import", "♙")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessApp() {
    var selected by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = { TopAppBar(title = { Text(destinations[selected].label) }) },
        bottomBar = {
            NavigationBar {
                destinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = { Text(destination.symbol, style = MaterialTheme.typography.titleLarge) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            if (selected == 0) ChessBoard() else {
                Text("Coming later", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text("This destination is not implemented yet.")
            }
        }
    }
}
