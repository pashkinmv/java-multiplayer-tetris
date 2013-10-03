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

import ru.pashkin.jmt.model.AbstractFigure;
import ru.pashkin.jmt.view.BoardOnePlayer;
import ru.pashkin.jmt.view.BoardTwoPlayers;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class GameControllerOnePlayer implements GameController {

    private ScoreCounter scoreCounter;
    private boolean gameStarted;
    private boolean gamePaused;
    private BoardOnePlayer board;
    private Timer borderTimer;
    private Timer gameTimer;

    public GameControllerOnePlayer(BoardOnePlayer board) {
        this.board = board;
        scoreCounter = new ScoreCounter();
    }

    @Override
    public synchronized void player1PushLeft() {
        if (board.getTetrisModel().getFigure1().pushLeft()) {
            repaintDownShifter();
        }
    }

    @Override
    public synchronized void player1PushRight() {
        if (board.getTetrisModel().getFigure1().pushRight()) {
            repaintDownShifter();
        }
    }

    @Override
    public synchronized void player1TurnClockwise() {
        if (!board.getTetrisModel().getFigure1().turnClockwise()) {
            blink();
        }

        repaintDownShifter();
    }

    @Override
    public synchronized void player1TurnCounterClockwise() {
        if (!board.getTetrisModel().getFigure1().turnCounterClockwise()) {
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
        if (board.getTetrisModel().getFigure1().pushDown() == AbstractFigure.RESULT_FREEZED) {
            updateScoreViewer(board.getTetrisModel().removeRows());

            board.getTetrisModel().getFigureManager1().pregenerateFigure();
            /*
             * Put preview figure
             */
            board.getPreviewerModel().clear();
            board.getTetrisModel().getFigureManager1().getNextFigure().putFigure(board.getPreviewerModel(), 4, 0);
            repaintPreviewer();
            /*
             * Put new figure
             */
            final AbstractFigure figure = board.getTetrisModel().getFigureManager1().getCurrentFigure();
            board.getTetrisModel().setFigure1(figure);
            if (!figure.putFigure(board.getTetrisModel(), 4, 0)) {
                gameOver();
            } else {
                board.getDownShifter().doShift();
            }
        }

        updateScoreViewer(1);

        repaintDownShifter();
    }

    @Override
    public void player2PushLeft() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void player2PushRight() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void player2TurnClockwise() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void player2TurnCounterClockwise() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void player2PushDown() {
        //To change body of implemented methods use File | Settings | File Templates.
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

        scoreCounter.reset();
        updateScoreViewer(0);
        board.getTetrisModel().clear();
        
        board.getTetrisModel().getFigureManager1().pregenerateFigure();
        board.getPreviewerModel().clear();
        board.getTetrisModel().getFigureManager1().getNextFigure().putFigure(board.getPreviewerModel(), 4, 0);
        final AbstractFigure figure = board.getTetrisModel().getFigureManager1().getCurrentFigure();
        board.getTetrisModel().setFigure1(figure);
        figure.putFigure(board.getTetrisModel(), 4, 0);
        
        gameStarted = true;
        gamePaused = false;

        restartGameTimer();

        repaintDownShifter();
    }

    @Override
    public void gameOver() {
        stopGameTimer();

        gameStarted = false;
        board.getTetrisModel().setMessage("Game over");
    }

    @Override
    public BoardTwoPlayers getBoard() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public synchronized void pauseGame() {
        if (gameStarted && !gamePaused) {
            stopGameTimer();
            gamePaused = true;
            board.getTetrisModel().setMessage("pause");
            repaintDownShifter();
        }
    }

    @Override
    public synchronized void resumeGame() {
        if (gameStarted && gamePaused) {
            restartGameTimer();
            gamePaused = false;
            board.getTetrisModel().setMessage(null);
            repaintDownShifter();
        }
    }

    private void repaintPreviewer() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                board.getPreviewerView().repaint();
            }

        });
    }

    public void repaintDownShifter() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                board.getDownShifter().repaint();
            }

        });
    }

    @Override
    public void repaintPlayer1Previewer() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void repaintPlayer2Previewer() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doShift() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void blink() {
        board.getTetrisView().setBorderColor(Color.RED);
        board.getTetrisView().setBackgroundColor(Color.RED);
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
                board.getTetrisView().setBorderColor(Color.BLACK);
                board.getTetrisView().setBackgroundColor(Color.WHITE);

                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        board.getDownShifter().repaint();
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
                synchronized (GameControllerOnePlayer.this) {
                    if (gamePaused || !gameStarted) {
                        return;
                    }
                    
                    if (board.getTetrisModel().getFigure1().pushDown() == AbstractFigure.RESULT_FREEZED) {
                        updateScoreViewer(board.getTetrisModel().removeRows());
                        board.getTetrisModel().getFigureManager1().pregenerateFigure();
                        /*
                         * Put preview figure
                         */
                        board.getPreviewerModel().clear();
                        board.getTetrisModel().getFigureManager1().getNextFigure().putFigure(board.getPreviewerModel(), 4, 0);
                        repaintPreviewer();
                        /*
                         * Put new figure
                         */
                        final AbstractFigure figure = board.getTetrisModel().getFigureManager1().getCurrentFigure();
                        board.getTetrisModel().setFigure1(figure);
                        if (!figure.putFigure(board.getTetrisModel(), 4, 0)) {
                            gameOver();
                        } else {
                            board.getDownShifter().doShift();
                        }
                    }

                    repaintDownShifter();
                }
            }

        }, scoreCounter.getSpeed(), scoreCounter.getSpeed());
    }

    private void updateScoreViewer(int removedRowNumber) {
        scoreCounter.increaseScores(removedRowNumber);
        board.getScoresViewer().update(scoreCounter.getScores(), scoreCounter.getNextLevelScores(), scoreCounter.getLevel());
    }

}
