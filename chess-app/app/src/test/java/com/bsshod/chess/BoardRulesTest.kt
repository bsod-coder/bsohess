package com.bsshod.chess

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Square
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BoardRulesTest {
    @Test fun startingPositionHasTwentyLegalMoves() {
        val board = Board()
        assertEquals(20, board.legalMoves().size)
        assertFalse(board.legalMoves().any { it.from == Square.E2 && it.to == Square.E5 })
    }
}
