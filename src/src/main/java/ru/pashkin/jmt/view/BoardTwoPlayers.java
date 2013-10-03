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

import ru.pashkin.jmt.model.TetrisModel;
import ru.pashkin.jmt.view.components.DownShifter;
import ru.pashkin.jmt.view.components.TetrisView;

import javax.swing.*;
import java.awt.*;

public class BoardTwoPlayers extends JPanel {

    private TetrisModel tetrisModel = new TetrisModel(20, 18);
    private TetrisView tetrisView = new TetrisView(tetrisModel);
    private DownShifter downShifter = new DownShifter(tetrisView);
    private TetrisModel player1PreviewerModel = new TetrisModel(10, 4);
    private TetrisModel player2PreviewerModel = new TetrisModel(10, 4);
    private TetrisView player1PreviewerView = new TetrisView(player1PreviewerModel);
    private TetrisView player2PreviewerView = new TetrisView(player2PreviewerModel);
    private final ScoresViewer player1ScoresViewer = new ScoresViewer();
    private final ScoresViewer player2ScoresViewer = new ScoresViewer();

    public BoardTwoPlayers() {
        initGui();
    }

    private void initGui() {
        setLayout(new BorderLayout());

        final FlowLayout shiftPanelLayout = new FlowLayout(FlowLayout.CENTER);
        shiftPanelLayout.setVgap(10);
        final JPanel downShifterPanel = new JPanel(shiftPanelLayout);
        downShifterPanel.add(downShifter);

        add(downShifterPanel, BorderLayout.CENTER);
        add(composeRightPanel(), BorderLayout.EAST);
        add(composeLeftPanel(), BorderLayout.WEST);
    }

    private JPanel composeRightPanel() {
        final JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(10, 30));

        final Box infoPanel = Box.createVerticalBox();
        infoPanel.add(player2PreviewerView);
        infoPanel.add(emptyPanel);
        infoPanel.add(player2ScoresViewer);

        final JPanel wrapperPanel = new JPanel();
        wrapperPanel.add(infoPanel);

        return wrapperPanel;
    }

    private JPanel composeLeftPanel() {
        final JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(10, 30));

        final Box infoPanel = Box.createVerticalBox();
        infoPanel.add(player1PreviewerView);
        infoPanel.add(emptyPanel);
        infoPanel.add(player1ScoresViewer);

        final JPanel wrapperPanel = new JPanel();
        wrapperPanel.add(infoPanel);

        return wrapperPanel;
    }

    public TetrisModel getTetrisModel() {
        return tetrisModel;
    }

    public TetrisModel getPlayer1PreviewerModel() {
        return player1PreviewerModel;
    }

    public DownShifter getDownShifter() {
        return downShifter;
    }

    public ScoresViewer getPlayer1ScoresViewer() {
        return player1ScoresViewer;
    }

    public ScoresViewer getPlayer2ScoresViewer() {
        return player2ScoresViewer;
    }

    public TetrisModel getPlayer2PreviewerModel() {
        return player2PreviewerModel;
    }

    public TetrisView getPlayer1PreviewerView() {
        return player1PreviewerView;
    }

    public TetrisView getPlayer2PreviewerView() {
        return player2PreviewerView;
    }

    public TetrisView getTetrisView() {
        return tetrisView;
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
