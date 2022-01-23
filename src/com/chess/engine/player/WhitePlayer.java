package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
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
import java.util.Collections;
import java.util.List;

import static com.chess.engine.pieces.Piece.PieceType.*;

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

        if (!hasCastleOpportunities()){
            return Collections.emptyList();
        }

        final List<Move> castleMoves = new ArrayList<>();

        if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 60 && !this.isInCheck()){
            // white kingside castle
            if (this.board.getPiece(61) == null &&
                    this.board.getPiece(62) == null){
                // empty squares between kingside rook and king
                final Piece queenSideRook = this.board.getPiece(63);
                if (queenSideRook != null && queenSideRook.isFirstMove() &&
                        Player.calculateAttacksOnTile(61, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(62, opponentLegalMoves).isEmpty() &&
                        queenSideRook.getPieceType() == ROOK){
                    // no attacks on squares between rook and king and the rook is a rook
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 52)) {

                        castleMoves.add(
                                new KingSideCastleMove(
                                        this.board, this.playerKing, 62,
                                        (Rook)queenSideRook, queenSideRook.getPiecePosition(), 61));
                    }
                }
            }
            // white queenside castle
            if (this.board.getPiece(59) == null &&
                    this.board.getPiece(58) == null &&
                    this.board.getPiece(57) == null){
                // empty squares between queenside rook and king
                final Piece queenSideRook = this.board.getPiece(56);
                if (queenSideRook != null && queenSideRook.isFirstMove() &&
                        Player.calculateAttacksOnTile(58, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(59, opponentLegalMoves).isEmpty() &&
                        queenSideRook.getPieceType() == ROOK) {
                    // no attacks on squares between rook and king and the rook is a rook
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 52)) {

                        castleMoves.add(
                                new QueenSideCastleMove(
                                        this.board, this.playerKing, 58,
                                        (Rook)queenSideRook, queenSideRook.getPiecePosition(), 59));
                    }
                }
            }
        }

        return ImmutableList.copyOf(castleMoves);
    }

    @Override
    public String toString(){
        return "WHITE";
    }
}
