package com.chess.engine.board;

/*
Project started here by Jesper Andersson on 19.1.2022
 */

import com.chess.engine.pieces.Piece;
import com.chess.engine.board.BoardUtils;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {
    protected final int tileCoordinate;   // represents the index of the tile on the board (0-63), can only be set once

    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles(){
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for (int i = 0; i < BoardUtils.NUM_TILES; i++){
            emptyTileMap.put(i, new EmptyTile(i));
        }

        // not using guava? do Collections.unmodifiableMap(emptyTileMap);
        return ImmutableMap.copyOf(emptyTileMap);
    }

    public static Tile createTile(final int tileCoordinate, final Piece piece){
        // The only method a user can call, which returns a cached empty tile if no piece is specified,
        // otherwise returns a new Occupied tile object
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }

    private Tile(int coordinate){
        this.tileCoordinate = coordinate;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public int getTileCoordinate() {
        return this.tileCoordinate;
    }

    public static final class EmptyTile extends Tile {
        EmptyTile(int coordinate){
            super(coordinate);
        }

        @Override
        public String toString(){
            return "-";
        }

        @Override
        public boolean isTileOccupied(){
            return false;
        }

        @Override
        public Piece getPiece(){
            return null;
        }
    }

    public static final class OccupiedTile extends Tile {
        private final Piece pieceOnTile;  // to make this class immutable, for easy garbage collection solutions and more

        private OccupiedTile(int coordinate, Piece pieceOnTile){
            super(coordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString(){
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() : getPiece().toString();
        }

        @Override
        public boolean isTileOccupied(){
            return true;
        }

        @Override
        public Piece getPiece(){
            return this.pieceOnTile;
        }
    }
}
