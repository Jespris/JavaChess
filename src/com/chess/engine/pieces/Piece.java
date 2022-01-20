package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;
import java.util.List;

public abstract class Piece {
    protected final int piecePosition;
    protected final Alliance pieceAlliance;

    Piece(final int piecePosition, final Alliance pieceAlliance){
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
    }

    // Unspecified return collection (e.g. can be list)
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public Alliance getPieceAlliance(){
        return this.pieceAlliance;
    }
}
