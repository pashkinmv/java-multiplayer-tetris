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
import ru.pashkin.jmt.TerminalController;
import ru.pashkin.jmt.controller.MultiplayerGameController;
import ru.pashkin.jmt.controller.OnePlayerKeyListener;
import ru.pashkin.jmt.model.TetrisModel;
import ru.pashkin.jmt.network.NetworkManager;
import ru.pashkin.jmt.view.components.DownShifter;
import ru.pashkin.jmt.view.components.TetrisView;

public class MultiplayerGameTerminal extends GameTerminal {

    private TetrisModel tetrisModel = new TetrisModel(20, 18, true);
    private TetrisView tetrisView = new TetrisView(tetrisModel);
    private DownShifter downShifter = new DownShifter(tetrisView);
    private TetrisModel player1PreviewerModel = new TetrisModel(10, 4, false);
    private TetrisModel player2PreviewerModel = new TetrisModel(10, 4, false);
    private TetrisView player1PreviewerView = new TetrisView(player1PreviewerModel);
    private TetrisView player2PreviewerView = new TetrisView(player2PreviewerModel);
    private final ScoresViewer player1ScoresViewer = new ScoresViewer();
    private final ScoresViewer player2ScoresViewer = new ScoresViewer();
    private MultiplayerGameController gameController = new MultiplayerGameController();
    private NetworkManager networkManager;
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

    public MultiplayerGameTerminal(NetworkManager networkManager, TerminalController terminalController) {
        this.networkManager = networkManager;

        gameController.setTetrisView(tetrisView);
        gameController.setTetrisModel(tetrisModel);
        gameController.setDownShifter(downShifter);
        gameController.setPlayer1PreviewerView(player1PreviewerView);
        gameController.setPlayer1PreviewerModel(player1PreviewerModel);
        gameController.setPlayer1ScoresViewer(player1ScoresViewer);
        gameController.setPlayer2PreviewerView(player2PreviewerView);
        gameController.setPlayer2PreviewerModel(player2PreviewerModel);
        gameController.setPlayer2ScoresViewer(player2ScoresViewer);
        gameController.setNetworkManager(networkManager);
        gameController.setTerminalController(terminalController);

        initGui();
    }

    @Override
    public void startGame() {
        if (networkManager.isServer()) {
            gameController.startGame();
        }
    }

    @Override
    public void destroyTerminal() {
        gameController.stopPlayer1GameTimer();
        gameController.stopPlayer2GameTimer();
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

}
