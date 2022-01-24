package com.chess.engine.player.ai;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MoveFactory;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.Player;
import com.chess.pgn.FenUtilities;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import java.util.*;

public class AlphaBetaWithMoveSorting extends Observable implements MoveStrategy {
    private final BoardEvaluator evaluator;
    private final long minSearchTime; // in seconds
    private final MoveSorter moveSorter;
    private final MoveSorter captureSorter;
    private long totalBoardsEvaluated;
    private long boardsEvaluated;
    private long executionTime;
    private long captureChainEvaluationTime;
    private int cutOffsProduced;
    private int transpositionsSkipped;
    private final Map<String, Integer> transpositionTable;
    private final boolean doCaptureChains;
    private int captureTranspositionsSkipped;
    private final Map<String, Integer> captureTranspositionTable;

    private enum MoveSorter {

        SORT {
            @Override
            Collection<Move> sort(final Collection<Move> moves, final Move bestMove) {
                return getSortedMoves(moves, bestMove);
            }
        },
        CAPTURE_SORT{
            @Override
            Collection<Move> sort(Collection<Move> moves, final Move bestMove) {
                return getCaptureMoves(moves);
            }

            private Collection<Move> getCaptureMoves(final Collection<Move> moves) {
                final List<Move> captureMoves= new ArrayList<>();
                for (final Move move : moves){
                    if (move.isAttackMove()){
                        captureMoves.add(move);
                    }
                }
                return ImmutableList.copyOf(captureMoves);
            }
        };

        private static Collection<Move> getSortedMoves(final Collection<Move> moves, final Move bestMove){
            final List<Move> sortedMoves = new ArrayList<>();
            if (bestMove != null){
               sortedMoves.add(bestMove);
            }
            for (final Move move : moves){
                if (move != bestMove){
                    if (move.isAttackMove() || move.isCastlingMove()){
                        sortedMoves.add(move);
                    }
                }
            }
            for (final Move move : moves){
                if (!sortedMoves.contains(move)){
                    sortedMoves.add(move);
                }
            }

            return ImmutableList.copyOf(sortedMoves);
        }

        abstract Collection<Move> sort(Collection<Move> moves, Move bestMove);
    }

    public AlphaBetaWithMoveSorting(final int minSearchTime, final boolean doCaptureChains) {
        this.evaluator = StandardBoardEvaluator.get();
        this.minSearchTime = minSearchTime;
        this.doCaptureChains = doCaptureChains;
        this.moveSorter = MoveSorter.SORT;
        this.captureSorter = MoveSorter.CAPTURE_SORT;
        this.totalBoardsEvaluated = 0;
        this.boardsEvaluated = 0;
        this.cutOffsProduced = 0;
        this.transpositionsSkipped = 0;
        this.transpositionTable = new HashMap<>();
        this.captureTranspositionsSkipped = 0;
        this.captureTranspositionTable = new HashMap<>();
        this.captureChainEvaluationTime = 0;
    }

    @Override
    public String toString() {
        return "AB+MO";
    }

    @Override
    public long getNumBoardsEvaluated() {
        return this.totalBoardsEvaluated;
    }

    @Override
    public Move execute(final Board board) {
        List<Move> sortedMoves = (List<Move>) this.moveSorter.sort(board.currentPlayer().getLegalMoves(), null);
        final long startTime = System.currentTimeMillis();
        Move bestMove = getTheOnlyLegalMove(board, sortedMoves);
        if (bestMove != null){
            System.out.println(bestMove + "is the only legal move, play it!");
            return bestMove;
        }
        int depth = 0;
        do {
            this.captureChainEvaluationTime = 0;
            depth++;
            System.out.println("INCREASING DEPTH TO " + depth);
            sortedMoves = (List<Move>) this.moveSorter.sort(board.currentPlayer().getLegalMoves(), bestMove);
            bestMove = findBestMoveAlphaBetaMinMax(board, sortedMoves, depth);
            this.executionTime = System.currentTimeMillis() - startTime;
            System.out.println("Current time spent on move: " + executionTime + " ms");
            if (this.doCaptureChains) {
                System.out.println("Time spent on capture chain evaluation (this depth): " + this.captureChainEvaluationTime);
            }
        } while (this.executionTime + ((long) Math.pow(depth, 3) * sortedMoves.size()) < (this.minSearchTime * 1000));
        // estimate how long next search is going to take, e.g. depth 7 => 7^3 * 33 = 11319 ms
        return bestMove;
    }

