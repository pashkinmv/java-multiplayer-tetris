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

package ru.pashkin.jmt.controller;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import ru.pashkin.jmt.TerminalController;
import ru.pashkin.jmt.controller.multiplayercommands.*;
import ru.pashkin.jmt.model.AbstractFigure;
import ru.pashkin.jmt.model.TetrisModel;
import ru.pashkin.jmt.network.NetworkManager;
import ru.pashkin.jmt.utils.StringUtils;
import ru.pashkin.jmt.view.ScoresViewer;
import ru.pashkin.jmt.view.components.DownShifter;
import ru.pashkin.jmt.view.components.TetrisView;

public class MultiplayerGameController implements GameController {

    private int[] LEVELS = new int[]{200, 600, 1200, 2000, 3000, 4200, 5600, 7200, 9000, 11000, 13200};
    private int SPEED_LOWEST = 5000;
    private int SPEED_FASTEST = 90;
    private int currentScores;
    private int currentLevel;
    private int currentSpeed;
    private boolean gameStarted;
    private boolean gamePaused;
    private TetrisModel tetrisModel;
    private TetrisView tetrisView;
    private DownShifter downShifter;
    private TetrisView player1PreviewerView;
    private TetrisModel player1PreviewerModel;
    private ScoresViewer player1ScoresViewer;
    private TetrisView player2PreviewerView;
    private TetrisModel player2PreviewerModel;
    private ScoresViewer player2ScoresViewer;
    private Timer borderTimer;
    private Timer player1GameTimer;
    private Timer player2GameTimer;
    private NetworkManager networkManager;
    private TerminalController terminalController;
    /*
     * Command list
     */
    private final PushLeftCommand pushLeftCommand = new PushLeftCommand(this);
    private final PushRightCommand pushRightCommand = new PushRightCommand(this);
    private final PushDownCommand pushDownCommand = new PushDownCommand(this);
    private final TurnClockwiseCommand turnClockwiseCommand = new TurnClockwiseCommand(this);
    private final TurnCounterClockwiseCommand turnCounterClockwiseCommand = new TurnCounterClockwiseCommand(this);
    private final UpdateTetrisModelCommand updateTetrisModelCommand = new UpdateTetrisModelCommand(this);
    private final UpdatePlayer1PreviewerModelCommand updatePlayer1PreviewerModelCommand = new UpdatePlayer1PreviewerModelCommand(this);
    private final UpdatePlayer2PreviewerModelCommand updatePlayer2PreviewerModelCommand = new UpdatePlayer2PreviewerModelCommand(this);
    private final StartGameCommand startGameCommand = new StartGameCommand(this);
    private final PauseGameCommand pauseGameCommand = new PauseGameCommand(this);
    private final ResumeGameCommand resumeGameCommand = new ResumeGameCommand(this);
    private final DoShiftCommand doShiftCommand = new DoShiftCommand(this);
    private final BlinkCommand blinkCommand = new BlinkCommand(this);
    private final UpdateScoresCommand updateScoresCommand = new UpdateScoresCommand(this);
    private final GameOverCommand gameOverCommand = new GameOverCommand(this);

    public MultiplayerGameController() {
    }

    @Override
    public synchronized void player1PushLeft() {
        if (networkManager.isServer()) {
            if (tetrisModel.getFigure1().pushLeft()) {
                repaintDownShifter();
            }
        } else {
            try {
                pushLeftCommand.sendCommand();
            } catch (IOException ex) {
                interraptGame(ex);
            }
        }
    }

    @Override
    public synchronized void player1PushRight() {
        try {
            if (networkManager.isServer()) {
                if (tetrisModel.getFigure1().pushRight()) {
                    repaintDownShifter();
                }
            } else {
                pushRightCommand.sendCommand();
            }
        } catch (IOException ex) {
            interraptGame(ex);
        }
    }

    @Override
    public synchronized void player1TurnClockwise() {
        try {
            if (networkManager.isServer()) {
                if (!tetrisModel.getFigure1().turnClockwise()) {
                    blink();
                }

                repaintDownShifter();
            } else {
                turnClockwiseCommand.sendCommand();
            }
        } catch (IOException ex) {
            interraptGame(ex);
        }
    }

