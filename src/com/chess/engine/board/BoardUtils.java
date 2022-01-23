package com.chess.engine.board;

import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public enum BoardUtils {

    INSTANCE;

    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHT_COLUMN = initColumn(7);

    public static final boolean[] FIRST_ROW = initRow(0);
    public static final boolean[] SECOND_ROW = initRow(1);
    public static final boolean[] SEVENTH_ROW = initRow(6);
    public static final boolean[] EIGHT_ROW = initRow(7);

    public static final String[] ALGEBRAIC_NOTATION = initAlgebraicNotation();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initPositionToCoordinateMap();

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    private static boolean[] initColumn(int columnIndex){
        final boolean[] board = new boolean[NUM_TILES];
        do {
            board[columnIndex] = true;
            columnIndex += NUM_TILES_PER_ROW;
        } while (columnIndex < NUM_TILES);

        return board;
    }

    private static boolean[] initRow(int rowIndex){
        final boolean[] board = new boolean[NUM_TILES];
        for (int i=rowIndex * 8; i< rowIndex * 8 + 8; i++){
            board[i] = true;
        }
        return board;
    }

    public static boolean isValidTileCoordinate(final int index){
        return 0 <= index && index < NUM_TILES;
    }

    public static int getCoordinateAtPosition(final String chessCoordinate) {
        return POSITION_TO_COORDINATE.get(chessCoordinate);
    }

    public static String getChessNotationAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION[coordinate];
    }

    private static Map<String, Integer> initPositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();

        for (int i = 0; i < NUM_TILES; i++){
            positionToCoordinate.put(ALGEBRAIC_NOTATION[i], i);
        }

        return ImmutableMap.copyOf(positionToCoordinate);
    }

    private static String[] initAlgebraicNotation() {
        return new String[] {
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        };
    }

    public static boolean isKingPawnTrap(final Board board,
                                         final King king,
                                         final int frontTile) {
        final Piece piece = board.getPiece(frontTile);
        return piece != null &&
                piece.getPieceType() == Piece.PieceType.PAWN &&
                piece.getPieceAlliance() != king.getPieceAlliance();
    }
}
