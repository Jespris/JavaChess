package com.chess.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimeControlSetup extends JDialog {
    private final JSlider minuteSlider;
    private final JSlider incrementSlider;

    private static final String MINUTE_TEXT = "Minutes per side: ";
    private static final String INCREMENT_TEXT = "Increment in seconds: ";

    TimeControlSetup(final JFrame frame, final boolean modal){
        super(frame, modal);
        final JPanel myPanel = new JPanel(new GridLayout(0, 1));
        getContentPane().add(myPanel);
        this.minuteSlider = addLabeledSlider(
                myPanel,
                new JSlider(JSlider.HORIZONTAL, 0, 30, 10),
                MINUTE_TEXT);
        this.incrementSlider = addLabeledSlider(
                myPanel,
                new JSlider(JSlider.HORIZONTAL, 0, 30, 5),
                INCREMENT_TEXT);

        final JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("New time controls: " + getMinutesPerSide() + " + " + getIncrement());
                TimeControlSetup.this.setVisible(false);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel");
                TimeControlSetup.this.setVisible(false);
            }
        });

        myPanel.add(cancelButton);
        myPanel.add(okButton);

        setLocationRelativeTo(frame);
        pack();
        setVisible(false);
    }

    void promptUser(){
        setVisible(true);
        repaint();
    }

    int getMinutesPerSide(){
        return (Integer)this.minuteSlider.getValue();
    }

    int getIncrement(){
        return (Integer)this.incrementSlider.getValue();
    }

    private static JSlider addLabeledSlider(final Container c, final JSlider slider, final String text){
        final JLabel l = new JLabel(text);
        c.add(l);
        l.setLabelFor(slider);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        c.add(slider);
        return slider;
    }
}
