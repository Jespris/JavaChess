package com.chess.pgn;

import com.chess.engine.classic.board.Move;
import com.chess.gui.Table;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PGNUtilities {

    private PGNUtilities() {
        throw new RuntimeException("Not Instantiable!");
    }

    public static void writeGameToPGNFile(final File pgnFile, final Table.MoveLog moveLog) throws IOException{
        final StringBuilder builder = new StringBuilder();
        builder.append(calculateEventString()).append("\n");
        builder.append(calculateDateString()).append("\n");
        builder.append(calculatePlyCountString(moveLog)).append("\n");
        for(final Move move : moveLog.getMoves()) {
            builder.append(move.toString()).append(" ");
        }
        try (final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pgnFile, true)))) {
            writer.write(builder.toString());
        }
    }

    private static String calculatePlyCountString(final Table.MoveLog moveLog) {
        return "[PlyCount \"" + moveLog.size() + "\"]";
    }

    private static String calculateDateString() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return "[Date \"" + dateFormat.format(new Date()) + "\"]";
    }

    private static String calculateEventString() {
        return "[Event \"" + "Jesper Andersson Game" + "\"]";
    }
}
