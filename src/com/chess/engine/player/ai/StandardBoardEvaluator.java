package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator{
    @Override
    public int evaluateBoard(Board board) {
        return scorePlayer(board, board.whitePlayer()) - scorePlayer(board, board.blackPlayer());
    }

    private int scorePlayer(final Board board, final Player player) {
        return materialValue(player);
    }

    private static int materialValue(final Player player){
        int materialScore = 0;
        for (final Piece piece : player.getActivePieces()){
            materialScore += piece.getPieceValue();
        }
        return materialScore;
    }
}