    private Move findBestMoveAlphaBetaMinMax(final Board board, final List<Move> sortedMoves, final int depth){
        Move bestMove = MoveFactory.getNullMove();
        this.transpositionTable.clear();
        this.transpositionsSkipped = 0;
        final Player currentPlayer = board.currentPlayer();
        final Alliance alliance = currentPlayer.getAlliance();
        this.boardsEvaluated = 0;
        this.cutOffsProduced = 0;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        int moveCounter = 1;
        final int numMoves = sortedMoves.size();
        System.out.println(board.currentPlayer() + " THINKING with depth = " + depth);
        System.out.println("Previous best move found: " + sortedMoves.get(0).toString());
        // System.out.println("\tOrdered moves! : " + this.moveSorter.sort(board.currentPlayer().getLegalMoves()));
        for (final Move move : sortedMoves) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            final String s;
            if (moveTransition.getMoveStatus().isDone()) {
                final long candidateMoveStartTime = System.nanoTime();
                currentValue = alliance.isWhite() ?
                        min(moveTransition.getToBoard(), depth - 1, highestSeenValue, lowestSeenValue) :
                        max(moveTransition.getToBoard(), depth - 1, highestSeenValue, lowestSeenValue);
                if (alliance.isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                }
                else if (alliance.isBlack() && currentValue < lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
                final String moveEvaluation = " [evaluation: " + (highestSeenValue == Integer.MIN_VALUE ? -lowestSeenValue : highestSeenValue) + "]";
                s = "\t" + toString() + "(" +depth+ "), m: (" + moveCounter + "/" + numMoves + ") " +
                        move + ", best:  " + bestMove + moveEvaluation + ", t: " +
                        calculateTimeTaken(candidateMoveStartTime, System.nanoTime());
            } else {
                s = "\t" + toString() + ", m: (" + moveCounter + "/" + numMoves + ") " + move + " is illegal, best: " + bestMove;
            }
            System.out.println(s);
            setChanged();
            notifyObservers(s);
            moveCounter++;
        }
        System.out.printf("%s's BEST MOVE %s [#boards evaluated = %d, time taken = %d ms, eval rate = %.1f cutoffCount = %d transpositions percent = %.2f prune percent = %.2f]\n",
                board.currentPlayer(),
                bestMove, this.boardsEvaluated, this.executionTime,
                (1000 * ((double)this.boardsEvaluated/this.executionTime)),
                this.cutOffsProduced, 100 * ((double)this.boardsEvaluated/this.transpositionsSkipped), 100 * ((double)this.cutOffsProduced/this.boardsEvaluated));
        return bestMove;
    }

