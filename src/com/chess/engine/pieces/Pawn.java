package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.AttackMove;
import com.chess.engine.board.Move.PawnMove;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece{

    private final static int[] PAWN_VECTORS = {7, 8, 9, 16};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int pawnVector: PAWN_VECTORS){
            final int destination = this.piecePosition + (pawnVector * this.getPieceAlliance().getDirection());

            if (!BoardUtils.isValidTileCoordinate(destination)){
                continue;
            }

            if (pawnVector == 8 && !board.getTile(destination).isTileOccupied()){
                // normal pawn move
                // TODO: add pawn promotion with capture
                legalMoves.add(new PawnMove(board, this, this.piecePosition, destination));

            } else if (pawnVector == 16 &&
                    this.isFirstMove() &&
                    (BoardUtils.SECOND_ROW[this.piecePosition] && this.pieceAlliance.isBlack()) ||
                    (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.pieceAlliance.isWhite())){
                // two-square pawn move
                final int behindDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindDestinationCoordinate).isTileOccupied() &&
                        !board.getTile(destination).isTileOccupied()){
                    legalMoves.add(new PawnMove(board, this, this.piecePosition, destination));
                }
            } else if (pawnVector == 7 &&
                    !((BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                    (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))){
                // attack to the right, with exception on the edge of the board
                // TODO: add pawn promotion with capture
                if (board.getTile(destination).isTileOccupied()){
                    final Piece pieceOnTile = board.getTile(destination).getPiece();
                    if (this.pieceAlliance != pieceOnTile.getPieceAlliance()){
                        legalMoves.add(new AttackMove(board, this, this.piecePosition, destination, pieceOnTile));
                    }
                }
            } else if (pawnVector == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))){
                // attack to the left, with exception on the edge of the board
                // TODO: add pawn promotion with capture
                if (board.getTile(destination).isTileOccupied()){
                    final Piece pieceOnTile = board.getTile(destination).getPiece();
                    if (this.pieceAlliance != pieceOnTile.getPieceAlliance()){
                        legalMoves.add(new AttackMove(board, this, this.piecePosition, destination, pieceOnTile));
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
}
