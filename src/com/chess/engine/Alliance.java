package com.chess.engine;

import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

public enum Alliance {
    WHITE() {
        @Override
        public int getDirection(){
            return -1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return false;
        }

        @Override
        public Player choosePlayerByAlliance(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }
    },
    BLACK() {
        @Override
        public int getDirection(){
            return 1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return false;
        }

        @Override
        public Player choosePlayerByAlliance(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }
    };

    public abstract int getDirection();

    public abstract boolean isWhite();
    public abstract boolean isBlack();

    public abstract boolean isPawnPromotionSquare(int position);

    public abstract Player choosePlayerByAlliance(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);

    public abstract int getOppositeDirection();
}
