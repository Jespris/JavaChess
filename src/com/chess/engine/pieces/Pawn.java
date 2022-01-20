package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece{

    private final static int[] PAWN_VECTORS = {8};

    Pawn(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int pawnVector: PAWN_VECTORS){
            int destination = this.piecePosition + (pawnVector * this.getPieceAlliance().getDirection());

            if (!BoardUtils.isValidTileCoordinate(destination)){
                continue;
            }

            if (pawnVector == 8 && !board.getTile(destination).isTileOccupied()){
                legalMoves.add(new PawnMove(board, this, this.piecePosition, destination));
            }
        }

        return null;
    }
}
