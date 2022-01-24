package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MoveFactory;
import com.chess.engine.board.Tile;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveStatus;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.AlphaBetaWithMoveSorting;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.chess.pgn.FenUtilities;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.chess.pgn.PGNUtilities.writeGameToPGNFile;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private final MoveLog moveLog;

    private final GameSetup gameSetup;
    private final TimeControlSetup timeControlSetup;

    private Piece sourcePiece;
    private Piece humanMovePiece;
    private BoardDirection boardDirection;

    private Move computerMove;

    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);

    private String pieceIconPath;

    private Color lightTileColor = new Color(238,238,210);
    private Color darkTileColor = new Color(118,150,86);

    enum PlayerType{
        HUMAN,
        COMPUTER
    }

    private static Table INSTANCE = new Table();

    private Table(){
        this.pieceIconPath = getDefaultPieceIconPath();
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
        this.addObserver(new TableGameAIWatcher());

        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.timeControlSetup = new TimeControlSetup(this.gameFrame, true);

        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;

        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);
    }

    private String getDefaultPieceIconPath() {
        return "art/ChessPieces/";
    }

    public void reset(){
        INSTANCE = new Table();
    }

    public void show(){
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    public static Table get(){
        return INSTANCE;
    }

    private GameSetup getGameSetup(){
        return this.gameSetup;
    }

    private TimeControlSetup getTimeControlSetup() { return this.timeControlSetup; }

    private void setupUpdate(final GameSetup gameSetup){
        setChanged();
        notifyObservers(gameSetup);
    }

    private Board getGameBoard(){
        return this.chessBoard;
    }

    private static class TableGameAIWatcher implements Observer {
        @Override
        public void update(final Observable o, final Object arg) {
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                    !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                    !Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                // create an AI thread
                final AIThinkTank thinkTank = new AIThinkTank();
                // execute AI work
                thinkTank.execute();
            }

            if (Table.get().getGameBoard().currentPlayer().isInCheckMate()){
                System.out.println("Game over! " + Table.get().getGameBoard().currentPlayer().toString() + " is checkmated!");
                JOptionPane.showMessageDialog(
                        Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in checkmate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            if (Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                System.out.println("Game over by stalemate!");
                JOptionPane.showMessageDialog(
                        Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private static class AIThinkTank extends SwingWorker<Move, String>{

            private AIThinkTank(){

            }

            @Override
            protected Move doInBackground() throws Exception{
                final MoveStrategy strategy = new AlphaBetaWithMoveSorting(Table.get().getGameSetup().getSearchDepth(), false);
                if (isCancelled()){
                    System.out.println("bestMoveFinder cancelled!");
                    return null;
                }
                return strategy.execute(Table.get().getGameBoard());
            }

            @Override
            public void done(){
                try{
                    final Move bestMove = get();
                    if (bestMove != null) {
                        Table.get().updateComputerMove(bestMove);
                        Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getToBoard());
                        Table.get().getMoveLog().addMove(bestMove);
                        Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                        Table.get().moveMadeUpdate(PlayerType.COMPUTER);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private JFrame getGameFrame(){
        return this.gameFrame;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    private void updateComputerMove(final Move move) {
        this.computerMove = move;
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private void timeControlSetupUpdate(TimeControlSetup timeControlSetup) {
        setChanged();
        notifyObservers(timeControlSetup);
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showOpenDialog(Table.get().getGameFrame());
            if (option == JFileChooser.APPROVE_OPTION){
                loadPGNFile(chooser.getSelectedFile());
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem openFEN = new JMenuItem("Load FEN File");
        openFEN.addActionListener(e ->  {
            String fenString = JOptionPane.showInputDialog("Input FEN");
            if (fenString != null){
                clearBoard();
                chessBoard = FenUtilities.createGameFromFEN(fenString);
                Table.get().getBoardPanel().drawBoard(chessBoard);
            }
        });
        fileMenu.add(openFEN);

        final JMenuItem saveToPGN = new JMenuItem("Save Game");
        saveToPGN.addActionListener(e -> {
            final JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileFilter() {
                @Override
                public String getDescription() {
                    return ".pgn";
                }
                @Override
                public boolean accept(final File file) {
                    return file.isDirectory() || file.getName().toLowerCase().endsWith("pgn");
                }
            });
            final int option = chooser.showSaveDialog(Table.get().getGameFrame());
            if (option == JFileChooser.APPROVE_OPTION) {
                savePGNFile(chooser.getSelectedFile());
            }
        });
        fileMenu.add(saveToPGN);

        final JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Table.get().reset();
            }
        });
        fileMenu.add(newGameItem);

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

    private static void savePGNFile(final File pgnFile) {
        try{
            writeGameToPGNFile(pgnFile, Table.get().getMoveLog());
        } catch (final IOException e){
            e.printStackTrace();
        }
    }

    private static void loadPGNFile(File pgnFile){
        try {
            persistPGNFile(pgnFile);
        } catch (final IOException e){
            e.printStackTrace();
        }
    }

    public static void persistPGNFile(final File pgnFile) throws IOException {
        System.out.println("PGN file import not implemented yet!");
    }

    private void clearBoard() {
        for (int i = Table.get().getMoveLog().size() - 1; i >= 0; i--){
            final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
            this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
        }
        this.computerMove = null;
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(chessBoard);
    }

    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu = new JMenu("Preferences");

        final JMenu colorChooserSubMenu = new JMenu("Choose Colors");

        final JMenuItem chooseDarkMenuItem = new JMenuItem("Choose Dark Tile Color");
        colorChooserSubMenu.add(chooseDarkMenuItem);

        final JMenuItem chooseLightMenuItem = new JMenuItem("Choose Light Tile Color");
        colorChooserSubMenu.add(chooseLightMenuItem);

        final JMenuItem chooseLegalHighlightMenuItem = new JMenuItem(
                "Choose Legal Move Highlight Color");
        colorChooserSubMenu.add(chooseLegalHighlightMenuItem);

        preferencesMenu.add(colorChooserSubMenu);

        chooseDarkMenuItem.addActionListener(e -> {
            final Color colorChoice = JColorChooser.showDialog(Table.get().getGameFrame(), "Choose Dark Tile Color",
                    Table.get().getGameFrame().getBackground());
            if (colorChoice != null) {
                Table.get().getBoardPanel().setTileDarkColor(chessBoard, colorChoice);
            }
        });

        chooseLightMenuItem.addActionListener(e -> {
            final Color colorChoice = JColorChooser.showDialog(Table.get().getGameFrame(), "Choose Light Tile Color",
                    Table.get().getGameFrame().getBackground());
            if (colorChoice != null) {
                Table.get().getBoardPanel().setTileLightColor(chessBoard, colorChoice);
            }
        });

        final JMenu chessMenChoiceSubMenu = new JMenu("Choose Chess Men Image Set");

        final JMenuItem defaultSetMenuItem = new JMenuItem("Default Set");
        chessMenChoiceSubMenu.add(defaultSetMenuItem);

        defaultSetMenuItem.addActionListener(e -> {
            pieceIconPath = "art/simple/";
            Table.get().getBoardPanel().drawBoard(chessBoard);
        });

        preferencesMenu.add(chessMenChoiceSubMenu);

        chooseLegalHighlightMenuItem.addActionListener(e -> {
            System.out.println("implement me");
            Table.get().getGameFrame().repaint();
        });

        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip board");

        flipBoardMenuItem.addActionListener(e -> {
            boardDirection = boardDirection.opposite();
            boardPanel.drawBoard(chessBoard);
        });

        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();


        final JCheckBoxMenuItem cbLegalMoveHighlighter = new JCheckBoxMenuItem(
                "Highlight Legal Moves", false);

        cbLegalMoveHighlighter.addActionListener(e -> highlightLegalMoves = cbLegalMoveHighlighter.isSelected());

        preferencesMenu.add(cbLegalMoveHighlighter);

        return preferencesMenu;
    }

    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");

        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionsMenu.add(setupGameMenuItem);

        final JMenuItem setupTimeControls = new JMenuItem("Time controls");
        setupTimeControls.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getTimeControlSetup().promptUser();
                Table.get().timeControlSetupUpdate(Table.get().getTimeControlSetup());
            }
        });
        optionsMenu.add(setupTimeControls);

        return optionsMenu;
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

        void setTileDarkColor(final Board board,
                              final Color darkColor) {
            for (final TilePanel boardTile : boardTiles) {
                boardTile.setDarkTileColor(darkColor);
            }
            drawBoard(board);
        }

        void setTileLightColor(final Board board,
                               final Color lightColor) {
            for (final TilePanel boardTile : boardTiles) {
                boardTile.setLightTileColor(lightColor);
            }
            drawBoard(board);
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
                        sourcePiece = null;
                        humanMovePiece = null;
                    } else if (isLeftMouseButton(e)) {
                        // select square if piece is on that square
                        if (sourcePiece == null) {
                            // first click
                            sourcePiece = chessBoard.getPiece(tileID);
                            humanMovePiece = sourcePiece;
                            // if empty tile clicked,
                            if (humanMovePiece == null) {
                                sourcePiece = null;
                            }
                        } else {
                            // second click
                            final Move move = MoveFactory.createMove(chessBoard, sourcePiece.getPiecePosition(), tileID);
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getToBoard();
                                moveLog.addMove(move);
                            }
                            sourcePiece = null;
                            humanMovePiece = null;
                        }
                        SwingUtilities.invokeLater(() -> {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);

                                if (gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);
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
            if (board.getPiece(this.tileID) != null){
                try {
                    // example WB => white Bishop, BB=> black bishop
                    final String pieceString = board.getPiece(this.tileID).getPieceAlliance().toString().charAt(0) + "" +
                            board.getPiece(this.tileID).toString();
                    final BufferedImage image = ImageIO.read(
                            new File(pieceIconPath + pieceString + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void setLightTileColor(final Color color) {
            lightTileColor = color;
        }

        void setDarkTileColor(final Color color) {
            darkTileColor = color;
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
