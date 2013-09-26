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

public class OnePlayerGameController implements GameController {

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
    private TetrisView previewerView;
    private TetrisModel previewerModel;
    private final ScoresViewer scoresViewer;
    private Timer borderTimer;
    private Timer gameTimer;

    public OnePlayerGameController(TetrisView tetrisView, TetrisModel tetrisModel, DownShifter downShifter,
            TetrisView previewerView, TetrisModel previewerModel, ScoresViewer scoresViewer) {
        this.tetrisView = tetrisView;
        this.tetrisModel = tetrisModel;
        this.downShifter = downShifter;
        this.previewerView = previewerView;
        this.previewerModel = previewerModel;
        this.scoresViewer = scoresViewer;
    }

    @Override
    public synchronized void player1PushLeft() {
        if (tetrisModel.getFigure1().pushLeft()) {
            repaintDownShifter();
        }
    }

    @Override
    public synchronized void player1PushRight() {
        if (tetrisModel.getFigure1().pushRight()) {
            repaintDownShifter();
        }
    }

    @Override
    public synchronized void player1TurnClockwise() {
        if (!tetrisModel.getFigure1().turnClockwise()) {
            blink();
        }

        repaintDownShifter();
    }

    @Override
    public synchronized void player1TurnCounterClockwise() {
        if (!tetrisModel.getFigure1().turnCounterClockwise()) {
            blink();
        }

        repaintDownShifter();
    }

    @Override
    public synchronized void player1PushDown() {
        if (gamePaused || !gameStarted) {
            return;
        }
        
        restartGameTimer();
        if (tetrisModel.getFigure1().pushDown() == AbstractFigure.RESULT_FREEZED) {
            countScores(tetrisModel.removeRows());
            tetrisModel.getFigureManager1().pregenerateFigure();
            /*
             * Put preview figure
             */
            previewerModel.clear();
            tetrisModel.getFigureManager1().getNextFigure().putFigure(previewerModel, 4, 0);
            repaintPreviewer();
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
        stopGameTimer();

        scoresViewer.setScores(0);
        scoresViewer.setLevel(1);
        scoresViewer.setNextLevel(LEVELS[0]);
        currentScores = 0;
        currentLevel = 1;
        currentSpeed = SPEED_LOWEST;
        tetrisModel.clear();
        
        tetrisModel.getFigureManager1().pregenerateFigure();
        previewerModel.clear();
        tetrisModel.getFigureManager1().getNextFigure().putFigure(previewerModel, 4, 0);
        final AbstractFigure figure = tetrisModel.getFigureManager1().getCurrentFigure();
        tetrisModel.setFigure1(figure);
        figure.putFigure(tetrisModel, 4, 0);
        
        gameStarted = true;
        gamePaused = false;

        restartGameTimer();

        repaintDownShifter();
    }

    private void gameOver() {
        stopGameTimer();

        gameStarted = false;
        tetrisModel.setMessage("Game over");
    }

    @Override
    public synchronized void pauseGame() {
        if (gameStarted && !gamePaused) {
            stopGameTimer();
            gamePaused = true;
            tetrisModel.setMessage("Press P to continue");
            repaintDownShifter();
        }
    }

    @Override
    public synchronized void resumeGame() {
        if (gameStarted && gamePaused) {
            restartGameTimer();
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
        scoresViewer.setScores(currentScores);

        if (currentLevel != LEVELS.length + 2) {
            if (currentScores >= LEVELS[LEVELS.length - 1]) {
                currentLevel = LEVELS.length + 1;
                scoresViewer.setLevel("Maximum");
                scoresViewer.setNextLevel("");
                currentSpeed = SPEED_FASTEST;
            } else {
                for (int i = LEVELS.length - 1; i >= 0; i--) {
                    if (currentScores >= LEVELS[i]) {
                        if (currentLevel != i + 2) {
                            currentLevel = i + 2;
                            scoresViewer.setLevel(currentLevel);
                            scoresViewer.setNextLevel(LEVELS[i + 1]);
                            currentSpeed = SPEED_LOWEST - (currentLevel - 1) * (SPEED_LOWEST - SPEED_FASTEST) / LEVELS.length;
                        }
                        break;
                    }
                }
            }
        }
    }
    
    private void repaintPreviewer() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                previewerView.repaint();
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

    public synchronized void stopGameTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
            gameTimer.purge();
            gameTimer = null;
        }
    }

    private void restartGameTimer() {
        stopGameTimer();

        gameTimer = new Timer(true);
        gameTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                synchronized (OnePlayerGameController.this) {
                    if (gamePaused || !gameStarted) {
                        return;
                    }
                    
                    if (tetrisModel.getFigure1().pushDown() == AbstractFigure.RESULT_FREEZED) {
                        countScores(tetrisModel.removeRows());
                        tetrisModel.getFigureManager1().pregenerateFigure();
                        /*
                         * Put preview figure
                         */
                        previewerModel.clear();
                        tetrisModel.getFigureManager1().getNextFigure().putFigure(previewerModel, 4, 0);
                        repaintPreviewer();
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

}
