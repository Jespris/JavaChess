package com.chess.engine.bitboard.board;

public class Orion {
    private final long WP;
    private final long WN;
    private final long WB;
    private final long WR;
    private final long WQ;
    private final long WK;
    private final long BP;
    private final long BN;
    private final long BB;
    private final long BR;
    private final long BQ;
    private final long BK;
    private final long EP;
    private final boolean CWK;
    private final boolean CWQ;
    private final boolean CBK;
    private final boolean CBQ;
    private final boolean WhiteToMove;
    private int moveCounter;
    private final int MATE_SCORE;

    private Orion(){
        this.WP=0L;
        this.WN=0L;
        this.WB=0L;
        this.WR=0L;
        this.WQ=0L;
        this.WK=0L;
        this.BP=0L;
        this.BN=0L;
        this.BB=0L;
        this.BR=0L;
        this.BQ=0L;
        this.BK=0L;
        this.EP=0L;
        this.CWK=true;  // true=castle is possible
        this.CWQ=true;
        this.CBK=true;
        this.CBQ=true;
        this.WhiteToMove=true;
        this.MATE_SCORE = 10000;
        this.moveCounter = 0;
    }

    private static final Orion INSTANCE = new Orion();

    public static Orion get(){
        return INSTANCE;
    }

    public int getMoveCounter(){
        return this.moveCounter;
    }

    public void incrementMoveCounter(){
        this.moveCounter++;
    }

    public int getMateScore(){
        return this.MATE_SCORE;
    }

    public Long getEP(){
        return this.EP;
    }

    public Long getWP(){
        return this.WP;
    }

    public Long getBP(){
        return this.BP;
    }

    public Long getWR(){
        return this.WR;
    }

    public Long getBR(){
        return this.BR;
    }
    public Long getWN(){
        return this.WN;
    }
    public Long getBN(){
        return this.BN;
    }

    public Long getWB(){
        return this.WB;
    }

    public Long getBB(){
        return this.BB;
    }

    public Long getWQ(){
        return this.WQ;
    }

    public Long getBQ(){
        return this.BQ;
    }
    public Long getWK(){
        return this.WK;
    }
    public Long getBK(){
        return this.BK;
    }

    public boolean getCWK(){
        return this.CWK;
    }

    public boolean getCWQ(){
        return this.CWQ;
    }

    public boolean getCBK(){
        return this.CBK;
    }

    public boolean getCBQ(){
        return this.CBQ;
    }

    public boolean getIsWhiteToMove(){
        return this.WhiteToMove;
    }

    public static void main(String[] args) {
        Zobrist.zobristFillArray();
        BitBoard.importFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        UCI.inputPrint();
        UCI.inputPrint();
        BitBoard.importFEN("rnbqkbnr/ppp1pppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        UCI.inputPrint();
        long startTime = System.currentTimeMillis();
        System.out.println(PrincipalVariation.pvSearch(-1000,1000,0));
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds");
        UCI.uciCommunication();
    }
}