    @Override
    public synchronized void player1TurnCounterClockwise() {
        try {
            if (networkManager.isServer()) {
                if (!tetrisModel.getFigure1().turnCounterClockwise()) {
                    blink();
                }

                repaintDownShifter();
            } else {
                turnCounterClockwiseCommand.sendCommand();
            }
        } catch (IOException ex) {
            interraptGame(ex);
        }
    }

    @Override
    public synchronized void player1PushDown() {
        if (gamePaused || !gameStarted) {
            return;
        }
        try {
            if (networkManager.isServer()) {
                restartPlayer1GameTimer();
                if (tetrisModel.getFigure1().pushDown() == AbstractFigure.RESULT_FREEZED) {
                    countScores(tetrisModel.removeRows());
                    tetrisModel.getFigureManager1().pregenerateFigure();
                    /*
                     * Put preview figure
                     */
                    player1PreviewerModel.clear();
                    tetrisModel.getFigureManager1().getNextFigure().putFigure(player1PreviewerModel, 4, 0);
                    repaintPlayer1Previewer();
                    /*
                     * Put new figure
                     */
                    final AbstractFigure figure = tetrisModel.getFigureManager1().getCurrentFigure();
                    tetrisModel.setFigure1(figure);
                    if (!figure.putFigure(tetrisModel, 4, 0)) {
                        gameOver();
                    } else {
                        downShifter.doShift();
                        doShiftCommand.sendCommand();
                    }
                }

                countScores(1);
                repaintDownShifter();
            } else {
                pushDownCommand.sendCommand();
            }
        } catch (IOException ex) {
            interraptGame(ex);
        }
    }

    @Override
    public boolean gameStarted() {
        return gameStarted;
    }

    @Override
    public boolean gamePaused() {
        return gamePaused;
    }

    @Override
    public synchronized void startGame() {
        if (networkManager.isServer()) {
            try {
                stopPlayer1GameTimer();
                stopPlayer2GameTimer();

                startGameCommand.sendCommand();

                player1ScoresViewer.setScores(0);
                player1ScoresViewer.setLevel(1);
                player1ScoresViewer.setNextLevel(LEVELS[0]);
                player2ScoresViewer.setScores(0);
                player2ScoresViewer.setLevel(1);
                player2ScoresViewer.setNextLevel(LEVELS[0]);
                currentScores = 0;
                currentLevel = 1;
                currentSpeed = SPEED_LOWEST;
                tetrisModel.clear();

                tetrisModel.getFigureManager1().pregenerateFigure();
                player1PreviewerModel.clear();
                tetrisModel.getFigureManager1().getNextFigure().putFigure(player1PreviewerModel, 4, 0);
                final AbstractFigure figure1 = tetrisModel.getFigureManager1().getCurrentFigure();
                tetrisModel.setFigure1(figure1);
                figure1.putFigure(tetrisModel, 4, 0);

                tetrisModel.getFigureManager2().pregenerateFigure();
                player2PreviewerModel.clear();
                tetrisModel.getFigureManager2().getNextFigure().putFigure(player2PreviewerModel, 4, 0);
                final AbstractFigure figure2 = tetrisModel.getFigureManager2().getCurrentFigure();
                tetrisModel.setFigure2(figure2);
                figure2.putFigure(tetrisModel, 14, 0);

                gameStarted = true;
                gamePaused = false;

                restartPlayer1GameTimer();
                restartPlayer2GameTimer();

                repaintPlayer1Previewer();
                repaintPlayer2Previewer();
                repaintDownShifter();
            } catch (IOException ex) {
                interraptGame(ex);
            }
        } else {
            player1ScoresViewer.setScores(0);
            player1ScoresViewer.setLevel(1);
            player1ScoresViewer.setNextLevel(LEVELS[0]);
            player2ScoresViewer.setScores(0);
            player2ScoresViewer.setLevel(1);
            player2ScoresViewer.setNextLevel(LEVELS[0]);

            gameStarted = true;
            gamePaused = false;
        }
    }

