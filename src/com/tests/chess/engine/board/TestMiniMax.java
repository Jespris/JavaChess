package com.tests.chess.engine.board;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MoveFactory;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMiniMax {
    @Test
    public void FoolsMate(){
        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtChessNotation("f2"),
                        BoardUtils.INSTANCE.getCoordinateAtChessNotation("f3")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getTransitionBoard()
                .currentPlayer()
                .makeMove(MoveFactory.createMove(t1.getTransitionBoard(), BoardUtils.INSTANCE.getCoordinateAtChessNotation("e7"),
                        BoardUtils.INSTANCE.getCoordinateAtChessNotation("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getTransitionBoard()
                .currentPlayer()
                .makeMove(MoveFactory.createMove(t2.getTransitionBoard(), BoardUtils.INSTANCE.getCoordinateAtChessNotation("g2"),
                        BoardUtils.INSTANCE.getCoordinateAtChessNotation("g4")));

        assertTrue(t3.getMoveStatus().isDone());

        /*
        final MoveTransition t4 = t3.getTransitionBoard()
                .currentPlayer()
                .makeMove(MoveFactory.createMove(t3.getTransitionBoard(), BoardUtils.INSTANCE.getCoordinateAtChessNotation("d8"),
                        BoardUtils.INSTANCE.getCoordinateAtChessNotation("h4")));
        assertTrue(t4.getMoveStatus().isDone());
        assertTrue(t4.getTransitionBoard().currentPlayer().isInCheckMate());
        */
        final MoveStrategy strategy = new MiniMax(4);
        final Move aiMove = strategy.execute(t3.getTransitionBoard());
        final Move bestMove = Move.MoveFactory.createMove(
                t3.getTransitionBoard(),
                BoardUtils.getCoordinateAtChessNotation("d8"),
                BoardUtils.getCoordinateAtChessNotation("h4"));
        assertEquals(aiMove, bestMove);
    }
}
