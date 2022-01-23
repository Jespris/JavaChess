package com.tests.chess.engine.board;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MoveFactory;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMiniMax {
    @Test
    public void FoolsMate(){
        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("f2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g4")));

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
        final Move aiMove = strategy.execute(t3.getToBoard());
        final Move bestMove = Move.MoveFactory.createMove(
                t3.getToBoard(),
                BoardUtils.getCoordinateAtPosition("d8"),
                BoardUtils.getCoordinateAtPosition("h4"));
        assertEquals(aiMove, bestMove);
    }
}