    @Override
    public synchronized void pauseGame() {
        if (gameStarted && !gamePaused) {
            if (networkManager.isServer()) {
                try {
                    stopPlayer1GameTimer();
                    stopPlayer2GameTimer();
                    pauseGameCommand.sendCommand();
                    gamePaused = true;
                    tetrisModel.setMessage("pause");
                    repaintDownShifter();
                } catch (IOException ex) {
                    interraptGame(ex);
                }
            } else {
                gamePaused = true;
                tetrisModel.setMessage("pause");
            }
        }
    }

    @Override
    public synchronized void resumeGame() {
        if (gameStarted && gamePaused) {
            if (networkManager.isServer()) {
                try {
                    restartPlayer1GameTimer();
                    restartPlayer2GameTimer();
                    resumeGameCommand.sendCommand();
                    gamePaused = false;
                    tetrisModel.setMessage(null);
                    repaintDownShifter();
                } catch (IOException ex) {
                    interraptGame(ex);
                }
            } else {
                gamePaused = false;
                tetrisModel.setMessage(null);
            }
        }
    }

    public synchronized void player2PushLeft() {
        if (tetrisModel.getFigure2().pushLeft()) {
            repaintDownShifter();
        }
    }

    public synchronized void player2PushRight() {
        if (tetrisModel.getFigure2().pushRight()) {
            repaintDownShifter();
        }
    }

    public synchronized void player2TurnClockwise() {
        if (!tetrisModel.getFigure2().turnClockwise()) {
            blink();
        }

        repaintDownShifter();
    }

    public synchronized void player2TurnCounterClockwise() {
        if (!tetrisModel.getFigure2().turnCounterClockwise()) {
            blink();
        }

        repaintDownShifter();
    }

    public synchronized void player2PushDown() {
        if (gamePaused || !gameStarted) {
            return;
        }

        try {
            if (networkManager.isServer()) {
                restartPlayer2GameTimer();
                if (tetrisModel.getFigure2().pushDown() == AbstractFigure.RESULT_FREEZED) {
                    countScores(tetrisModel.removeRows());
                    tetrisModel.getFigureManager2().pregenerateFigure();
                    /*
                     * Put preview figure
                     */
                    player2PreviewerModel.clear();
                    tetrisModel.getFigureManager2().getNextFigure().putFigure(player2PreviewerModel, 4, 0);
                    repaintPlayer2Previewer();
                    /*
                     * Put new figure
                     */
                    final AbstractFigure figure = tetrisModel.getFigureManager2().getCurrentFigure();
                    tetrisModel.setFigure2(figure);
                    if (!figure.putFigure(tetrisModel, 14, 0)) {
                        gameOver();
                    } else {
                        downShifter.doShift();
                        doShiftCommand.sendCommand();
                    }
                }
            }

            countScores(1);
            repaintDownShifter();
        } catch (IOException ex) {
            interraptGame(ex);
        }
    }

    public synchronized void stopPlayer1GameTimer() {
        if (player1GameTimer != null) {
            player1GameTimer.cancel();
            player1GameTimer.purge();
            player1GameTimer = null;
        }
    }

    public synchronized void stopPlayer2GameTimer() {
        if (player2GameTimer != null) {
            player2GameTimer.cancel();
            player2GameTimer.purge();
            player2GameTimer = null;
        }
    }

    public void setDownShifter(DownShifter downShifter) {
        this.downShifter = downShifter;
    }

    public void setPlayer1PreviewerModel(TetrisModel player1PreviewerModel) {
        this.player1PreviewerModel = player1PreviewerModel;
    }

    public void setPlayer1PreviewerView(TetrisView player1PreviewerView) {
        this.player1PreviewerView = player1PreviewerView;
    }

    public void setPlayer2PreviewerModel(TetrisModel player2PreviewerModel) {
        this.player2PreviewerModel = player2PreviewerModel;
    }

