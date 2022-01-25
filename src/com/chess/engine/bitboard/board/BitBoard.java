package com.chess.engine.bitboard.board;

import java.util.Arrays;

public class BitBoard {
    // represent white pieces as binary strings of length 4, black is whites complement
    // (invert every one to a zero)
    // this way we can represent the whole board as bit string with length 64 * 4
    private static void initiateStandardChess(){
        long WP=0L, WR=0L, WN=0L, WB=0L, WQ=0L, WK=0L, BP=0L, BR=0L, BN=0L, BB=0L, BQ=0L, BK=0L;
        String[][] chessBoard = {
                {"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {"-", "-", "-", "-", "-", "-", "-", "-"},
                {"-", "-", "-", "-", "-", "-", "-", "-"},
                {"-", "-", "-", "-", "-", "-", "-", "-"},
                {"-", "-", "-", "-", "-", "-", "-", "-"},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}
        };
        // convert string board into binary bitboards
        arrayToBitBoards(chessBoard, WP, WR, WN, WB, WQ, WK, BP, BR, BN, BB, BQ, BK);
    }
    private static void initiateChess960(){
        long WP=0L, WR=0L, WN=0L, WB=0L, WQ=0L, WK=0L, BP=0L, BR=0L, BN=0L, BB=0L, BQ=0L, BK=0L;
        String[][] chessBoard = {
                {" "," "," "," "," "," "," "," "},
                {"p","p","p","p","p","p","p","p"},
                {" "," "," "," "," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {" "," "," "," "," "," "," "," "},
                {"P","P","P","P","P","P","P","P"},
                {" "," "," "," "," "," "," "," "}
        };
        //step 1: place a bishop
        int random1=(int)(Math.random()*8);
        chessBoard[0][random1]="b";
        chessBoard[7][random1]="B";
        //step 2: place the second bishop on opposite color of first bishop
        int random2=(int)(Math.random()*8);
        while (random2%2==random1%2) {
            random2=(int)(Math.random()*8);
        }
        chessBoard[0][random2]="b";
        chessBoard[7][random2]="B";
        //step 3: place queen on empty square
        int random3=(int)(Math.random()*8);
        while (random3==random1 || random3==random2) {
            random3=(int)(Math.random()*8);
        }
        chessBoard[0][random3]="q";
        chessBoard[7][random3]="Q";
        //step 4: place a knight on the left side of the board, then place a knight on the right side of the board
        int random4a=(int)(Math.random()*5);
        int counter=0;
        int loop=0;
        while (counter-1<random4a) {
            if (chessBoard[0][loop].equals(" ")) {counter++;}
            loop++;
        }
        chessBoard[0][loop-1]="n";
        chessBoard[7][loop-1]="N";
        int random4b=(int)(Math.random()*4);
        counter=0;
        loop=0;
        while (counter-1<random4b) {
            if (chessBoard[0][loop].equals(" ")) {counter++;}
            loop++;
        }
        chessBoard[0][loop-1]="n";
        chessBoard[7][loop-1]="N";
        //step 5: place a rook, then king, then rook on the empty squares remaining
        counter=0;
        while (!" ".equals(chessBoard[0][counter])) {
            counter++;
        }
        chessBoard[0][counter]="r";
        chessBoard[7][counter]="R";
        while (!" ".equals(chessBoard[0][counter])) {
            counter++;
        }
        chessBoard[0][counter]="k";
        chessBoard[7][counter]="K";
        while (!" ".equals(chessBoard[0][counter])) {
            counter++;
        }
        chessBoard[0][counter]="r";
        chessBoard[7][counter]="R";
        arrayToBitBoards(chessBoard,WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK);
    }

    public static void importFEN(String fenString) {
        //not chess960 compatible
        Orion.WP=0; Orion.WN=0; Orion.WB=0;
        Orion.WR=0; Orion.WQ=0; Orion.WK=0;
        Orion.BP=0; Orion.BN=0; Orion.BB=0;
        Orion.BR=0; Orion.BQ=0; Orion.BK=0;
        Orion.CWK=false; Orion.CWQ=false;
        Orion.CBK=false; Orion.CBQ=false;
        int charIndex = 0;
        int boardIndex = 0;
        while (fenString.charAt(charIndex) != ' ')
        {
            switch (fenString.charAt(charIndex++))
            {
                case 'P': Orion.WP |= (1L << boardIndex++);
                    break;
                case 'p': Orion.BP |= (1L << boardIndex++);
                    break;
                case 'N': Orion.WN |= (1L << boardIndex++);
                    break;
                case 'n': Orion.BN |= (1L << boardIndex++);
                    break;
                case 'B': Orion.WB |= (1L << boardIndex++);
                    break;
                case 'b': Orion.BB |= (1L << boardIndex++);
                    break;
                case 'R': Orion.WR |= (1L << boardIndex++);
                    break;
                case 'r': Orion.BR |= (1L << boardIndex++);
                    break;
                case 'Q': Orion.WQ |= (1L << boardIndex++);
                    break;
                case 'q': Orion.BQ |= (1L << boardIndex++);
                    break;
                case 'K': Orion.WK |= (1L << boardIndex++);
                    break;
                case 'k': Orion.BK |= (1L << boardIndex++);
                    break;
                case '/':
                    break;
                case '1': boardIndex++;
                    break;
                case '2': boardIndex += 2;
                    break;
                case '3': boardIndex += 3;
                    break;
                case '4': boardIndex += 4;
                    break;
                case '5': boardIndex += 5;
                    break;
                case '6': boardIndex += 6;
                    break;
                case '7': boardIndex += 7;
                    break;
                case '8': boardIndex += 8;
                    break;
                default:
                    break;
            }
        }
        Orion.WhiteToMove = (fenString.charAt(++charIndex) == 'w');
        charIndex += 2;
        while (fenString.charAt(charIndex) != ' ')
        {
            switch (fenString.charAt(charIndex++))
            {
                case '-':
                    break;
                case 'K': Orion.CWK = true;
                    break;
                case 'Q': Orion.CWQ = true;
                    break;
                case 'k': Orion.CBK = true;
                    break;
                case 'q': Orion.CBQ = true;
                    break;
                default:
                    break;
            }
        }
        if (fenString.charAt(++charIndex) != '-')
        {
            Orion.EP = Moves.FileMasks8[fenString.charAt(charIndex++) - 'a'];
        }
        //the rest of the fenString is not yet utilized
    }

    private static void arrayToBitBoards(final String[][] chessBoard, long WP, long WR, long WN, long WB, long WQ, long WK, long BP, long BR, long BN, long BB, long BQ, long BK) {
        String binary;
        for (int i = 0; i < 64; i++){
            // 64 zeroes?
            binary = "0000000000000000000000000000000000000000000000000000000000000000";
            // System.out.println("binary length: " + binary.length());
            binary = binary.substring(i+1) + 1 + binary.substring(0, i);
            switch (chessBoard[i/8][i%8]){
                case "P":
                    WP += convertStringToBitBoard(binary);
                    break;
                case "R":
                    WR += convertStringToBitBoard(binary);
                    break;
                case "N":
                    WN += convertStringToBitBoard(binary);
                    break;
                case "B":
                    WB += convertStringToBitBoard(binary);
                    break;
                case "Q":
                    WQ += convertStringToBitBoard(binary);
                    break;
                case "K":
                    WK += convertStringToBitBoard(binary);
                    break;
                case "p":
                    BP += convertStringToBitBoard(binary);
                    break;
                case "r":
                    BR += convertStringToBitBoard(binary);
                    break;
                case "n":
                    BN += convertStringToBitBoard(binary);
                    break;
                case "b":
                    BB += convertStringToBitBoard(binary);
                    break;
                case "q":
                    BQ += convertStringToBitBoard(binary);
                    break;
                case "k":
                    BK += convertStringToBitBoard(binary);
                    break;
                default:
                    break;
            }
        }
        drawArray(WP, WR, WN, WB, WQ, WK, BP, BR, BN, BB, BQ, BK);
    }

    public static void drawArray(long wp, long wr, long wn, long wb, long wq, long wk, long bp, long br, long bn, long bb, long bq, long bk) {
        String[][] chessBoard = new String[8][8];
        for (int i=0; i<64; i++){
            chessBoard[i/8][i%8] = " ";
        }
        for (int i=0; i<64; i++){
            if (((wp >> i)&1)==1) { chessBoard[i/8][i%8] = "P"; }
            if (((wr >> i)&1)==1) { chessBoard[i/8][i%8] = "R"; }
            if (((wn >> i)&1)==1) { chessBoard[i/8][i%8] = "N"; }
            if (((wb >> i)&1)==1) { chessBoard[i/8][i%8] = "B"; }
            if (((wq >> i)&1)==1) { chessBoard[i/8][i%8] = "Q"; }
            if (((wk >> i)&1)==1) { chessBoard[i/8][i%8] = "K"; }
            if (((bp >> i)&1)==1) { chessBoard[i/8][i%8] = "p"; }
            if (((br >> i)&1)==1) { chessBoard[i/8][i%8] = "r"; }
            if (((bn >> i)&1)==1) { chessBoard[i/8][i%8] = "n"; }
            if (((bb >> i)&1)==1) { chessBoard[i/8][i%8] = "b"; }
            if (((bq >> i)&1)==1) { chessBoard[i/8][i%8] = "q"; }
            if (((bk >> i)&1)==1) { chessBoard[i/8][i%8] = "k"; }
        }
        for (int i=0; i<8; i++){
            System.out.println(Arrays.toString(chessBoard[i]));
        }
    }

    private static long convertStringToBitBoard(final String binary) {
        if (binary.charAt(0) == '0') {
            // not a negative number
            return Long.parseLong(binary, 2);
        } else {
            // negative number
            return Long.parseLong("1"+binary.substring(2), 2)*2; // complement the number
        }
    }

    public static void main(String[] args){
        initiateStandardChess();
    }
}

