package com.chess;

import com.chess.engine.board.Board;
import com.chess.gui.Table;
import com.chess.pgn.FenUtilities;

public class JavaChess {
    public static void main(String[] args){
        Board board = Board.createStandardBoard();

        System.out.println(board);
        System.out.println(FenUtilities.createFENFromGame(board));

        Table.get().show();
    }
}
