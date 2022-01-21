package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.AttackMove;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece{

    private final static int[] KING_VECTORS = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int kingVector: KING_VECTORS){
            final int destination = this.piecePosition + kingVector;

            if (isFirstColumnExclusion(destination, kingVector) || isEightColumnExclusion(destination, kingVector)){
                continue;
            }

            if (BoardUtils.isValidTileCoordinate(destination)){
                final Tile destinationTile = board.getTile(destination);

                if (!destinationTile.isTileOccupied()) {
                    legalMoves.add(new MajorMove(board, this, this.piecePosition, destination));
                } else {

                    final Piece pieceOnTile = destinationTile.getPiece();
                    final Alliance pieceOnTileAlliance = pieceOnTile.getPieceAlliance();

                    if (this.pieceAlliance != pieceOnTileAlliance) {
                        legalMoves.add(new AttackMove(board, this, this.piecePosition, destination, pieceOnTile));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString(){
        return PieceType.KING.toString();
    }

    // Some king vectors are wrong at the edge of the boards, exclude those when adding legal moves
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1 || candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (candidateOffset == 1 || candidateOffset == 9 || candidateOffset == -7);
    }
}