    public void setPlayer2PreviewerView(TetrisView player2PreviewerView) {
        this.player2PreviewerView = player2PreviewerView;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;

        networkManager.addCommand(pushLeftCommand);
        networkManager.addCommand(pushRightCommand);
        networkManager.addCommand(pushDownCommand);
        networkManager.addCommand(turnClockwiseCommand);
        networkManager.addCommand(turnCounterClockwiseCommand);
        networkManager.addCommand(updateTetrisModelCommand);
        networkManager.addCommand(updatePlayer1PreviewerModelCommand);
        networkManager.addCommand(updatePlayer2PreviewerModelCommand);
        networkManager.addCommand(startGameCommand);
        networkManager.addCommand(pauseGameCommand);
        networkManager.addCommand(resumeGameCommand);
        networkManager.addCommand(doShiftCommand);
        networkManager.addCommand(blinkCommand);
        networkManager.addCommand(updateScoresCommand);
        networkManager.addCommand(gameOverCommand);
    }

    public void setTerminalController(TerminalController terminalController) {
        this.terminalController = terminalController;
    }

    public TetrisModel getTetrisModel() {
        return tetrisModel;
    }

    public TetrisModel getPlayer1PreviewerModel() {
        return player1PreviewerModel;
    }

    public TetrisModel getPlayer2PreviewerModel() {
        return player2PreviewerModel;
    }

    public void setTetrisModel(TetrisModel tetrisModel) {
        this.tetrisModel = tetrisModel;
    }

    public void setTetrisView(TetrisView tetrisView) {
        this.tetrisView = tetrisView;
    }

    public ScoresViewer getPlayer1ScoresViewer() {
        return player1ScoresViewer;
    }

    public void setPlayer1ScoresViewer(ScoresViewer player1ScoresViewer) {
        this.player1ScoresViewer = player1ScoresViewer;
    }

    public ScoresViewer getPlayer2ScoresViewer() {
        return player2ScoresViewer;
    }

    public void setPlayer2ScoresViewer(ScoresViewer player2ScoresViewer) {
        this.player2ScoresViewer = player2ScoresViewer;
    }

    public void doShift() {
        downShifter.doShift();
    }

    public void gameOver() {
        try {
            if (networkManager.isServer()) {
                stopPlayer1GameTimer();
                stopPlayer2GameTimer();
                gameOverCommand.sendCommand();
            }

            gameStarted = false;
            tetrisModel.setMessage("Game over");
        } catch (IOException e) {
            interraptGame(e);
        }
    }

    private void interraptGame(Exception e) {
        if (networkManager.isServer()) {
            stopPlayer1GameTimer();
            stopPlayer2GameTimer();
        }

        gameStarted = false;
        terminalController.showMessage("Connection lost " + e.getMessage());
    }

    private void countScores(int result) throws IOException {
        if (!StringUtils.isEmpty(tetrisModel.getMessage())) {
            return;
        }

        currentScores += Math.pow(2, result - 1) * currentLevel;
        player1ScoresViewer.setScores(currentScores);
        player2ScoresViewer.setScores(currentScores);

        if (currentLevel != LEVELS.length + 2) {
            if (currentScores >= LEVELS[LEVELS.length - 1]) {
                currentLevel = LEVELS.length + 1;
                player1ScoresViewer.setLevel("Maximum");
                player1ScoresViewer.setNextLevel("");
                player2ScoresViewer.setLevel("Maximum");
                player2ScoresViewer.setNextLevel("");
                currentSpeed = SPEED_FASTEST;
            } else {
                for (int i = LEVELS.length - 1; i >= 0; i--) {
                    if (currentScores >= LEVELS[i]) {
                        if (currentLevel != i + 2) {
                            currentLevel = i + 2;
                            player1ScoresViewer.setLevel(currentLevel);
                            player1ScoresViewer.setNextLevel(LEVELS[i + 1]);
                            player2ScoresViewer.setLevel(currentLevel);
                            player2ScoresViewer.setNextLevel(LEVELS[i + 1]);
                            currentSpeed = SPEED_LOWEST - (currentLevel - 1) * (SPEED_LOWEST - SPEED_FASTEST) / LEVELS.length;
                        }
                        break;
                    }
                }
            }
        }

        updateScoresCommand.sendCommand();
    }

