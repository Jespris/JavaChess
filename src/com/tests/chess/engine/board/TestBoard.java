package com.tests.chess.engine.board;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.Iterables;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestBoard {
    @Test
    public void initialBoard(){
        final Board board = Board.createStandardBoard();
        assertEquals(board.currentPlayer().getLegalMoves().size(), 20);
        assertEquals(board.currentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertFalse(board.currentPlayer().isInCheck());
        assertFalse(board.currentPlayer().isInCheckMate());
        assertFalse(board.currentPlayer().hasCastled());
        assertEquals(board.currentPlayer(), board.whitePlayer());
        assertEquals(board.currentPlayer().getOpponent(), board.blackPlayer());
        assertFalse(board.currentPlayer().getOpponent().isInCheck());
        assertFalse(board.currentPlayer().getOpponent().isInCheckMate());
        assertFalse(board.currentPlayer().getOpponent().hasCastled());

        final Iterable<Piece> allPieces = Iterables.concat(board.whitePlayer().getActivePieces(), board.blackPlayer().getActivePieces());
        final Iterable<Move> allMoves = Iterables.concat(board.whitePlayer().getLegalMoves(), board.blackPlayer().getLegalMoves());
        for(final Move move : allMoves) {
            assertFalse(move.isAttackMove());
            assertFalse(move.isCastlingMove());
        }

        assertEquals(Iterables.size(allMoves), 40);
        assertEquals(Iterables.size(allPieces), 32);
    }

    @Test
    public void testAlgebraicNotation() {
        assertEquals(BoardUtils.getChessNotationAtCoordinate(0), "a8");
        assertEquals(BoardUtils.getChessNotationAtCoordinate(1), "b8");
        assertEquals(BoardUtils.getChessNotationAtCoordinate(2), "c8");
        assertEquals(BoardUtils.getChessNotationAtCoordinate(3), "d8");
        assertEquals(BoardUtils.getChessNotationAtCoordinate(4), "e8");
        assertEquals(BoardUtils.getChessNotationAtCoordinate(5), "f8");
        assertEquals(BoardUtils.getChessNotationAtCoordinate(6), "g8");
        assertEquals(BoardUtils.getChessNotationAtCoordinate(7), "h8");
    }

    @Test
    public void mem() {
        final Runtime runtime = Runtime.getRuntime();
        long start, end;
        runtime.gc();
        start = runtime.freeMemory();
        Board board = Board.createStandardBoard();
        end =  runtime.freeMemory();
        System.out.println("That took " + (start-end) + " bytes.");
    }
}