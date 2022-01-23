package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import com.chess.engine.board.Tile;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Piece.PieceType;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player{
    public WhitePlayer(final Board board, final Collection<Move> whiteStandardLegalMoves, final Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    public Collection<Move> calculateCastles(Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves) {

        final List<Move> castleMoves = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()){
            // white kingside castle
            if (!this.board.getTile(61).isTileOccupied() &&
                    !this.board.getTile(62).isTileOccupied()){
                // empty squares between kingside rook and king
                final Tile rookTile = this.board.getTile(63);
                if (Player.calculateAttacksOnTile(61, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(62, opponentLegalMoves).isEmpty() &&
                        rookTile.getPiece().getPieceType() == PieceType.ROOK){
                    // no attacks on squares between rook and king and the rook is a rook
                    if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {

                        castleMoves.add(new KingSideCastleMove(this.board, this.playerKing, 62,
                                (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 61));
                    }
                }
            }
            if (!this.board.getTile(57).isTileOccupied() &&
                    !this.board.getTile(58).isTileOccupied() &&
                    !this.board.getTile(59).isTileOccupied()){
                // empty squares between queenside rook and king
                final Tile rookTile = this.board.getTile(56);
                if (Player.calculateAttacksOnTile(57, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(58, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(59, opponentLegalMoves).isEmpty() &&
                        rookTile.getPiece().getPieceType() == PieceType.ROOK) {
                    // no attacks on squares between rook and king and the rook is a rook
                    if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {

                        castleMoves.add(new QueenSideCastleMove(this.board, this.playerKing, 58,
                                (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 59));
                    }
                }
            }
        }

        return ImmutableList.copyOf(castleMoves);
    }
}
