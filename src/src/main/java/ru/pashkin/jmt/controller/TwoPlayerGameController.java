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
import java.util.Timer;
import java.util.TimerTask;
import ru.pashkin.jmt.model.AbstractFigure;
import ru.pashkin.jmt.model.TetrisModel;
import ru.pashkin.jmt.view.ScoresViewer;
import ru.pashkin.jmt.view.components.DownShifter;
import ru.pashkin.jmt.view.components.TetrisView;

public class TwoPlayerGameController {

    private int[] LEVELS = new int[]{200, 600, 1200, 2000, 3000, 4200, 5600, 7200, 9000, 11000, 13200};
    private int SPEED_LOWEST = 800;
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
    private final ScoresViewer player1ScoresViewer;
    private TetrisView player2PreviewerView;
    private TetrisModel player2PreviewerModel;
    private final ScoresViewer player2ScoresViewer;
    private Timer borderTimer;
    private Timer player1GameTimer;
    private Timer player2GameTimer;

    public TwoPlayerGameController(TetrisView tetrisView, TetrisModel tetrisModel, DownShifter downShifter,
            TetrisView player1PreviewerView, TetrisModel player1PreviewerModel, ScoresViewer player1ScoresViewer,
            TetrisView player2PreviewerView, TetrisModel player2PreviewerModel, ScoresViewer player2ScoresViewer) {
        this.tetrisView = tetrisView;
        this.tetrisModel = tetrisModel;
        this.downShifter = downShifter;
        this.player1PreviewerView = player1PreviewerView;
        this.player1PreviewerModel = player1PreviewerModel;
        this.player1ScoresViewer = player1ScoresViewer;
        this.player2PreviewerView = player2PreviewerView;
        this.player2PreviewerModel = player2PreviewerModel;
        this.player2ScoresViewer = player2ScoresViewer;
    }
    
    public synchronized void player1PushLeft() {
        if (tetrisModel.getFigure1().pushLeft()) {
            repaintDownShifter();
        }
    }
    
    public synchronized void player2PushLeft() {
        if (tetrisModel.getFigure2().pushLeft()) {
            repaintDownShifter();
        }
    }

    public synchronized void player1PushRight() {
        if (tetrisModel.getFigure1().pushRight()) {
            repaintDownShifter();
        }
    }
    
    public synchronized void player2PushRight() {
        if (tetrisModel.getFigure2().pushRight()) {
            repaintDownShifter();
        }
    }

    public synchronized void player1TurnClockwise() {
        if (!tetrisModel.getFigure1().turnClockwise()) {
            blink();
        }

        repaintDownShifter();
    }

    public synchronized void player2TurnClockwise() {
        if (!tetrisModel.getFigure2().turnClockwise()) {
            blink();
        }

        repaintDownShifter();
    }

    public synchronized void player1TurnCounterClockwise() {
        if (!tetrisModel.getFigure1().turnCounterClockwise()) {
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

    public synchronized void player1PushDown() {
        if (gamePaused || !gameStarted) {
            return;
        }
        
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
            }
        }

        countScores(1);
        repaintDownShifter();
    }
    
    public synchronized void player2PushDown() {
        if (gamePaused || !gameStarted) {
            return;
        }
        
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
            }
        }

        countScores(1);
        repaintDownShifter();
    }

    public boolean gameStarted() {
        return gameStarted;
    }

    public boolean gamePaused() {
        return gamePaused;
    }

    public synchronized void startGame() {
        stopPlayer1GameTimer();
        stopPlayer2GameTimer();

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

        repaintDownShifter();
    }

    private void gameOver() {
        stopPlayer1GameTimer();
        stopPlayer2GameTimer();

        gameStarted = false;
        tetrisModel.setMessage("Game over");
    }

    public synchronized void pauseGame() {
        if (gameStarted && !gamePaused) {
            stopPlayer1GameTimer();
            stopPlayer2GameTimer();
            gamePaused = true;
            tetrisModel.setMessage("Press P to continue");
            repaintDownShifter();
        }
    }

    public synchronized void resumeGame() {
        if (gameStarted && gamePaused) {
            restartPlayer1GameTimer();
            restartPlayer2GameTimer();
            gamePaused = false;
            tetrisModel.setMessage(null);
            repaintDownShifter();
        }
    }

    private void countScores(int result) {
        if (tetrisModel.getMessage() != null) {
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
    }
    
    private void repaintPlayer1Previewer() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                player1PreviewerView.repaint();
            }

        });
    }
    
    private void repaintPlayer2Previewer() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                player2PreviewerView.repaint();
            }

        });
    }

    private void repaintDownShifter() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                downShifter.repaint();
            }

        });
    }

    private void blink() {
        tetrisView.setBorderColor(Color.RED);
        tetrisView.setBackgroundColor(Color.RED);
        restartBorderTimer();
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

    private void restartPlayer1GameTimer() {
        stopPlayer1GameTimer();

        player1GameTimer = new Timer(true);
        player1GameTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                synchronized (TwoPlayerGameController.this) {
                    if (gamePaused || !gameStarted) {
                        return;
                    }
                    
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
                        }
                    }

                    repaintDownShifter();
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
                synchronized (TwoPlayerGameController.this) {
                    if (gamePaused || !gameStarted) {
                        return;
                    }
                    
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
                        }
                    }

                    repaintDownShifter();
                }
            }

        }, currentSpeed, currentSpeed);
    }

}
