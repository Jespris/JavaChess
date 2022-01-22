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

public class Bishop extends Piece{
    private final static int[] BISHOP_VECTORS = {-9, -7, 7, 9};

    public Bishop(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance, true);
    }

    public Bishop(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(PieceType.ROOK, piecePosition, pieceAlliance, isFirstMove);

    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int bishopVector : BISHOP_VECTORS){
            int candidateDestinationCoordinate = this.piecePosition;
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){

                if (isFirstColumnExclusion(candidateDestinationCoordinate, bishopVector) || isEightColumnExclusion(candidateDestinationCoordinate, bishopVector)){
                    break;
                }
                candidateDestinationCoordinate += bishopVector;

                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                    final Tile destinationTile = board.getTile(candidateDestinationCoordinate);

                    if (!destinationTile.isTileOccupied()) {
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    } else {

                        final Piece pieceOnTile = destinationTile.getPiece();
                        final Alliance pieceOnTileAlliance = pieceOnTile.getPieceAlliance();

                        if (this.pieceAlliance != pieceOnTileAlliance) {
                            legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceOnTile));
                        }
                        break;
                    }

                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getDestination(), move.getPieceMoved().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.BISHOP.toString();
    }

    // Some bishop vectors are wrong at the edge of the boards, exclude those when adding legal moves
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9) || (candidateOffset == 7));
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && ((candidateOffset == 9) || (candidateOffset == -7));
    }
}
