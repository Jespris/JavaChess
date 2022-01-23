package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

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
