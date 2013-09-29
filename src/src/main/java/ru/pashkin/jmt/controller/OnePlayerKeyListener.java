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

public class OnePlayerKeyListener extends KeyAdapter {

    private int keyRepeatDelay = 100;
    private GameController gameController;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean spacePressed;
    private Timer repeatKeyTimer;

    public OnePlayerKeyListener(GameController gameController) {
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
                if (!leftPressed) {
                    leftPressed = true;
                    if (rightPressed) {
                        rightPressed = false;
                    }
                    restartRepeatGameTimer();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (!rightPressed) {
                    rightPressed = true;
                    if (leftPressed) {
                        leftPressed = false;
                    }
                    restartRepeatGameTimer();
                }
                break;
            case KeyEvent.VK_SPACE:
                if (!spacePressed) {
                    spacePressed = true;
                    restartRepeatGameTimer();
                }
                break;
            case KeyEvent.VK_DOWN:
                gameController.player1TurnClockwise();
                break;
            case KeyEvent.VK_UP:
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
                leftPressed = false;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = false;
                break;
        }

        if (!leftPressed && !rightPressed && !spacePressed) {
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
            leftPressed = false;
            rightPressed = false;
            spacePressed = false;

            stopRepeatGameTimer();
        }
        if (leftPressed) {
            gameController.player1PushLeft();
        }
        if (rightPressed) {
            gameController.player1PushRight();
        }
        if (spacePressed) {
            gameController.player1PushDown();
        }
    }

}
