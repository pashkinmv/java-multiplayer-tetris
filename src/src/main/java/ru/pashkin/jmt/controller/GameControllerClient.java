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

import ru.pashkin.jmt.network.NetworkManager;
import ru.pashkin.jmt.network.commands.*;
import ru.pashkin.jmt.view.BoardTwoPlayers;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class GameControllerClient implements GameController {

    // TODO move to props
    private static final int PORT = 10001;

    private BoardTwoPlayers board;
    private Timer borderTimer;
    private NetworkManager networkManager;

    public GameControllerClient(BoardTwoPlayers board) {
        this.board = board;
        networkManager = new NetworkManager(this);
    }

    @Override
    public void player1PushLeft() {
        try {
            networkManager.sendCommand(PushLeftCommand.class);
        } catch (IOException ex) {
            interraptGame(ex);
        }
    }

    @Override
    public void player1PushRight() {
        try {
            networkManager.sendCommand(PushRightCommand.class);
        } catch (IOException ex) {
            interraptGame(ex);
        }
    }

    @Override
    public void player1TurnClockwise() {
        try {
            networkManager.sendCommand(TurnClockwiseCommand.class);
        } catch (IOException ex) {
            interraptGame(ex);
        }
    }

    @Override
    public void player1TurnCounterClockwise() {
        try {
            networkManager.sendCommand(TurnCounterClockwiseCommand.class);
        } catch (IOException ex) {
            interraptGame(ex);
        }
    }

    @Override
    public void player1PushDown() {
        try {
            networkManager.sendCommand(PushDownCommand.class);
        } catch (IOException e) {
            interraptGame(e);
        }
    }

    @Override
    public void player2PushLeft() {

    }

    @Override
    public void player2PushRight() {

    }

    @Override
    public void player2TurnClockwise() {

    }

    @Override
    public void player2TurnCounterClockwise() {

    }

    @Override
    public void player2PushDown() {

    }

    @Override
    public boolean gameStarted() {
        return false;
    }

    @Override
    public boolean gamePaused() {
        return false;
    }

    @Override
    public void startGame() {
        // TODO do I need this?
        try {
            networkManager.sendCommand(StartGameCommand.class);
        } catch (IOException e) {
            interraptGame(e);
        }
    }

    @Override
    public void pauseGame() {
        try {
            networkManager.sendCommand(PauseGameCommand.class);
        } catch (IOException e) {
            interraptGame(e);
        }
    }

    @Override
    public void resumeGame() {
        try {
            networkManager.sendCommand(ResumeGameCommand.class);
        } catch (IOException e) {
            interraptGame(e);
        }
    }

    @Override
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
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                board.getPlayer1PreviewerView().repaint();
            }
        });
    }

    @Override
    public void repaintPlayer2Previewer() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                board.getPlayer2PreviewerView().repaint();
            }
        });
    }

    @Override
    public void doShift() {
        board.getDownShifter().doShift();
    }

    public void blink() {
        board.getTetrisView().setBorderColor(Color.RED);
        board.getTetrisView().setBackgroundColor(Color.RED);
        restartBorderTimer();
    }

    @Override
    public void gameOver() {

    }

    @Override
    public BoardTwoPlayers getBoard() {
        return null;
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

    private void interraptGame(Exception e) {
        board.showMessage("Connection lost " + e.getMessage());
    }

    public void connectToServer(String host) {
        try {
            final Socket socket = new Socket(host, PORT);
            networkManager.startCommunication(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {

    }
}
