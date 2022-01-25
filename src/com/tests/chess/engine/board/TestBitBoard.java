package com.tests.chess.engine.board;

import com.chess.engine.bitboard.board.*;
import org.junit.Test;

public class TestBitBoard {
    @Test
    public void TestBitBoard(){
        Zobrist.zobristFillArray();
        BitBoard.importFEN("rnbqkbnr/pp2pppp/3p4/2p5/4P3/5P2/PPPP2PP/RNBQKBNR w KQkq - 0 1");
        UCI.inputPrint();
    }


}
