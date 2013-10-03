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

package ru.pashkin.jmt.terminal;

import ru.pashkin.jmt.controller.GameControllerServer;
import ru.pashkin.jmt.controller.KeyListenerOnePlayer;
import ru.pashkin.jmt.view.BoardTwoPlayers;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

public class GameTerminalServer implements GameTerminal {

    private BoardTwoPlayers board = new BoardTwoPlayers();
    private GameControllerServer gameController = new GameControllerServer(board);
    private final KeyListener keyListener = new KeyListenerOnePlayer(gameController);
    private final FocusAdapter focusListener = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            // TODO uncomment
//            gameController.pauseGame();
        }
    };

    public GameTerminalServer() {
    }

    public void waitForConnection() {
        gameController.waitForConnection();
    }

    @Override
    public KeyListener getKeyListener() {
        return keyListener;
    }

    @Override
    public FocusListener getFocusListener() {
        return focusListener;
    }

    @Override
    public void startGame() {
        gameController.startGame();
    }

    @Override
    public void destroyTerminal() {
        gameController.destroy();
    }

    @Override
    public JPanel getBoard() {
        return board;
    }

}
