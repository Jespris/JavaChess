package com.tests.chess.engine.board;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TestPieces.class,
        TestBoard.class,
        TestStaleMate.class,
        TestCastling.class,
        TestFenParser.class,
        TestBestMoveFinder.class
})
class ChessTestSuite {}
