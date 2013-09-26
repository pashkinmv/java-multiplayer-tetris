/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 pashkinmv
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ru.pashkin.jmt.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import javax.swing.Box;
import javax.swing.JPanel;
import ru.pashkin.jmt.controller.TwoPlayerGameController;
import ru.pashkin.jmt.controller.TwoPlayerKeyListener;
import ru.pashkin.jmt.model.TetrisModel;
import ru.pashkin.jmt.view.components.DownShifter;
import ru.pashkin.jmt.view.components.TetrisView;

public class TwoPlayerGameTerminal extends GameTerminal {

    private TetrisModel tetrisModel = new TetrisModel(20, 18, true);
    private TetrisView tetrisView = new TetrisView(tetrisModel);
    private DownShifter downShifter = new DownShifter(tetrisView);
    private TetrisModel leftPreviewerModel = new TetrisModel(10, 4, false);
    private TetrisView leftPreviewerView = new TetrisView(leftPreviewerModel);
    private final ScoresViewer leftScoresViewer = new ScoresViewer();
    private TetrisModel rightPreviewerModel = new TetrisModel(10, 4, false);
    private TetrisView rightPreviewerView = new TetrisView(rightPreviewerModel);
    private final ScoresViewer rightScoresViewer = new ScoresViewer();
    private TwoPlayerGameController gameController =
            new TwoPlayerGameController(tetrisView, tetrisModel, downShifter, leftPreviewerView, leftPreviewerModel,
                    leftScoresViewer, rightPreviewerView, rightPreviewerModel, rightScoresViewer);
    private final KeyListener keyListener = new TwoPlayerKeyListener(gameController);
    private final FocusAdapter focusListener = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            gameController.pauseGame();
        }
    };

    @Override
    public KeyListener getKeyListener() {
        return keyListener;
    }

    @Override
    public FocusListener getFocusListener() {
        return focusListener;
    }

    public TwoPlayerGameTerminal() {
        setLayout(new BorderLayout());

        initGui();
    }

    private void initGui() {
        final FlowLayout shiftPanelLayout = new FlowLayout(FlowLayout.CENTER);
        shiftPanelLayout.setVgap(10);
        final JPanel downShifterPanel = new JPanel(shiftPanelLayout);
        downShifterPanel.add(downShifter);

        add(downShifterPanel, BorderLayout.CENTER);
        add(getRightPanel(), BorderLayout.EAST);
        add(getLeftPanel(), BorderLayout.WEST);
    }

    private JPanel getRightPanel() {
        final JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(10, 30));

        final Box infoPanel = Box.createVerticalBox();
        infoPanel.add(rightPreviewerView);
        infoPanel.add(emptyPanel);
        infoPanel.add(rightScoresViewer);

        final JPanel wrapperPanel = new JPanel();
        wrapperPanel.add(infoPanel);

        return wrapperPanel;
    }

    private JPanel getLeftPanel() {
        final JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(10, 30));

        final Box infoPanel = Box.createVerticalBox();
        infoPanel.add(leftPreviewerView);
        infoPanel.add(emptyPanel);
        infoPanel.add(leftScoresViewer);

        final JPanel wrapperPanel = new JPanel();
        wrapperPanel.add(infoPanel);

        return wrapperPanel;
    }

    @Override
    public void startGame() {
        gameController.startGame();
    }

    @Override
    public void destroyTerminal() {
        gameController.stopPlayer1GameTimer();
        gameController.stopPlayer2GameTimer();
    }
}
