package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Piece.PieceType;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.pieces.Piece.PieceType.*;

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
    public Collection<Move> calculateCastles(Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves) {

        if (!hasCastleOpportunities()){
            return Collections.emptyList();
        }

        final List<Move> castleMoves = new ArrayList<>();

        if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 4 && !this.isInCheck()){
            // black kingside castle
            if (this.board.getPiece(6) == null &&
                    this.board.getPiece(5) == null){
                // empty squares between kingside rook and king
                final Piece queenSideRook = this.board.getPiece(7);
                if (queenSideRook != null && queenSideRook.isFirstMove() &&
                        Player.calculateAttacksOnTile(6, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(5, opponentLegalMoves).isEmpty() &&
                        queenSideRook.getPieceType() == ROOK){
                    // no attacks on squares between rook and king and the rook is a rook
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {

                        castleMoves.add(
                                new KingSideCastleMove(
                                        this.board, this.playerKing, 6,
                                        (Rook)queenSideRook, queenSideRook.getPiecePosition(), 5));
                    }
                }
            }
            if (this.board.getPiece(1) == null &&
                    this.board.getPiece(2) == null &&
                    this.board.getPiece(3) == null){
                // empty squares between queenside rook and king
                final Piece queenSideRook = this.board.getPiece(0);
                if (queenSideRook != null && queenSideRook.isFirstMove() &&
                        Player.calculateAttacksOnTile(2, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(3, opponentLegalMoves).isEmpty() &&
                        queenSideRook.getPieceType() == ROOK) {
                    // no attacks on squares between rook and king and the rook is a rook
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {

                        castleMoves.add(
                                new QueenSideCastleMove(
                                        this.board, this.playerKing, 2,
                                        (Rook)queenSideRook, queenSideRook.getPiecePosition(), 3));
                    }
                }
            }
        }

        return ImmutableList.copyOf(castleMoves);
    }

    @Override
    public String toString(){
        return "BLACK";
    }
}
