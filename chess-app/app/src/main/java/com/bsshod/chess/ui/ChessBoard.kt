package com.bsshod.chess.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import kotlin.math.roundToInt

private fun squareAt(column: Int, row: Int, flipped: Boolean): Square {
    val file = if (flipped) 7 - column else column
    val rank = if (flipped) row else 7 - row
    return Square.valueOf("${('A' + file)}${rank + 1}")
}

@Composable
fun ChessBoard() {
    val board = remember { Board() }
    var revision by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<Square?>(null) }
    var flipped by remember { mutableStateOf(false) }
    var lastMove by remember { mutableStateOf<Move?>(null) }
    var promotions by remember { mutableStateOf<List<Move>>(emptyList()) }
    var dragFrom by remember { mutableStateOf<Square?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    val haptic = LocalHapticFeedback.current
    val legal = remember(revision) { board.legalMoves() }
    val fen = remember(revision) { board.fen }

    fun makeMove(move: Move) {
        if (board.doMove(move)) {
            lastMove = move
            revision++
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        selected = null
    }
    fun chooseTarget(target: Square) {
        val candidates = legal.filter { it.from == selected && it.to == target }
        when {
            candidates.size > 1 -> promotions = candidates
            candidates.size == 1 -> makeMove(candidates.first())
            board.getPiece(target).pieceSide == board.sideToMove -> selected = target
            else -> selected = null
        }
    }

    if (promotions.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { promotions = emptyList() },
            title = { Text("Choose promotion") },
            text = {
                Row {
                    promotions.forEach { move ->
                        TextButton(onClick = { makeMove(move); promotions = emptyList() }) {
                            Text(move.promotion.fanSymbol, fontSize = 28.sp)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { promotions = emptyList() }) { Text("Cancel") } }
        )
    }

    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(if (board.sideToMove.name == "WHITE") "White to move" else "Black to move", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { flipped = !flipped }) { Text("Flip board") }
        }
        BoxWithConstraints(Modifier.fillMaxWidth().aspectRatio(1f)) {
            val size = maxWidth / 8
            val px = constraints.maxWidth / 8f
            fun at(position: Offset): Square? {
                val x = (position.x / px).toInt()
                val y = (position.y / px).toInt()
                return if (position.x >= 0 && position.y >= 0 && x in 0..7 && y in 0..7) squareAt(x, y, flipped) else null
            }
            Box(Modifier.fillMaxSize()
                .pointerInput(flipped, revision, selected) { detectTapGestures { p -> at(p)?.let(::chooseTarget) } }
                .pointerInput(flipped, revision) {
                    detectDragGestures(
                        onDragStart = { p -> dragFrom = at(p); selected = dragFrom; dragPosition = p },
                        onDragEnd = { at(dragPosition)?.let(::chooseTarget); dragFrom = null },
                        onDragCancel = { dragFrom = null; selected = null },
                        onDrag = { change, amount -> change.consume(); dragPosition += amount }
                    )
                }) {
                for (row in 0..7) for (column in 0..7) {
                    val square = squareAt(column, row, flipped)
                    val piece = remember(fen, square) { board.getPiece(square) }
                    val light = (row + column) % 2 == 0
                    val marked = lastMove?.let { it.from == square || it.to == square } == true || selected == square
                    val highlight by animateFloatAsState(if (marked) 0.38f else 0f, label = "highlight")
                    Box(Modifier.offset(x = size * column, y = size * row).size(size)
                        .background(if (light) Color(0xFFE6EAD9) else Color(0xFF788E6B))
                        .background(Color(0xFFFFD568).copy(alpha = highlight)), contentAlignment = Alignment.Center) {
                        if (selected != null && legal.any { it.from == selected && it.to == square }) {
                            Box(Modifier.size(if (piece == Piece.NONE) 12.dp else 34.dp).background(Color.Black.copy(alpha = 0.22f), CircleShape))
                        }
                        if (piece != Piece.NONE && dragFrom != square) {
                            Text(piece.fanSymbol, fontSize = (size.value * 0.7f).sp, textAlign = TextAlign.Center, color = Color.Black)
                        }
                    }
                }
                dragFrom?.let { from ->
                    val piece = board.getPiece(from)
                    if (piece != Piece.NONE) Text(piece.fanSymbol,
                        modifier = Modifier.offset { IntOffset((dragPosition.x - px / 2).roundToInt(), (dragPosition.y - px / 2).roundToInt()) }.size(size),
                        fontSize = (size.value * 0.8f).sp, textAlign = TextAlign.Center, color = Color.Black)
                }
            }
        }
    }
}
