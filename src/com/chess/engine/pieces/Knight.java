package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move.AttackMove;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Tile;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece{

    private final static int[] knightJumps = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, true);
    }

    public Knight(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);

    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {

        int destination;
        final List<Move> legalMoves = new ArrayList<>();

        for(final int knightJump : knightJumps){
            destination = this.piecePosition + knightJump;
            if (BoardUtils.isValidTileCoordinate(destination)){

                if (isFirstColumnExclusion(this.piecePosition, knightJump) ||
                        isSecondColumnExclusion(this.piecePosition, knightJump) ||
                        isSeventhColumnExclusion(this.piecePosition, knightJump) ||
                        isEightColumnExclusion(this.piecePosition, knightJump)){
                    // knight jump is wrong at the edge of the board
                    continue;
                }

                final Tile destinationTile = board.getTile(destination);

                if (!destinationTile.isTileOccupied()){
                    legalMoves.add(new MajorMove(board, this, destination));
                } else {

                    final Piece pieceOnTile = destinationTile.getPiece();
                    final Alliance pieceOnTileAlliance = pieceOnTile.getPieceAlliance();

                    if (this.pieceAlliance != pieceOnTileAlliance){
                        legalMoves.add(new MajorAttackMove(board, this, destination, pieceOnTile));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }

    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getDestination(), move.getPieceMoved().getPieceAlliance());
    }

    // Some knight-jump offsets are wrong at the edge of the boards, exclude those when adding legal moves
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -17) || (candidateOffset == -10) ||
                (candidateOffset == 6) || (candidateOffset == 15));
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.SECOND_COLUMN[currentPosition] && ((candidateOffset == -10) || (candidateOffset == 6));
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && ((candidateOffset == 10) || (candidateOffset == -6));
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && ((candidateOffset == 17) || (candidateOffset == 10) ||
                (candidateOffset == -6) || (candidateOffset == -15));
    }
}
