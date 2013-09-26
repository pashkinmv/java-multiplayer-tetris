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
import ru.pashkin.jmt.controller.OnePlayerGameController;
import ru.pashkin.jmt.controller.OnePlayerKeyListener;
import ru.pashkin.jmt.model.TetrisModel;
import ru.pashkin.jmt.view.components.DownShifter;
import ru.pashkin.jmt.view.components.TetrisView;

public class OnePlayerGameTerminal extends GameTerminal {

    private TetrisModel tetrisModel = new TetrisModel(10, 18, false);
    private TetrisView tetrisView = new TetrisView(tetrisModel);
    private DownShifter downShifter = new DownShifter(tetrisView);
    private TetrisModel previewerModel = new TetrisModel(10, 4, false);
    private TetrisView previewerView = new TetrisView(previewerModel);
    private final ScoresViewer scoresViewer = new ScoresViewer();
    private OnePlayerGameController gameController =
            new OnePlayerGameController(tetrisView, tetrisModel, downShifter, previewerView, previewerModel, scoresViewer);
    private final KeyListener keyListener = new OnePlayerKeyListener(gameController);
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

    public OnePlayerGameTerminal() {
        setLayout(new BorderLayout());

        initGui();
    }

    private void initGui() {
        final JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(10, 30));

        final Box infoPanel = Box.createVerticalBox();
        infoPanel.add(previewerView);
        infoPanel.add(emptyPanel);
        infoPanel.add(scoresViewer);

        final JPanel wrapperPanel = new JPanel();
        wrapperPanel.add(infoPanel);

        final FlowLayout shiftPanelLayout = new FlowLayout(FlowLayout.CENTER);
        shiftPanelLayout.setVgap(10);
        final JPanel downShifterPanel = new JPanel(shiftPanelLayout);
        downShifterPanel.add(downShifter);

        add(downShifterPanel, BorderLayout.WEST);
        add(wrapperPanel, BorderLayout.EAST);
    }

    @Override
    public void startGame() {
        gameController.startGame();
    }

    @Override
    public void destroyTerminal() {
        gameController.stopGameTimer();
    }
}
