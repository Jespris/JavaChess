package com.chess.engine.board;

public class BoardUtils {

    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHT_COLUMN = initColumn(7);

    public static final boolean[] SECOND_ROW = initRow(1);
    public static final boolean[] SEVENTH_ROW = initRow(6);

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

    private BoardUtils(){
        throw new RuntimeException("You cannot instantiate me!");
    }

    public static boolean isValidTileCoordinate(final int index){
        return 0 <= index && index < NUM_TILES;
    }
}