    public synchronized void repaintPlayer1Previewer() {
        if (networkManager.isServer()) {
            try {
                updatePlayer1PreviewerModelCommand.sendCommand();
            } catch (IOException ex) {
                interraptGame(ex);
            }
        }

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                player1PreviewerView.repaint();
            }
        });
    }

    public synchronized void repaintPlayer2Previewer() {
        if (networkManager.isServer()) {
            try {
                updatePlayer2PreviewerModelCommand.sendCommand();
            } catch (IOException ex) {
                interraptGame(ex);
            }
        }

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                player2PreviewerView.repaint();
            }
        });
    }

    public synchronized void repaintDownShifter() {
        if (networkManager.isServer()) {
            try {
                updateTetrisModelCommand.sendCommand();
            } catch (IOException ex) {
                interraptGame(ex);
            }
        }

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                downShifter.repaint();
            }
        });
    }

    public void blink() {
        try {
            if (networkManager.isServer()) {
                blinkCommand.sendCommand();
            }

            tetrisView.setBorderColor(Color.RED);
            tetrisView.setBackgroundColor(Color.RED);
            restartBorderTimer();
        } catch (IOException ex) {
            interraptGame(ex);
        }
    }

    private void restartBorderTimer() {
        if (borderTimer != null) {
            borderTimer.cancel();
            borderTimer.purge();
        }

        borderTimer = new Timer(true);

        borderTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                tetrisView.setBorderColor(Color.BLACK);
                tetrisView.setBackgroundColor(Color.WHITE);

                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        downShifter.repaint();
                    }
                });
            }
        }, 100);
    }

    private void restartPlayer1GameTimer() {
        stopPlayer1GameTimer();

        player1GameTimer = new Timer(true);
        player1GameTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                synchronized (MultiplayerGameController.this) {
                    if (gamePaused || !gameStarted) {
                        return;
                    }

                    try {
                        if (tetrisModel.getFigure1().pushDown() == AbstractFigure.RESULT_FREEZED) {
                            countScores(tetrisModel.removeRows());
                            tetrisModel.getFigureManager1().pregenerateFigure();
                            /*
                             * Put preview figure
                             */
                            player1PreviewerModel.clear();
                            tetrisModel.getFigureManager1().getNextFigure().putFigure(player1PreviewerModel, 4, 0);
                            repaintPlayer1Previewer();
                            /*
                             * Put new figure
                             */
                            final AbstractFigure figure = tetrisModel.getFigureManager1().getCurrentFigure();
                            tetrisModel.setFigure1(figure);
                            if (!figure.putFigure(tetrisModel, 4, 0)) {
                                gameOver();
                            } else {
                                downShifter.doShift();
                                doShiftCommand.sendCommand();
                            }
                        }

                        repaintDownShifter();
                    } catch (IOException e) {
                        interraptGame(e);
                    }
                }
            }
        }, currentSpeed, currentSpeed);
    }

    private void restartPlayer2GameTimer() {
        stopPlayer2GameTimer();

        player2GameTimer = new Timer(true);
        player2GameTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                synchronized (MultiplayerGameController.this) {
                    if (gamePaused || !gameStarted) {
                        return;
                    }

                    try {
                        if (tetrisModel.getFigure2().pushDown() == AbstractFigure.RESULT_FREEZED) {
                            countScores(tetrisModel.removeRows());
                            tetrisModel.getFigureManager2().pregenerateFigure();
                            /*
                             * Put preview figure
                             */
                            player2PreviewerModel.clear();
                            tetrisModel.getFigureManager2().getNextFigure().putFigure(player2PreviewerModel, 4, 0);
                            repaintPlayer2Previewer();
                            /*
                             * Put new figure
                             */
                            final AbstractFigure figure = tetrisModel.getFigureManager2().getCurrentFigure();
                            tetrisModel.setFigure2(figure);
                            if (!figure.putFigure(tetrisModel, 14, 0)) {
                                gameOver();
                            } else {
                                downShifter.doShift();
                                doShiftCommand.sendCommand();
                            }
                        }

                        repaintDownShifter();
                    } catch (IOException e) {
                        interraptGame(e);
                    }
                }
            }
        }, currentSpeed, currentSpeed);
    }
}
