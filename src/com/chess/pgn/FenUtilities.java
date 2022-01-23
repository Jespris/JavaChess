package com.chess.pgn;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Pawn;

public class FenUtilities {
    private FenUtilities(){
        throw new RuntimeException("Not instantiable!");
    }

    public static Board createGameFromFEN(final String fenString){
        return null;
    }

    public static String createFENFromGame(final Board board){
        return calculateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculateEnPassantSquare(board) + " " +
                "0 1";
    }

    private static String calculateBoardText(final Board board) {
        final StringBuilder builder = new StringBuilder();
        int emptyTiles = 0;
        for (int i = 0; i < BoardUtils.NUM_TILES; i++){
            if (i % 8 == 0 && i != 0){
                if (emptyTiles > 0){
                    builder.append(emptyTiles);
                    emptyTiles = 0;
                }
                builder.append("/");
            }
            if (board.getPiece(i) != null){
                if (emptyTiles > 0){
                    builder.append(emptyTiles);
                }
                builder.append(board.getPiece(i).getPieceAlliance() == Alliance.WHITE ?
                        board.getPiece(i).toString().toUpperCase() :
                        board.getPiece(i).toString().toLowerCase());
                emptyTiles = 0;
            } else {
                emptyTiles += 1;
            }
        }
        return builder.toString();
    }

    private static String calculateEnPassantSquare(final Board board) {
        final Pawn enPassantPawn = board.getEnPassantPawn();
        if (enPassantPawn != null){
            return BoardUtils.getChessNotationAtCoordinate(enPassantPawn.getPiecePosition() + 8 * enPassantPawn.getPieceAlliance().getOppositeDirection());
        }
        return "-";
    }

    private static String calculateCastleText(final Board board) {
        final StringBuilder builder = new StringBuilder();

        if (board.whitePlayer().isKingSideCastleCapable()){
            builder.append("K");
        }
        if (board.whitePlayer().isQueenSideCastleCapable()){
            builder.append("Q");
        }
        if (board.blackPlayer().isKingSideCastleCapable()){
            builder.append("k");
        }
        if (board.blackPlayer().isQueenSideCastleCapable()){
            builder.append("q");
        }
        final String result = builder.toString();
        return result.isEmpty() ? "-" : result;
    }

    private static String calculateCurrentPlayerText(final Board board) {
        return board.currentPlayer().toString().substring(0, 1).toLowerCase();
    }
}
