package com.chess.engine.classic.pieces;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Move.MajorAttackMove;
import com.chess.engine.classic.board.Move.MajorMove;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bishop extends Piece{
    private final static int[] BISHOP_VECTORS = {-9, -7, 7, 9};

    public Bishop(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.BISHOP, pieceAlliance, piecePosition, true);
    }

    public Bishop(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.BISHOP, pieceAlliance, piecePosition, isFirstMove);

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

                    final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);

                    if (pieceAtDestination == null) {
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    } else {

                        final Alliance pieceOnTileAlliance = pieceAtDestination.getPieceAlliance();

                        if (this.pieceAlliance != pieceOnTileAlliance) {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
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
        return new Bishop(move.getPieceMoved().getPieceAlliance(), move.getDestination());
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.bishopBonus(this.piecePosition);
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