    private Move getTheOnlyLegalMove(final Board board, final List<Move> sortedMoves) {
        Move onlyMove = null;
        for (final Move move : sortedMoves){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                if (onlyMove == null) {
                    onlyMove = move;
                } else {
                    return null;
                }
            }
        }
        return onlyMove;
    }

    public int max(final Board board,
                   final int depth,
                   final int highest,
                   final int lowest) {
        if (depth == 0 || BoardUtils.INSTANCE.isEndGameScenario(board)) {
            final String FEN = FenUtilities.createFENFromGame(board);
            this.boardsEvaluated++;
            this.totalBoardsEvaluated++;
            if (this.transpositionTable.containsKey(FEN)){
                // System.out.println("Found a transposition!");
                this.transpositionsSkipped++;
                return this.transpositionTable.get(FEN);
            } else {
                final int evaluation;
                if (!BoardUtils.INSTANCE.isEndGameScenario(board) && this.doCaptureChains) {
                    // start a new search from here that only looks at captures
                    evaluation = resolveCaptureChains(board, board.currentPlayer().getLegalMoves(), depth);
                } else {
                    evaluation = this.evaluator.evaluate(board, depth);
                }
                this.transpositionTable.put(FEN, evaluation);
                return evaluation;
            }
        }
        int currentHighest = highest;
        for (final Move move : this.moveSorter.sort(board.currentPlayer().getLegalMoves(), null)) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentHighest = Math.max(currentHighest, min(moveTransition.getToBoard(),
                        calculateQuiescenceDepth(board, move, depth), currentHighest, lowest));
                if (lowest <= currentHighest) {
                    this.cutOffsProduced++;
                    break;
                }
            }
        }
        return currentHighest;
    }

    public int min(final Board board,
                   final int depth,
                   final int highest,
                   final int lowest) {
        if (depth == 0 || BoardUtils.INSTANCE.isEndGameScenario(board)) {
            final String FEN = FenUtilities.createFENFromGame(board);
            this.totalBoardsEvaluated++;
            this.boardsEvaluated++;
            if (this.transpositionTable.containsKey(FEN)){
                // System.out.println("Found a transposition!");
                this.transpositionsSkipped++;
                return this.transpositionTable.get(FEN);
            } else {
                final int evaluation;
                if (!BoardUtils.INSTANCE.isEndGameScenario(board) && this.doCaptureChains) {
                    // start a new search from here that only looks at captures
                    evaluation = resolveCaptureChains(board, board.currentPlayer().getLegalMoves(), depth);
                } else {
                    evaluation = this.evaluator.evaluate(board, depth);
                }
                this.transpositionTable.put(FEN, evaluation);
                return evaluation;
            }
        }
        int currentLowest = lowest;
        for (final Move move : this.moveSorter.sort(board.currentPlayer().getLegalMoves(), null)) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentLowest = Math.min(currentLowest, max(moveTransition.getToBoard(),
                        calculateQuiescenceDepth(board, move, depth), highest, currentLowest));
                if (currentLowest <= highest) {
                    this.cutOffsProduced++;
                    break;
                }
            }
        }
        return currentLowest;
    }

    private int resolveCaptureChains(final Board board, final Collection<Move> legalMoves, final int depth) {
        // check if there are captures in the legal moves, if so start a new search only looking at captures
        for (final Move move : legalMoves){
            if (move.isAttackMove()){
                // start new search here
                // System.out.println("There are captures in the position, starting new capture chain best move search...");
                return executeOnlyCaptures(board);
            }
        }
        /*
        System.out.println("==================================");
        System.out.println("No captures found in the position!");
        System.out.println("==================================");
        */
        return this.evaluator.evaluate(board, depth);
    }

    private int findBestMoveAlphaBetaMinMaxOnlyCaptures(final Board board, final Collection<Move> moves, final int maxDepth) {
        final List<Move> captureMoves = (List<Move>) this.captureSorter.sort(moves, null);
        final Player currentPlayer = board.currentPlayer();
        final Alliance alliance = currentPlayer.getAlliance();
        int bestValue = 0;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        int moveCounter = 1;
        this.captureTranspositionsSkipped = 0;
        this.captureTranspositionTable.clear();
        final int numMoves = captureMoves.size();
        for (final Move move : captureMoves){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = alliance.isWhite() ?
                        minCaptures(moveTransition.getToBoard(), maxDepth - 1, highestSeenValue, lowestSeenValue) :
                        maxCaptures(moveTransition.getToBoard(), maxDepth - 1, highestSeenValue, lowestSeenValue);
                if (alliance.isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestValue = currentValue;
                }
                else if (alliance.isBlack() && currentValue < lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestValue = currentValue;
                }
                // System.out.println("m: (" + moveCounter + "/" + numMoves + ") " + move + ", best evaluation:  " + bestValue);
            }

            moveCounter++;
        }
        return bestValue;
    }

    private int executeOnlyCaptures(final Board board){
        int bestEvaluation;
        int maxDepth = 10;  // capture chains longer than this are very complicated and very rare
        final long startTime = System.currentTimeMillis();
        List<Move> sortedMoves = (List<Move>) this.captureSorter.sort(board.currentPlayer().getLegalMoves(), null);
        bestEvaluation = findBestMoveAlphaBetaMinMaxOnlyCaptures(board, sortedMoves, maxDepth);
        this.captureChainEvaluationTime += System.currentTimeMillis() - startTime;
        // System.out.println(this.captureTranspositionsSkipped + " transpositions skipped in capture chain");
        return bestEvaluation;
    }

    public int maxCaptures(final Board board,
                            final int depth,
                            final int highest,
                            final int lowest) {
        final List<Move> captureMoves = (List<Move>) this.captureSorter.sort(board.currentPlayer().getLegalMoves(), null);
        if (captureMoves.isEmpty() || BoardUtils.INSTANCE.isEndGameScenario(board) || depth == 0) {
            final String FEN = FenUtilities.createFENFromGame(board);
            if (this.captureTranspositionTable.containsKey(FEN)){
                this.captureTranspositionsSkipped++;
                return this.captureTranspositionTable.get(FEN);
            } else {
                final int evaluation = this.evaluator.evaluate(board, depth);
                this.captureTranspositionTable.put(FEN, evaluation);
                return evaluation;
            }
        }
        int currentHighest = highest;
        for (final Move move : captureMoves) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentHighest = Math.max(currentHighest, minCaptures(moveTransition.getToBoard(), depth - 1, currentHighest, lowest));
                if (lowest <= currentHighest) {
                    break;
                }
            }
        }
        return currentHighest;
    }

    public int minCaptures(final Board board,
                            final int depth,
                            final int highest,
                            final int lowest) {
        final List<Move> captureMoves = (List<Move>) this.captureSorter.sort(board.currentPlayer().getLegalMoves(), null);
        if (captureMoves.isEmpty() || BoardUtils.INSTANCE.isEndGameScenario(board) || depth == 0) {
            final String FEN = FenUtilities.createFENFromGame(board);
            if (this.captureTranspositionTable.containsKey(FEN)){
                this.captureTranspositionsSkipped++;
                return this.captureTranspositionTable.get(FEN);
            } else {
                final int evaluation = this.evaluator.evaluate(board, depth);
                this.captureTranspositionTable.put(FEN, evaluation);
                return evaluation;
            }
        }
        int currentLowest = lowest;
        for (final Move move : captureMoves) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentLowest = Math.min(currentLowest, maxCaptures(moveTransition.getToBoard(), depth, highest, currentLowest));
                if (currentLowest <= highest) {
                    break;
                }
            }
        }
        return currentLowest;
    }

    private int calculateQuiescenceDepth(final Board board,
                                         final Move move,
                                         final int depth) {
        return depth - 1;
    }

    private static String calculateTimeTaken(final long start, final long end) {
        final long timeTaken = (end - start) / 1000000;
        return timeTaken + " ms";
    }
}
