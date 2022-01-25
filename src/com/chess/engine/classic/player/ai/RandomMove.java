package com.chess.engine.classic.player.ai;

import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.Move;

public class RandomMove implements MoveStrategy {
    @Override
    public long getNumBoardsEvaluated() {
        return 0;
    }

    @Override
    public Move execute(Board board) {
        return null;
    }
}
