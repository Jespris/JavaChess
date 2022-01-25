package com.chess.pgn;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.Board.Builder;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.pieces.*;

import java.util.Objects;

public class FenUtilities {
    private FenUtilities(){
        throw new RuntimeException("Not instantiable!");
    }

    public static Board createGameFromFEN(final String fenString){
        return parseFEN(fenString);
    }

    private static Board parseFEN(final String fenString) {
        final String[] fenPartitions = fenString.trim().split(" ");
        final Builder builder = new Builder();
        final boolean whiteKingSideCastle = whiteKingSideCastle(fenPartitions[2]);
        final boolean whiteQueenSideCastle = whiteQueenSideCastle(fenPartitions[2]);
        final boolean blackKingSideCastle = blackKingSideCastle(fenPartitions[2]);
        final boolean blackQueenSideCastle = blackQueenSideCastle(fenPartitions[2]);
        final String gameConfiguration = fenPartitions[0];
        final char[] boardTiles = gameConfiguration.replaceAll("/", "")
                .replaceAll("8", "--------")
                .replaceAll("7", "-------")
                .replaceAll("6", "------")
                .replaceAll("5", "-----")
                .replaceAll("4", "----")
                .replaceAll("3", "---")
                .replaceAll("2", "--")
                .replaceAll("1", "-")
                .toCharArray();
        int i = 0;
        while (i < boardTiles.length) {
            switch (boardTiles[i]) {
                case 'r':
                    builder.setPiece(new Rook(Alliance.BLACK, i));
                    i++;
                    break;
                case 'n':
                    builder.setPiece(new Knight(Alliance.BLACK, i));
                    i++;
                    break;
                case 'b':
                    builder.setPiece(new Bishop(Alliance.BLACK, i));
                    i++;
                    break;
                case 'q':
                    builder.setPiece(new Queen(Alliance.BLACK, i));
                    i++;
                    break;
                case 'k':
                    final boolean isCastled = !blackKingSideCastle && !blackQueenSideCastle;
                    builder.setPiece(new King(Alliance.BLACK, i, blackKingSideCastle, blackQueenSideCastle));
                    i++;
                    break;
                case 'p':
                    builder.setPiece(new Pawn(Alliance.BLACK, i));
                    i++;
                    break;
                case 'R':
                    builder.setPiece(new Rook(Alliance.WHITE, i));
                    i++;
                    break;
                case 'N':
                    builder.setPiece(new Knight(Alliance.WHITE, i));
                    i++;
                    break;
                case 'B':
                    builder.setPiece(new Bishop(Alliance.WHITE, i));
                    i++;
                    break;
                case 'Q':
                    builder.setPiece(new Queen(Alliance.WHITE, i));
                    i++;
                    break;
                case 'K':
                    builder.setPiece(new King(Alliance.WHITE, i, whiteKingSideCastle, whiteQueenSideCastle));
                    i++;
                    break;
                case 'P':
                    builder.setPiece(new Pawn(Alliance.WHITE, i));
                    i++;
                    break;
                case '-':
                    i++;
                    break;
                default:
                    throw new RuntimeException("Invalid FEN String " + gameConfiguration);
            }
        }
        final Alliance allianceToMove = allianceToMove(fenPartitions[1]);
        final Pawn enPassantPawn = getEnPassantFromFEN(fenPartitions[3], allianceToMove);
        builder.setMoveMaker(allianceToMove);
        builder.setEnPassantPawn(enPassantPawn);
        return builder.build();
    }

    private static Pawn getEnPassantFromFEN(final String fenPartition, final Alliance alliance) {
        if (!Objects.equals(fenPartition, "-")){
            final int coordinate = BoardUtils.getCoordinateAtPosition(fenPartition) + 8 * alliance.getDirection();
            return new Pawn(alliance.oppositeAlliance(), coordinate);
        }
        return null;
    }

    private static Alliance allianceToMove(final String moveMakerString) {
        if(moveMakerString.equals("w")) {
            return Alliance.WHITE;
        } else if(moveMakerString.equals("b")) {
            return Alliance.BLACK;
        }
        throw new RuntimeException("Invalid FEN String " + moveMakerString);
    }

    private static boolean blackQueenSideCastle(final String fenPartition) {
        return fenPartition.contains("q");
    }

    private static boolean blackKingSideCastle(final String fenPartition) {
        return fenPartition.contains("k");
    }

    private static boolean whiteQueenSideCastle(final String fenPartition) {
        return fenPartition.contains("Q");
    }

    private static boolean whiteKingSideCastle(final String fenPartition) {
        return fenPartition.contains("K");
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
