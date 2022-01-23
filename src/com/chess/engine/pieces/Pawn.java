package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.*;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece{

    private final static int[] PAWN_VECTORS = {7, 8, 9, 16};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);

    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int pawnVector: PAWN_VECTORS){
            final int destination = this.piecePosition + (pawnVector * this.getPieceAlliance().getDirection());

            if (!BoardUtils.isValidTileCoordinate(destination)){
                continue;
            }

            if (pawnVector == 8 && board.getPiece(destination) == null){
                // normal pawn move
                if (this.pieceAlliance.isPawnPromotionSquare(destination)){
                    // TODO: add more promotion alternatives
                    legalMoves.add(new PromotionMove(new PawnMove(board, this, destination)));
                } else {
                    legalMoves.add(new PawnMove(board, this, destination));
                }
            } else if (pawnVector == 16 &&
                    this.isFirstMove() &&
                    ((BoardUtils.SECOND_ROW[this.piecePosition] && this.pieceAlliance.isBlack()) ||
                    (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.pieceAlliance.isWhite()))){
                // two-square pawn move
                final int behindDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (board.getPiece(behindDestinationCoordinate) == null &&
                        board.getPiece(destination) == null){
                    legalMoves.add(new PawnJump(board, this, destination));
                }
            } else if (pawnVector == 7 &&
                    !((BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                    (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))){
                // attack to the right, with exception on the edge of the board
                if (board.getPiece(destination) != null){
                    final Piece pieceOnTile = board.getPiece(destination);
                    if (this.pieceAlliance != pieceOnTile.getPieceAlliance()){
                        if (this.pieceAlliance.isPawnPromotionSquare(destination)){
                            legalMoves.add(new PromotionMove(new PawnAttackMove(board, this, destination, pieceOnTile)));
                        } else {
                            legalMoves.add(new PawnAttackMove(board, this, destination, pieceOnTile));
                        }
                    }
                } else if (board.getEnPassantPawn() != null){ // en passant square
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            legalMoves.add(new EnPassantMove(board, this, destination, pieceOnCandidate));
                        }
                    }
                }
            } else if (pawnVector == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))){
                // attack to the left, with exception on the edge of the board
                if (board.getPiece(destination) != null){
                    final Piece pieceOnTile = board.getPiece(destination);
                    if (this.pieceAlliance != pieceOnTile.getPieceAlliance()){
                        if (this.pieceAlliance.isPawnPromotionSquare(destination)){
                            legalMoves.add(new PromotionMove(new PawnAttackMove(board, this, destination, pieceOnTile)));
                        } else {
                            legalMoves.add(new PawnAttackMove(board, this, destination, pieceOnTile));
                        }
                    }
                } else if (board.getEnPassantPawn() != null){ // en passant square
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            legalMoves.add(new EnPassantMove(board, this, destination, pieceOnCandidate));
                        }
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getDestination(), move.getPieceMoved().getPieceAlliance());
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.pawnBonus(this.piecePosition);
    }

    public Piece getPromotionPiece(){
        // TODO: implement promoting to different pieces
        return new Queen(this.piecePosition, this.pieceAlliance, false);
    }
}
