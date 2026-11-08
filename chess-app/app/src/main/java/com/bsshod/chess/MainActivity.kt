package com.bsshod.chess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bsshod.chess.ui.ChessApp
import com.bsshod.chess.ui.ChessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ChessTheme { ChessApp() } }
    }
}
