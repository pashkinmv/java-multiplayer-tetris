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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class KeyListenerTwoPlayers extends KeyAdapter {
    
    private int keyRepeatDelay = 100;
    private GameControllerTwoPlayers gameController;
    private boolean player1LeftPressed;
    private boolean player1RightPressed;
    private boolean player1SpacePressed;
    private boolean player2LeftPressed;
    private boolean player2RightPressed;
    private boolean player2SpacePressed;
    private Timer repeatKeyTimer;
    
    public KeyListenerTwoPlayers(GameControllerTwoPlayers gameController) {
        this.gameController = gameController;
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        if (!gameController.gameStarted()) {
            return;
        }

        if (gameController.gamePaused()) {
            gameController.resumeGame();
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (!player2LeftPressed) {
                    player2LeftPressed = true;
                    if (player2RightPressed) {
                        player2RightPressed = false;
                    }
                    restartRepeatGameTimer();
                }
                break;
            case KeyEvent.VK_A:
                if (!player1LeftPressed) {
                    player1LeftPressed = true;
                    if (player1RightPressed) {
                        player1RightPressed = false;
                    }
                    restartRepeatGameTimer();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (!player2RightPressed) {
                    player2RightPressed = true;
                    if (player2LeftPressed) {
                        player2LeftPressed = false;
                    }
                    restartRepeatGameTimer();
                }
                break;
            case KeyEvent.VK_D:
                if (!player1RightPressed) {
                    player1RightPressed = true;
                    if (player1LeftPressed) {
                        player1LeftPressed = false;
                    }
                    restartRepeatGameTimer();
                }
                break;
            case KeyEvent.VK_ENTER:
                if (!player2SpacePressed) {
                    player2SpacePressed = true;
                    restartRepeatGameTimer();
                }
                break;
            case KeyEvent.VK_X:
                if (!player1SpacePressed) {
                    player1SpacePressed = true;
                    restartRepeatGameTimer();
                }
                break;
            case KeyEvent.VK_DOWN:
                gameController.player2TurnClockwise();
                break;
            case KeyEvent.VK_S:
                gameController.player1TurnClockwise();
                break;
            case KeyEvent.VK_UP:
                gameController.player2TurnCounterClockwise();
                break;
            case KeyEvent.VK_W:
                gameController.player1TurnCounterClockwise();
                break;
            case KeyEvent.VK_P:
                gameController.pauseGame();
                break;
        }
    }
    
    @Override
    public synchronized void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                player2LeftPressed = false;
            case KeyEvent.VK_RIGHT:
                player2RightPressed = false;
                break;
            case KeyEvent.VK_ENTER:
                player2SpacePressed = false;
                break;
            case KeyEvent.VK_A:
                player1LeftPressed = false;
            case KeyEvent.VK_D:
                player1RightPressed = false;
                break;
            case KeyEvent.VK_X:
                player1SpacePressed = false;
                break;
        }
        
        if (!player1LeftPressed && !player1RightPressed && !player1SpacePressed
                && !player2LeftPressed && !player2RightPressed && !player2SpacePressed) {
            stopRepeatGameTimer();
        }
    }
    
    private synchronized void restartRepeatGameTimer() {
        stopRepeatGameTimer();

        repeatKeyTimer = new Timer(true);
        repeatKeyTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                doPressKey();
            }

        }, 0, keyRepeatDelay);
    }
    
    private synchronized void stopRepeatGameTimer() {
        if (repeatKeyTimer != null) {
            repeatKeyTimer.cancel();
            repeatKeyTimer.purge();
            repeatKeyTimer = null;
        }
    }
    
    private void doPressKey() {
        if (gameController.gamePaused() || !gameController.gameStarted()) {
            player1LeftPressed = false;
            player1RightPressed = false;
            player1SpacePressed = false;
            player2LeftPressed = false;
            player2RightPressed = false;
            player2SpacePressed = false;
            
            stopRepeatGameTimer();
        }
        
        if (player1LeftPressed) {
            gameController.player1PushLeft();
        }
        if (player1RightPressed) {
            gameController.player1PushRight();
        }
        if (player1SpacePressed) {
            gameController.player1PushDown();
        }
        if (player2LeftPressed) {
            gameController.player2PushLeft();
        }
        if (player2RightPressed) {
            gameController.player2PushRight();
        }
        if (player2SpacePressed) {
            gameController.player2PushDown();
        }
    }
    
}
