package com.chess.engine.classic.board;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Move.MoveFactory;
import com.chess.engine.classic.pieces.*;
import com.chess.engine.classic.player.BlackPlayer;
import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {

    private final Map<Integer, Piece> boardConfig;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private final Pawn enPassantPawn;

    private final Move transitionMove;


    private static final Board STANDARD_BOARD = createStandardBoardImpl();

    private Board(final Builder builder){
        this.boardConfig = Collections.unmodifiableMap(builder.boardConfig);

        this.whitePieces = calculateActivePieces(builder, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(builder, Alliance.BLACK);

        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackStandardLegalMoves, whiteStandardLegalMoves);

        this.currentPlayer = builder.nextMoveMaker.choosePlayerByAlliance(this.whitePlayer, this.blackPlayer);

        this.transitionMove = builder.transitionMove != null ? builder.transitionMove : MoveFactory.getNullMove();
    }

    @Override
    public String toString(){
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++){
            final String tileText = prettyPrint(this.boardConfig.get(i));
            builder.append(String.format("%3s", tileText));
            if ((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0){
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private String prettyPrint(final Piece piece) {
        if(piece != null) {
            return piece.getPieceAlliance().isBlack() ?
                    piece.toString().toLowerCase() : piece.toString();
        }
        return "-";
    }

    public Player whitePlayer(){
        return this.whitePlayer;
    }

    public Player blackPlayer(){
        return this.blackPlayer;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    public Collection<Piece> getWhitePieces(){
        return this.whitePieces;
    }

    public Collection<Piece> getBlackPieces(){
        return this.blackPieces;
    }

    public Collection<Piece> getAllPieces() {
        return Stream.concat(this.whitePieces.stream(),
                this.blackPieces.stream()).collect(Collectors.toList());
    }

    public Move getTransitionMove(){
        return this.transitionMove;
    }

    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces){
        final List<Move> legalMoves = new ArrayList<>();
        for (final Piece piece : pieces){
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private static Collection<Piece> calculateActivePieces(final Builder builder, final Alliance alliance){
        return builder.boardConfig.values().stream().  // get all values from pieces map
                filter(piece -> piece.getPieceAlliance() == alliance).  // check if correct alliance
                collect(Collectors.toList());  // return a list
    }

    public static Board createStandardBoard() {
        return STANDARD_BOARD;
    }

    public static Board createStandardBoardImpl(){
        final Builder builder = new Builder();
        // Black pieces:
        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new King(Alliance.BLACK, 4, true, true));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 11));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));
        // white pieces:
        builder.setPiece(new Rook(Alliance.WHITE, 56));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Queen(Alliance.WHITE, 59));
        builder.setPiece(new King(Alliance.WHITE, 60, true, true));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Rook(Alliance.WHITE, 63));
        builder.setPiece(new Pawn(Alliance.WHITE, 48));
        builder.setPiece(new Pawn(Alliance.WHITE, 49));
        builder.setPiece(new Pawn(Alliance.WHITE, 50));
        builder.setPiece(new Pawn(Alliance.WHITE, 51));
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));

        // white to move first
        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    public static Board createMaximumLegalMovesBoard(){
        final Builder builder = new Builder();
        // Black pieces:
        builder.setPiece(new King(Alliance.BLACK, 56, true, true));
        builder.setPiece(new Pawn(Alliance.BLACK, 48, false));
        builder.setPiece(new Pawn(Alliance.BLACK, 49, false));
        // white pieces:
        builder.setPiece(new Rook(Alliance.WHITE, 0));
        builder.setPiece(new Rook(Alliance.WHITE, 7));
        builder.setPiece(new Queen(Alliance.WHITE, 11));
        builder.setPiece(new Queen(Alliance.WHITE, 17));
        builder.setPiece(new Queen(Alliance.WHITE, 22));
        builder.setPiece(new Queen(Alliance.WHITE, 28));
        builder.setPiece(new Queen(Alliance.WHITE, 34));
        builder.setPiece(new Queen(Alliance.WHITE, 39));
        builder.setPiece(new Queen(Alliance.WHITE, 40));
        builder.setPiece(new Queen(Alliance.WHITE, 45));
        builder.setPiece(new Queen(Alliance.WHITE, 51));
        builder.setPiece(new Bishop(Alliance.WHITE, 57));
        builder.setPiece(new Knight(Alliance.WHITE, 58));
        builder.setPiece(new Knight(Alliance.WHITE, 59));
        builder.setPiece(new King(Alliance.WHITE, 61, true, true));
        builder.setPiece(new Bishop(Alliance.WHITE, 62));

        // white to move first
        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    public Piece getPiece(final int tileCoordinate){
        return this.boardConfig.get(tileCoordinate);
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    public Pawn getEnPassantPawn(){
        return this.enPassantPawn;
    }

    public static class Builder {

        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;
        Move transitionMove;

        public Builder() {
            this.boardConfig = new HashMap<>(32, 1.0f);
        }

        public Builder setPiece(final Piece piece){
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker){
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

        public void setEnPassantPawn(final Pawn movedPawn) {
            this.enPassantPawn = movedPawn;
        }

        public Builder setMoveTransition(final Move transitionMove) {
            this.transitionMove = transitionMove;
            return this;
        }
    }
}
