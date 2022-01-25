package com.chess.gui;

import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.Move;
import com.chess.gui.Table.MoveLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryPanel extends JPanel {

    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

    GameHistoryPanel(){
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final MoveLog moveLog){
         int currentRow = 0;
         this.model.clear();
         for (final Move move : moveLog.getMoves()){
             final String moveText = move.toString();
             if (move.getPieceMoved().getPieceAlliance().isWhite()){
                 this.model.setValueAt(moveText, currentRow, 0);
             } else if (move.getPieceMoved().getPieceAlliance().isBlack()){
                 this.model.setValueAt(moveText, currentRow, 1);
                 currentRow++;
             }
         }
         if (moveLog.getMoves().size() > 0){
             final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
             final String moveText = lastMove.toString();
             if (lastMove.getPieceMoved().getPieceAlliance().isWhite()){
                 this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
             } else if (lastMove.getPieceMoved().getPieceAlliance().isBlack()){
                 this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
             }
         }
         final JScrollBar vertical = scrollPane.getVerticalScrollBar();
         // auto scroll down if move history log is full
         vertical.setValue(vertical.getMaximum());
    }

    private String calculateCheckAndCheckMateHash(final Board board) {
        if (board.currentPlayer().isInCheckMate()){
            return "#";
        } else if (board.currentPlayer().isInCheck()){
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel {
        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel(){
            this.values = new ArrayList<>();
        }

        public void clear(){
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount(){
            if (this.values == null){
                return 0;
            }
            return this.values.size();
        }
        @Override
        public int getColumnCount(){
            return NAMES.length;
        }
        @Override
        public Object getValueAt(final int row, final int col){
            final Row currentRow = this.values.get(row);
            if (col == 0){
                return currentRow.getWhiteMove();
            } else if (col == 1){
                return currentRow.getBlackMove();
            }
            return null;
        }
        @Override
        public void setValueAt(final Object Value, final int row, final int col){
            final Row currentRow;

            if (this.values.size() <= row){
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }

            if (col == 0){
                currentRow.setWhiteMove((String)Value);
                // add a new row
                fireTableRowsInserted(row, row);
            } else if (col == 1){
                currentRow.setBlackMove((String)Value);
                fireTableCellUpdated(row, col);
            }
        }
        @Override
        public Class<?> getColumnClass(final int col){
            return Move.class;
        }
        @Override
        public String getColumnName(final int col){
            return NAMES[col];
        }
    }

    private static class Row {

        private String whiteMove;
        private String blackMove;

        Row() {

        }

        public String getWhiteMove(){
            return this.whiteMove;
        }

        public String getBlackMove(){
            return this.blackMove;
        }

        public void setWhiteMove(final String move){
            this.whiteMove = move;
        }

        public void setBlackMove(final String move){
            this.blackMove = move;
        }

    }
}
