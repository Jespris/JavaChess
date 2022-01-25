package com.chess.engine.classic.pieces;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.Move;

import java.util.Collection;

public abstract class Piece {

    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private final int cashedHashCode;

    Piece(final PieceType pieceType,
          final Alliance pieceAlliance,
          final int piecePosition,
          final boolean isFirstMove){
        this.pieceType = pieceType;
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = isFirstMove;
        this.cashedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if (this == other){
            return true;
        }
        if (!(other instanceof Piece)){
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return pieceAlliance == otherPiece.getPieceAlliance() && pieceType == otherPiece.getPieceType() &&
                piecePosition == otherPiece.getPiecePosition() && isFirstMove == isFirstMove();
    }

    @Override
    public int hashCode(){
        return this.cashedHashCode;
    }

    // Unspecified return collection (e.g. can be list)
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    public abstract int locationBonus();

    public int getPieceValue(){
        return this.pieceType.getPieceValue();
    }

    public Alliance getPieceAlliance(){
        return this.pieceAlliance;
    }

    public boolean isFirstMove(){
        return this.isFirstMove;
    }

    public int getPiecePosition(){
        return this.piecePosition;
    }

    public PieceType getPieceType(){
        return this.pieceType;
    }

    public enum PieceType {

        PAWN("P", 100),
        ROOK("R", 500),
        KNIGHT("N", 300),
        BISHOP("B", 300),
        QUEEN("Q", 900),
        KING("K", 10000);

        private final String pieceName;
        private final int pieceValue;

        PieceType(final String pieceName, final int pieceValue){
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString(){
            return this.pieceName;
        }

        public int getPieceValue(){
            return this.pieceValue;
        }
    }
}
