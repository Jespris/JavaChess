package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

public abstract class Move {

    final Board board;
    final Piece pieceMoved;
    final int endCoordinate;
    final int startCoordinate;

    private Move(final Board board, final Piece pieceMoved, final int startCoordinate, final int endCoordinate){
        this.board = board;
        this.pieceMoved = pieceMoved;
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
    }

    public int getDestination(){
        return this.endCoordinate;
    }

    public abstract Board execute();

    public static final class MajorMove extends Move{

        public MajorMove(final Board board, final Piece pieceMoved, final int startCoordinate, final int endCoordinate) {
            super(board, pieceMoved, startCoordinate, endCoordinate);
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    public static final class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece pieceMoved, final int startCoordinate, final int endCoordinate, final Piece attackedPiece) {
            super(board, pieceMoved, startCoordinate, endCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    public static final class PawnMove extends Move{

        public PawnMove(Board board, Piece pieceMoved, int startCoordinate, int endCoordinate) {
            super(board, pieceMoved, startCoordinate, endCoordinate);
        }

        @Override
        public Board execute() {
            return null;
        }
    }
}
