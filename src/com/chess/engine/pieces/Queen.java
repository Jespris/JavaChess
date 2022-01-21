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

public class Queen extends Piece{
    private final static int[] QUEEN_VECTORS = {-9, -8, -7, -1, 1, 7, 8, 9};

    public Queen(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int queenVector : QUEEN_VECTORS){
            int candidateDestinationCoordinate = this.piecePosition;
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){

                if (isFirstColumnExclusion(candidateDestinationCoordinate, queenVector) || isEightColumnExclusion(candidateDestinationCoordinate, queenVector)){
                    break;
                }
                candidateDestinationCoordinate += queenVector;

                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                    final Tile destinationTile = board.getTile(candidateDestinationCoordinate);

                    if (!destinationTile.isTileOccupied()) {
                        legalMoves.add(new MajorMove(board, this, this.piecePosition, candidateDestinationCoordinate));
                    } else {

                        final Piece pieceOnTile = destinationTile.getPiece();
                        final Alliance pieceOnTileAlliance = pieceOnTile.getPieceAlliance();

                        if (this.pieceAlliance != pieceOnTileAlliance) {
                            legalMoves.add(new AttackMove(board, this, this.piecePosition, candidateDestinationCoordinate, pieceOnTile));
                        }
                        break;
                    }

                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString(){
        return PieceType.QUEEN.toString();
    }

    // Some queen vectors are wrong at the edge of the boards, exclude those when adding legal moves
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1 || candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (candidateOffset == 1 || candidateOffset == 9 || candidateOffset == -7);
    }

}
