package com.chess.engine.board;

import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class Move {

    final Board board;
    final Piece pieceMoved;
    final int endCoordinate;

    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece pieceMoved, final int endCoordinate){
        this.board = board;
        this.pieceMoved = pieceMoved;
        this.endCoordinate = endCoordinate;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;

        result = prime * result + this.endCoordinate;
        result = prime * result + this.pieceMoved.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if (this == other){
            return true;
        }
        if (!(other instanceof Move)){
            return false;
        }
        final Move otherMove = (Move)other;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestination() == otherMove.getDestination() &&
                getPieceMoved().equals(otherMove.getPieceMoved());
    }

    public int getCurrentCoordinate() { return this.getPieceMoved().getPiecePosition(); }

    public int getDestination(){
        return this.endCoordinate;
    }

    public Piece getPieceMoved(){
        return this.pieceMoved;
    }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastlingMove(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    public Board execute() {
        final Builder builder = new Builder();

        for (final Piece piece : this.board.currentPlayer().getActivePieces()){
            if (!this.pieceMoved.equals(piece)){
                builder.setPiece(piece);
            }
        }

        for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }

        // move the moved piece
        builder.setPiece(this.pieceMoved.movePiece(this));
        // switch turns
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());

        return builder.build();
    }

    public static final class MajorMove extends Move{

        public MajorMove(final Board board, final Piece pieceMoved, final int endCoordinate) {
            super(board, pieceMoved, endCoordinate);
        }
    }

    public static class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece pieceMoved, final int endCoordinate, final Piece attackedPiece) {
            super(board, pieceMoved, endCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean isAttack(){
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other){
            if (this == other){
                return true;
            }
            if (!(other instanceof AttackMove)){
                return false;
            }
            final Move otherAttackMove = (AttackMove)other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }
    }

    public static class PawnMove extends Move{

        public PawnMove(Board board, Piece pieceMoved, int endCoordinate) {
            super(board, pieceMoved, endCoordinate);
        }
    }

    public static class PawnAttackMove extends AttackMove{

        public PawnAttackMove(Board board, Piece pieceMoved, int endCoordinate, final Piece attackedPiece) {
            super(board, pieceMoved, endCoordinate, attackedPiece);
        }
    }

    public static final class EnPassantMove extends PawnAttackMove{

        public EnPassantMove(Board board, Piece pieceMoved, int endCoordinate, final Piece attackedPiece) {
            super(board, pieceMoved, endCoordinate, attackedPiece);
        }
    }

    public static final class PawnJump extends Move{

        public PawnJump(Board board, Piece pieceMoved, int endCoordinate) {
            super(board, pieceMoved, endCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()){
                if (!this.pieceMoved.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn)this.pieceMoved.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class PawnPromotion extends PawnMove{

        public PawnPromotion(Board board, Piece pieceMoved, int endCoordinate) {
            super(board, pieceMoved, endCoordinate);
        }
    }

    static abstract class CastleMove extends Move{

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final Board board, final Piece pieceMoved, final int endCoordinate,
                          final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, pieceMoved, endCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove(){
            return true;
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()){
                if (!this.pieceMoved.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.pieceMoved.movePiece(this));
            // TODO: look into first move in pieces
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class KingSideCastleMove extends CastleMove{

        public KingSideCastleMove(final Board board, final Piece pieceMoved, final int endCoordinate,
                                  final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, pieceMoved, endCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString(){
            return "O-O";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(final Board board, final Piece pieceMoved, final int endCoordinate,
                                  final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, pieceMoved, endCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString(){
            return "O-O-O";
        }
    }

    public static final class NullMove extends Move{

        public NullMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute(){
            throw new RuntimeException("Cannot execute a null move!");
        }
    }

    public static class MoveFactory{
        private MoveFactory(){
            throw new RuntimeException("MoveFactory not instantiable!");
        }

        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate){
            for (final Move move : board.getAllLegalMoves()){
                if (move.getCurrentCoordinate() == currentCoordinate && move.getDestination() == destinationCoordinate){
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}
