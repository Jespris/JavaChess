package com.tests.chess.engine.board;

import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Move.MoveFactory;
import com.chess.engine.classic.player.MoveTransition;
import com.chess.engine.classic.player.ai.AlphaBetaWithMoveSorting;
import com.chess.engine.classic.player.ai.MiniMax;
import com.chess.engine.classic.player.ai.MoveStrategy;
import com.chess.pgn.FenUtilities;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestBestMoveFinder {
    @Test
    public void FoolsMate(){
        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("f2"),
                        BoardUtils.getCoordinateAtPosition("f3")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(MoveFactory.createMove(t1.getToBoard(), BoardUtils.getCoordinateAtPosition("e7"),
                        BoardUtils.getCoordinateAtPosition("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(MoveFactory.createMove(t2.getToBoard(), BoardUtils.getCoordinateAtPosition("g2"),
                        BoardUtils.getCoordinateAtPosition("g4")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveStrategy strategy = new AlphaBetaWithMoveSorting(1, false);
        final Board newBoard = t3.getToBoard();
        final Move aiMove = strategy.execute(newBoard);
        final Move bestMove = Move.MoveFactory.createMove(
                newBoard,
                BoardUtils.getCoordinateAtPosition("d8"),
                BoardUtils.getCoordinateAtPosition("h4"));
        assertEquals(aiMove, bestMove);
        final MoveTransition t4 = newBoard.currentPlayer().makeMove(aiMove);
        assertTrue(t4.getMoveStatus().isDone());
        final Board checkMateBoard = t4.getToBoard();
        assertTrue(checkMateBoard.currentPlayer().isInCheckMate());
        assertFalse(checkMateBoard.currentPlayer().isInStaleMate());
    }

    @Test
    public void puzzle1(){
        final Board board = FenUtilities.createGameFromFEN("2b2rk1/p1q4p/1p3p2/2p2Np1/6QP/8/PP3PP1/4R1K1 w - - 0 1");  // re7 is best move
        final MoveStrategy strategy = new AlphaBetaWithMoveSorting(20, false);
        final Move aiMove = strategy.execute(board);
        assertEquals(aiMove, MoveFactory.createMove(board, 60, 12));
    }

    @Test
    public void puzzle2(){
        Board board;
        MoveStrategy strategy;
        Move aiMove;
        board = FenUtilities.createGameFromFEN("3rrk2/5p2/p5q1/1pp3nQ/4P3/1PB3NR/P5PP/6K1 w - - 0 1");
        // Qh8, ke7, Qe5, kd7, Qd5, kc7, Ba5, kb8, Bxd8, nxh3, gxh3
        strategy = new AlphaBetaWithMoveSorting(20, false);
        aiMove = strategy.execute(board);
        assertEquals(aiMove, MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("h5"), BoardUtils.getCoordinateAtPosition("h8")));
        final MoveTransition t1 = board.currentPlayer().makeMove(aiMove);
        assertTrue(t1.getMoveStatus().isDone());
        final MoveTransition t2 = t1.getToBoard().currentPlayer().makeMove(MoveFactory.createMove(t1.getToBoard(), BoardUtils.getCoordinateAtPosition("f8"), BoardUtils.getCoordinateAtPosition("e7")));
        assertTrue(t2.getMoveStatus().isDone());

        board = t2.getToBoard();
        strategy = new AlphaBetaWithMoveSorting(20, false);
        aiMove = strategy.execute(board);
        assertEquals(aiMove, MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("h8"), BoardUtils.getCoordinateAtPosition("e5")));
        final MoveTransition t3 = board.currentPlayer().makeMove(aiMove);
        assertTrue(t3.getMoveStatus().isDone());
        final MoveTransition t4 = t3.getToBoard().currentPlayer().makeMove(MoveFactory.createMove(t3.getToBoard(), BoardUtils.getCoordinateAtPosition("e7"), BoardUtils.getCoordinateAtPosition("d7")));
        assertTrue(t4.getMoveStatus().isDone());

        board = t4.getToBoard();
        strategy = new AlphaBetaWithMoveSorting(20, false);
        aiMove = strategy.execute(board);
        assertEquals(aiMove, MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("e5"), BoardUtils.getCoordinateAtPosition("d5")));
        final MoveTransition t5 = board.currentPlayer().makeMove(aiMove);
        assertTrue(t5.getMoveStatus().isDone());
        final MoveTransition t6 = t5.getToBoard().currentPlayer().makeMove(MoveFactory.createMove(t5.getToBoard(), BoardUtils.getCoordinateAtPosition("d7"), BoardUtils.getCoordinateAtPosition("c7")));
        assertTrue(t6.getMoveStatus().isDone());

        board = t6.getToBoard();
        strategy = new AlphaBetaWithMoveSorting(20, false);
        aiMove = strategy.execute(board);
        assertEquals(aiMove, MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("c3"), BoardUtils.getCoordinateAtPosition("a5")));
        final MoveTransition t7 = board.currentPlayer().makeMove(aiMove);
        assertTrue(t7.getMoveStatus().isDone());
        final MoveTransition t8 = t7.getToBoard().currentPlayer().makeMove(MoveFactory.createMove(t7.getToBoard(), BoardUtils.getCoordinateAtPosition("c7"), BoardUtils.getCoordinateAtPosition("b8")));
        assertTrue(t8.getMoveStatus().isDone());

        board = t8.getToBoard();
        strategy = new AlphaBetaWithMoveSorting(20, false);
        aiMove = strategy.execute(board);
        assertEquals(aiMove, MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("a5"), BoardUtils.getCoordinateAtPosition("d8")));
        final MoveTransition t9 = board.currentPlayer().makeMove(aiMove);
        assertTrue(t9.getMoveStatus().isDone());
        final MoveTransition t10 = t9.getToBoard().currentPlayer().makeMove(MoveFactory.createMove(t9.getToBoard(), BoardUtils.getCoordinateAtPosition("g5"), BoardUtils.getCoordinateAtPosition("h3")));
        assertTrue(t10.getMoveStatus().isDone());

        board = t10.getToBoard();
        strategy = new AlphaBetaWithMoveSorting(20, false);
        aiMove = strategy.execute(board);
        assertEquals(aiMove, MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("g2"), BoardUtils.getCoordinateAtPosition("h3")));
        final MoveTransition t11 = board.currentPlayer().makeMove(aiMove);
        assertTrue(t11.getMoveStatus().isDone());
    }

    @Test
    public void EndgamePuzzle1(){
        final Board board = FenUtilities.createGameFromFEN("3k4/8/1K6/5B2/4R3/4p3/4p3/8 w - - 0 1");
        final MoveStrategy strategy = new AlphaBetaWithMoveSorting(12, false);
        final Move aiMove = strategy.execute(board);
        assertEquals(MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("f5"), BoardUtils.getCoordinateAtPosition("d7")), aiMove);
    }

    @Test
    public void kiwiPeteDepth1() {
        final Board board = FenUtilities.createGameFromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        final MoveStrategy minMax = new MiniMax(1);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 48L);
    }

    @Test
    public void kiwiPeteDepth2() {
        final Board board = FenUtilities.createGameFromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        final MoveStrategy minMax = new MiniMax(2);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 2039L);
    }

    @Test
    public void kiwiPeteDepth3() {
        final Board board = FenUtilities.createGameFromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        final MoveStrategy minMax = new MiniMax(3);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 97862L);
    }

    @Test
    public void kiwiPeteDepth4() {
        final Board board = FenUtilities.createGameFromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        final MoveStrategy minMax = new MiniMax(4);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 4085603L);
    }

    @Test
    public void testPosition3Depth1() {
        final Board board = FenUtilities.createGameFromFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        final MoveStrategy minMax = new MiniMax(1);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 14L);
    }

    @Test
    public void testPosition3Depth2() {
        final Board board = FenUtilities.createGameFromFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        final MoveStrategy minMax = new MiniMax(2);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 191L);
    }

    @Test
    public void testPosition3Depth3() {
        final Board board = FenUtilities.createGameFromFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        final MoveStrategy minMax = new MiniMax(3);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 2812L);
    }

    @Test
    public void testPosition3Depth4() {
        final Board board = FenUtilities.createGameFromFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        final MoveStrategy minMax = new MiniMax(4);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 43238L);
    }

    @Test
    public void testPosition4Depth1() {
        final Board board = FenUtilities.createGameFromFEN("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        final MoveStrategy minMax = new MiniMax(1);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 6L);
    }

    @Test
    public void testPosition4Depth2() {
        final Board board = FenUtilities.createGameFromFEN("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        final MoveStrategy minMax = new MiniMax(2);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 264L);
    }

    @Test
    public void testPosition4Depth3() {
        final Board board = FenUtilities.createGameFromFEN("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        final MoveStrategy minMax = new MiniMax(3);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 9467L);
    }

    @Test
    public void testPosition4Depth4() {
        final Board board = FenUtilities.createGameFromFEN("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        final MoveStrategy minMax = new MiniMax(4);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 422333L);
    }

    @Test
    public void testPosition5Depth1() {
        final Board board = FenUtilities.createGameFromFEN("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        final MoveStrategy minMax = new MiniMax(1);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 44L);
    }

    @Test
    public void testPosition5Depth2() {
        final Board board = FenUtilities.createGameFromFEN("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        final MoveStrategy minMax = new MiniMax(2);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 1486L);
    }

    @Test
    public void testPosition5Depth3() {
        final Board board = FenUtilities.createGameFromFEN("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        final MoveStrategy minMax = new MiniMax(3);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 62379L);
    }

    @Test
    public void testPosition5Depth4() {
        final Board board = FenUtilities.createGameFromFEN("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        final MoveStrategy minMax = new MiniMax(4);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 2103487L);
    }

    @Test
    public void testPosition6Depth4() {
        final Board board = FenUtilities.createGameFromFEN("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10\n");
        final MoveStrategy minMax = new MiniMax(4);
        minMax.execute(board);
        assertEquals(minMax.getNumBoardsEvaluated(), 3894594L);
    }
}
