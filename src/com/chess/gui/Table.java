package com.chess.gui;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MoveFactory;
import com.chess.engine.board.Tile;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveStatus;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private final MoveLog moveLog;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovePiece;
    private BoardDirection boardDirection;

    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final String defaultPieceImagePath = "art/ChessPiecesGif/";

    private final Color lightTileColor = new Color(238,238,210);
    private final Color darkTileColor = new Color(118,150,86);

    public Table(){
        this.gameFrame = new JFrame("JavaChess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);

        this.chessBoard = Board.createStandardBoard();

        this.takenPiecesPanel = new TakenPiecesPanel();
        this.gameHistoryPanel = new GameHistoryPanel();

        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();

        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;

        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println("open up that pgn file!");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
            }
        });
        preferencesMenu.add(legalMoveHighlighterCheckbox);
        return preferencesMenu;
    }

    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;

        BoardPanel(){
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i=0; i < BoardUtils.NUM_TILES; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }

            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board chessBoard) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(chessBoard);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog {

        private final List<Move> moves;
        MoveLog(){
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move){
            this.moves.add(move);
        }

        public int size(){
            return this.moves.size();
        }

        public void clear(){
            this.moves.clear();
        }

        public Move removeMove(int index){
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }
    }

    private class TilePanel extends JPanel{

        private final int tileID;

        public TilePanel(final BoardPanel boardPanel, final int tileID){
            super(new GridBagLayout());
            this.tileID = tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (isRightMouseButton(e)) {
                        // cancel selection
                        sourceTile = null;
                        destinationTile = null;
                        humanMovePiece = null;
                    } else if (isLeftMouseButton(e)) {
                        // select square if piece is on that square
                        if (sourceTile == null) {
                            // first click
                            sourceTile = chessBoard.getTile(tileID);
                            humanMovePiece = sourceTile.getPiece();
                            // if empty tile clicked,
                            if (humanMovePiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            // second click
                            destinationTile = chessBoard.getTile(tileID);
                            final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovePiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(this.tileID).isTileOccupied()){
                try {
                    // example WB => white Bishop, BB=> black bishop
                    final String pieceString = board.getTile(this.tileID).getPiece().getPieceAlliance().toString().charAt(0) +
                            board.getTile(this.tileID).toString();
                    final BufferedImage image = ImageIO.read(
                            new File(defaultPieceImagePath + pieceString + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            final boolean isLight = ((this.tileID + tileID / 8) % 2 == 0);
            setBackground(isLight ? lightTileColor : darkTileColor);
        }

        public void drawTile(final Board chessBoard) {
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            highlightLegalMoves(chessBoard);
            validate();
            repaint();
        }

        private void highlightLegalMoves(final Board board){
            if (highlightLegalMoves){
                // user has highlight moves selected in preferences
                for (final Move move : pieceLegalMoves(board)){
                    if (move.getDestination() == this.tileID){
                        try{
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/dot.png")))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovePiece != null && humanMovePiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                // add the castle moves
                if(humanMovePiece.getPieceType() == Piece.PieceType.KING && humanMovePiece.isFirstMove()){
                    final List<Move> includesCastleMoves = new ArrayList<>(board.currentPlayer().calculateCastles(board.currentPlayer().getLegalMoves(),
                            board.currentPlayer().getOpponent().getLegalMoves()));
                    return ImmutableList.copyOf(Iterables.concat(humanMovePiece.calculateLegalMoves(board), includesCastleMoves));
                }
                // if no castle moves, return regular moves
                return humanMovePiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }
    }

    public enum BoardDirection {
        NORMAL{
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }
}
