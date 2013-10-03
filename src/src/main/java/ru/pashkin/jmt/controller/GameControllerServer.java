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
import ru.pashkin.jmt.network.NetworkManager;
import ru.pashkin.jmt.network.commands.*;
import ru.pashkin.jmt.view.BoardTwoPlayers;

import javax.net.ServerSocketFactory;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;

public class GameControllerServer implements GameController {

    // TODO move to props
    private static final int PORT = 10001;

    private ScoreCounter player1ScoreCounter;
    private ScoreCounter player2ScoreCounter;
    private boolean gameStarted;
    private boolean gamePaused;
    private Timer borderTimer;
    private Timer player1GameTimer;
    private Timer player2GameTimer;
    private BoardTwoPlayers board;
    private NetworkManager networkManager;

    public GameControllerServer(BoardTwoPlayers board) {
        this.board = board;
        networkManager = new NetworkManager(this);
        player1ScoreCounter = new ScoreCounter();
        player2ScoreCounter = new ScoreCounter();
    }

    public void waitForConnection() {
        try (ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(PORT)) {
            networkManager.startCommunication(serverSocket.accept());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            restartPlayer1GameTimer();
            if (board.getTetrisModel().getFigure1().pushDown() == AbstractFigure.RESULT_FREEZED) {
                updatePlayer1ScoreViewer(board.getTetrisModel().removeRows());
                board.getTetrisModel().getFigureManager1().pregenerateFigure();
                /*
                 * Put preview figure
                 */
                board.getPlayer1PreviewerModel().clear();
                board.getTetrisModel().getFigureManager1().getNextFigure().putFigure(board.getPlayer1PreviewerModel(), 4, 0);
                repaintPlayer1Previewer();
                /*
                 * Put new figure
                 */
                final AbstractFigure figure = board.getTetrisModel().getFigureManager1().getCurrentFigure();
                board.getTetrisModel().setFigure1(figure);
                if (!figure.putFigure(board.getTetrisModel(), 4, 0)) {
                    gameOver();
                } else {
                    board.getDownShifter().doShift();
                    networkManager.sendCommand(DoShiftCommand.class);
                }
            }

            updatePlayer1ScoreViewer(1);
            repaintDownShifter();
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
        try {
            stopPlayer1GameTimer();
            stopPlayer2GameTimer();

            networkManager.sendCommand(StartGameCommand.class);

            player1ScoreCounter.reset();
            player2ScoreCounter.reset();
            updatePlayer1ScoreViewer(0);
            updatePlayer2ScoreViewer(0);

            board.getTetrisModel().clear();

            board.getTetrisModel().getFigureManager1().pregenerateFigure();
            board.getPlayer1PreviewerModel().clear();
            board.getTetrisModel().getFigureManager1().getNextFigure().putFigure(board.getPlayer1PreviewerModel(), 4, 0);
            final AbstractFigure figure1 = board.getTetrisModel().getFigureManager1().getCurrentFigure();
            board.getTetrisModel().setFigure1(figure1);
            figure1.putFigure(board.getTetrisModel(), 4, 0);

            board.getTetrisModel().getFigureManager2().pregenerateFigure();
            board.getPlayer2PreviewerModel().clear();
            board.getTetrisModel().getFigureManager2().getNextFigure().putFigure(board.getPlayer2PreviewerModel(), 4, 0);
            final AbstractFigure figure2 = board.getTetrisModel().getFigureManager2().getCurrentFigure();
            board.getTetrisModel().setFigure2(figure2);
            figure2.putFigure(board.getTetrisModel(), 14, 0);

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
    }

    @Override
    public synchronized void pauseGame() {
        if (gameStarted && !gamePaused) {
            try {
                stopPlayer1GameTimer();
                stopPlayer2GameTimer();
                networkManager.sendCommand(PauseGameCommand.class);
                gamePaused = true;
                board.getTetrisModel().setMessage("pause");
                repaintDownShifter();
            } catch (IOException ex) {
                interraptGame(ex);
            }
        }
    }

    @Override
    public synchronized void resumeGame() {
        if (gameStarted && gamePaused) {
            try {
                restartPlayer1GameTimer();
                restartPlayer2GameTimer();
                networkManager.sendCommand(ResumeGameCommand.class);
                gamePaused = false;
                board.getTetrisModel().setMessage(null);
                repaintDownShifter();
            } catch (IOException ex) {
                interraptGame(ex);
            }
        }
    }

    public synchronized void player2PushLeft() {
        if (board.getTetrisModel().getFigure2().pushLeft()) {
            repaintDownShifter();
        }
    }

    public synchronized void player2PushRight() {
        if (board.getTetrisModel().getFigure2().pushRight()) {
            repaintDownShifter();
        }
    }

    public synchronized void player2TurnClockwise() {
        if (!board.getTetrisModel().getFigure2().turnClockwise()) {
            blink();
        }

        repaintDownShifter();
    }

    public synchronized void player2TurnCounterClockwise() {
        if (!board.getTetrisModel().getFigure2().turnCounterClockwise()) {
            blink();
        }

        repaintDownShifter();
    }

    public synchronized void player2PushDown() {
        if (gamePaused || !gameStarted) {
            return;
        }

        try {
            restartPlayer2GameTimer();
            if (board.getTetrisModel().getFigure2().pushDown() == AbstractFigure.RESULT_FREEZED) {
                updatePlayer2ScoreViewer(board.getTetrisModel().removeRows());
                board.getTetrisModel().getFigureManager2().pregenerateFigure();
                /*
                 * Put preview figure
                 */
                board.getPlayer2PreviewerModel().clear();
                board.getTetrisModel().getFigureManager2().getNextFigure().putFigure(board.getPlayer2PreviewerModel(), 4, 0);
                repaintPlayer2Previewer();
                /*
                 * Put new figure
                 */
                final AbstractFigure figure = board.getTetrisModel().getFigureManager2().getCurrentFigure();
                board.getTetrisModel().setFigure2(figure);
                if (!figure.putFigure(board.getTetrisModel(), 14, 0)) {
                    gameOver();
                } else {
                    board.getDownShifter().doShift();
                    networkManager.sendCommand(DoShiftCommand.class);
                }
            }

            updatePlayer2ScoreViewer(1);
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

    public BoardTwoPlayers getBoard() {
        return board;
    }

    public void doShift() {
        board.getDownShifter().doShift();
    }

    public void gameOver() {
        try {
            stopPlayer1GameTimer();
            stopPlayer2GameTimer();
            networkManager.sendCommand(GameOverCommand.class);

            gameStarted = false;
            board.getTetrisModel().setMessage("Game over");
        } catch (IOException e) {
            interraptGame(e);
        }
    }

    private void interraptGame(Exception e) {
        stopPlayer1GameTimer();
        stopPlayer2GameTimer();

        gameStarted = false;
        board.showMessage("Connection lost " + e.getMessage());
    }

    public synchronized void repaintPlayer1Previewer() {
        try {
            networkManager.sendCommand(UpdatePlayer1PreviewerModelCommand.class);
        } catch (IOException ex) {
            interraptGame(ex);
        }

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                board.getPlayer1PreviewerView().repaint();
            }
        });
    }

    public synchronized void repaintPlayer2Previewer() {
        try {
            networkManager.sendCommand(UpdatePlayer2PreviewerModelCommand.class);
        } catch (IOException ex) {
            interraptGame(ex);
        }

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                board.getPlayer2PreviewerView().repaint();
            }
        });
    }

    public synchronized void repaintDownShifter() {
        try {
            networkManager.sendCommand(UpdateTetrisModelCommand.class);
        } catch (IOException ex) {
            interraptGame(ex);
        }

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                board.getDownShifter().repaint();
            }
        });
    }

    public void blink() {
        try {
            networkManager.sendCommand(BlinkCommand.class);

            board.getTetrisView().setBorderColor(Color.RED);
            board.getTetrisView().setBackgroundColor(Color.RED);
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

    private void restartPlayer1GameTimer() {
        stopPlayer1GameTimer();

        player1GameTimer = new Timer(true);
        player1GameTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                synchronized (GameControllerServer.this) {
                    if (gamePaused || !gameStarted) {
                        return;
                    }

                    try {
                        if (board.getTetrisModel().getFigure1().pushDown() == AbstractFigure.RESULT_FREEZED) {
                            updatePlayer1ScoreViewer(board.getTetrisModel().removeRows());
                            board.getTetrisModel().getFigureManager1().pregenerateFigure();
                            /*
                             * Put preview figure
                             */
                            board.getPlayer1PreviewerModel().clear();
                            board.getTetrisModel().getFigureManager1().getNextFigure().putFigure(board.getPlayer1PreviewerModel(), 4, 0);
                            repaintPlayer1Previewer();
                            /*
                             * Put new figure
                             */
                            final AbstractFigure figure = board.getTetrisModel().getFigureManager1().getCurrentFigure();
                            board.getTetrisModel().setFigure1(figure);
                            if (!figure.putFigure(board.getTetrisModel(), 4, 0)) {
                                gameOver();
                            } else {
                                board.getDownShifter().doShift();
                                networkManager.sendCommand(DoShiftCommand.class);
                            }
                        }

                        repaintDownShifter();
                    } catch (IOException e) {
                        interraptGame(e);
                    }
                }
            }

        }, player1ScoreCounter.getSpeed(), player1ScoreCounter.getSpeed());
    }

    private void restartPlayer2GameTimer() {
        stopPlayer2GameTimer();

        player2GameTimer = new Timer(true);
        player2GameTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                synchronized (GameControllerServer.this) {
                    if (gamePaused || !gameStarted) {
                        return;
                    }

                    try {
                        if (board.getTetrisModel().getFigure2().pushDown() == AbstractFigure.RESULT_FREEZED) {
                            updatePlayer2ScoreViewer(board.getTetrisModel().removeRows());
                            board.getTetrisModel().getFigureManager2().pregenerateFigure();
                            /*
                             * Put preview figure
                             */
                            board.getPlayer2PreviewerModel().clear();
                            board.getTetrisModel().getFigureManager2().getNextFigure().putFigure(board.getPlayer2PreviewerModel(), 4, 0);
                            repaintPlayer2Previewer();
                            /*
                             * Put new figure
                             */
                            final AbstractFigure figure = board.getTetrisModel().getFigureManager2().getCurrentFigure();
                            board.getTetrisModel().setFigure2(figure);
                            if (!figure.putFigure(board.getTetrisModel(), 14, 0)) {
                                gameOver();
                            } else {
                                board.getDownShifter().doShift();
                                networkManager.sendCommand(DoShiftCommand.class);
                            }
                        }

                        repaintDownShifter();
                    } catch (IOException e) {
                        interraptGame(e);
                    }
                }
            }

        }, player2ScoreCounter.getSpeed(), player2ScoreCounter.getSpeed());
    }

    public void destroy() {
        stopPlayer1GameTimer();
        stopPlayer2GameTimer();
    }

    private void updatePlayer1ScoreViewer(int removedRowNumber) {
        player1ScoreCounter.increaseScores(removedRowNumber);
        board.getPlayer1ScoresViewer().update(
                player1ScoreCounter.getScores(),
                player1ScoreCounter.getNextLevelScores(),
                player1ScoreCounter.getLevel());
    }

    private void updatePlayer2ScoreViewer(int removedRowNumber) {
        player2ScoreCounter.increaseScores(removedRowNumber);
        board.getPlayer2ScoresViewer().update(
                player2ScoreCounter.getScores(),
                player2ScoreCounter.getNextLevelScores(),
                player2ScoreCounter.getLevel());
    }
}
