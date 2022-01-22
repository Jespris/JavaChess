package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player{
    public BlackPlayer(final Board board, final Collection<Move> blackStandardLegalMoves, final Collection<Move> whiteStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateCastles(Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves) {

        final List<Move> castleMoves = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()){
            // black kingside castle
            if (!this.board.getTile(6).isTileOccupied() &&
                    !this.board.getTile(5).isTileOccupied()){
                // empty squares between kingside rook and king
                final Tile rookTile = this.board.getTile(7);
                if (Player.calculateAttacksOnTile(6, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(5, opponentLegalMoves).isEmpty() &&
                        rookTile.getPiece().getPieceType() == Piece.PieceType.ROOK){
                    // no attacks on squares between rook and king and the rook is a rook
                    if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {

                        castleMoves.add(new KingSideCastleMove(this.board, this.playerKing, 6,
                                (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 5));
                    }
                }
            }
            if (!this.board.getTile(1).isTileOccupied() &&
                    !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(3).isTileOccupied()){
                // empty squares between queenside rook and king
                final Tile rookTile = this.board.getTile(0);
                if (Player.calculateAttacksOnTile(1, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(2, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(3, opponentLegalMoves).isEmpty() &&
                        rookTile.getPiece().getPieceType() == Piece.PieceType.ROOK) {
                    // no attacks on squares between rook and king and the rook is a rook
                    if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {

                        castleMoves.add(new QueenSideCastleMove(this.board, this.playerKing, 2,
                                (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
                    }
                }
            }
        }

        return ImmutableList.copyOf(castleMoves);
    }
}
