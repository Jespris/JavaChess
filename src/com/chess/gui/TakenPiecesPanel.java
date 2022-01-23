package com.chess.gui;

import com.chess.engine.Alliance;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.gui.Table.MoveLog;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);
    private static final String defaultPieceImagePath = "art/ChessPiecesGif/";

    public TakenPiecesPanel(){
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);

    }

    public void redo(final MoveLog moveLog){
        this.southPanel.removeAll();
        this.northPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for (final Move move : moveLog.getMoves()){
            if (move.isAttackMove()){
                final Piece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceAlliance() == Alliance.WHITE){
                    whiteTakenPieces.add(takenPiece);
                } else if (takenPiece.getPieceAlliance() == Alliance.BLACK){
                    blackTakenPieces.add(takenPiece);
                } else {
                    throw new RuntimeException("Piece didn't have alliance in the TakenPiecesPanel, should not reach here!");
                }
            }
        }

        whiteTakenPieces.sort(new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        blackTakenPieces.sort(new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        for (final Piece takenPiece : whiteTakenPieces){
            try {
                final String pieceString = takenPiece.getPieceAlliance().toString().charAt(0) + takenPiece.getPieceType().toString();
                final BufferedImage image = ImageIO.read(new File(defaultPieceImagePath + pieceString + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.southPanel.add(imageLabel);
            } catch(final IOException e) {
                e.printStackTrace();
            }
        }

        for (final Piece takenPiece : blackTakenPieces){
            try {
                final String pieceString = takenPiece.getPieceAlliance().toString().charAt(0) + takenPiece.getPieceType().toString();
                final BufferedImage image = ImageIO.read(new File(defaultPieceImagePath + pieceString + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.northPanel.add(imageLabel);
            } catch(final IOException e) {
                e.printStackTrace();
            }
        }

        validate();
    }
}
