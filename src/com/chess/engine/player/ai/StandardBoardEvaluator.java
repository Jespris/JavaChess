package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator{
    private static final int PIECE_MOBILITY_FACTOR = 2;
    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 20;

    private static final StandardBoardEvaluator INSTANCE = new StandardBoardEvaluator();

    private StandardBoardEvaluator(){
    }

    public static StandardBoardEvaluator get(){
        return INSTANCE;
    }

    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board, board.blackPlayer(), depth);
    }

    private int scorePlayer(final Board board, final Player player, final int depth) {
        return materialValue(player) +
                piecePlacement(player) +
                pieceMobility(player) +
                check(player) +
                checkMate(player, depth) +
                castled(player);
    }

    private int castled(Player player) {
        return player.hasCastled() ? CASTLE_BONUS : 0;
    }

    private int checkMate(final Player player, final int depth) {
        return player.getOpponent().isInCheckMate() ? CHECKMATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int pieceMobility(final Player player) {
        // how many legal moves
        return player.getLegalMoves().size() * PIECE_MOBILITY_FACTOR;
    }

    private static int piecePlacement(final Player player) {
        int piecePlacementScore = 0;
        for (final Piece piece : player.getActivePieces()){
            piecePlacementScore += piece.locationBonus();
        }
        return piecePlacementScore;
    }


    private static int materialValue(final Player player){
        int materialScore = 0;
        for (final Piece piece : player.getActivePieces()){
            materialScore += piece.getPieceValue();
        }
        return materialScore;
    }
}
