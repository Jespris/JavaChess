package com.chess.engine.classic.player.ai;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.pieces.King;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.player.Player;
import com.google.common.collect.Iterables;

public final class StandardBoardEvaluator implements BoardEvaluator{
    private static final int PIECE_MOBILITY_FACTOR = 2;
    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 50;
    private static final int CASTLE_BONUS = 20;
    private static final int KING_SAFETY_FACTOR = 5;
    private static final int SPACE_CONTROL_FACTOR = 5;

    private static final StandardBoardEvaluator INSTANCE = new StandardBoardEvaluator();

    private StandardBoardEvaluator(){
    }

    public static StandardBoardEvaluator get(){
        return INSTANCE;
    }

    @Override
    public int evaluate(final Board board, final int depth) {
        if (board.currentPlayer().isInStaleMate()){
            return 0;
        } else if (board.currentPlayer().isInCheckMate()){
            if (board.currentPlayer().getAlliance() == Alliance.WHITE){
                return -10000;
            } else {
                return 10000;
            }
        } else {
            return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board, board.blackPlayer(), depth);
        }
    }

    private int scorePlayer(final Board board, final Player player, final int depth) {
        // long startTime = System.currentTimeMillis();
        final int score = materialValue(player) +
                piecePlacement(player) +
                pieceMobility(player) +
                check(player) +
                checkMate(player, depth) +
                castled(player) +
                kingSafety(player) +
                space(player);
        // System.out.println("It took " + (System.currentTimeMillis() - startTime) + " ms to evaluate board");
        return score;
    }

    private int space(final Player player) {
        if (!isEndGamePhase(player)){
            return calculateSpaceControlled(player);
        }
        return 0;
    }

    private int calculateSpaceControlled(final Player player) {
        double spaceControlledWeighted = 0;
        if (player.getAlliance().isWhite()){
            for (int i = 0; i < BoardUtils.NUM_TILES / 2; i++){
                spaceControlledWeighted += Player.calculateAttacksOnTile(i, player.getLegalMoves()).size() * Math.floorDiv(i, 8);
            }
        }
        return (int) (spaceControlledWeighted * SPACE_CONTROL_FACTOR);
    }

    private boolean isEndGamePhase(final Player player) {
        int totalPieceValue = 0;
        for (final Piece piece : Iterables.concat(player.getActivePieces(), player.getOpponent().getActivePieces())){
            if (piece.getPieceType() != Piece.PieceType.KING) {
                totalPieceValue += piece.getPieceValue();
            }
        }
        return totalPieceValue <= 20;
    }

    private int kingSafety(Player player) {
        int kingAttacks = 0;
        // checks how many pieces attacks each of the squares around the king
        final int[] directions = {-9, -8, -7, -1, 1, 7, 8, 9};
        final King king = player.getPlayerKing();
        for (final int direction : directions){
            if (player.getPlayerKing().isFirstColumnExclusion(king.getPiecePosition(), direction) ||
                    player.getPlayerKing().isEightColumnExclusion(king.getPiecePosition(), direction)){
                continue;
            }
            final int destination = king.getPiecePosition() + direction;
            if (BoardUtils.isValidTileCoordinate(destination)){
                kingAttacks += Player.calculateAttacksOnTile(destination, player.getOpponent().getLegalMoves()).size();
            }
        }
        return -(kingAttacks * KING_SAFETY_FACTOR);
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
