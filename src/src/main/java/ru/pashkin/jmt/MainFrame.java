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

package ru.pashkin.jmt;

import ru.pashkin.jmt.terminal.*;
import ru.pashkin.jmt.view.components.ChangeLookAndFeelMenu;
import ru.pashkin.jmt.view.components.ExitMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private GameTerminal currentGameTerminal;
    private CardLayout layout = new CardLayout();
    private JPanel panelGame = new JPanel();
    private JPanel panelMenu = new JPanel();
    private JMenuItem restartCurrentGameMenu = new JMenuItem("Start new game");
    private static final String PANEL_GAME = "game";
    private static final String PANEL_MENU = "menu";

    public MainFrame() {
        super("Java multiplayer tetris");

        initGui();
    }

    private void initGui() {
        setLayout(layout);

        add(panelMenu, PANEL_MENU);
        add(panelGame, PANEL_GAME);

        initPanelMenu();
        addMenu();

        setMinimumSize(new Dimension(500, 400));
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Starts one player game
     */
    private void startOnePlayerGame() {
        installGameTerminal(GameTerminalOnePlayer.class);
        showGameTerminal();
        currentGameTerminal.startGame();
    }

    /**
     * Starts two player game
     */
    private void startTwoPlayerGame() {
        installGameTerminal(GameTerminalTwoPlayers.class);
        showGameTerminal();
        currentGameTerminal.startGame();
    }

    /**
     * Starts game as network server
     */
    private void startNetworkGame() {
        installGameTerminal(GameTerminalServer.class);
        // TODO implement
//        showMessage("Wait for the second player connection");
        ((GameTerminalServer) currentGameTerminal).waitForConnection();
        showGameTerminal();
        currentGameTerminal.startGame();
    }

    /**
     * Starts the game as network client
     */
    private void connectToNetworkGame() {
        installGameTerminal(GameTerminalClient.class);

        final String serverAddress = JOptionPane.showInternalInputDialog(getContentPane(), "Input server ip address or PC network name");
        if (serverAddress == null || serverAddress.trim().isEmpty()) {
            return;
        }

        // TODO implement
//        showMessage("Wait for the second player connection");
        ((GameTerminalClient) currentGameTerminal).connectToServer(serverAddress);
        showGameTerminal();
        currentGameTerminal.startGame();

    }

    /**
     * Get current game terminal and destroy previous one if necessary
     */
    private <T extends GameTerminal> void installGameTerminal(Class<T> clazz) {
        if (currentGameTerminal != null && clazz.getClass().isInstance(currentGameTerminal)) {
            return;
        }

        destroyCurrentGameTerminal();

        try {
            currentGameTerminal = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        initCurrentGameTerminal();
    }

    private void destroyCurrentGameTerminal() {
        if (currentGameTerminal == null) {
            return;
        }

        currentGameTerminal.destroyTerminal();
        panelGame.remove(currentGameTerminal.getBoard());
        removeKeyListener(currentGameTerminal.getKeyListener());
        removeFocusListener(currentGameTerminal.getFocusListener());
    }

    private void initCurrentGameTerminal() {
        panelGame.add(currentGameTerminal.getBoard());
        addKeyListener(currentGameTerminal.getKeyListener());
        addFocusListener(currentGameTerminal.getFocusListener());

        requestFocusInWindow();

        pack();
    }

    private void initPanelMenu() {
        final JButton singlePlayer = new JButton("One player");
        final JButton twoPlayer = new JButton("Two players");
        final JButton serverGame = new JButton("Create network game");
        final JButton clientGame = new JButton("Join network game");

        singlePlayer.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                startOnePlayerGame();
            }
        });
        twoPlayer.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                startTwoPlayerGame();
            }
        });
        serverGame.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                startNetworkGame();
            }
        });
        clientGame.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToNetworkGame();
            }
        });

        panelMenu.add(singlePlayer);
        panelMenu.add(twoPlayer);
        panelMenu.add(serverGame);
        panelMenu.add(clientGame);

        panelMenu.setLayout(new GridLayout(2, 2, 10, 10));
    }

    private void addMenu() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(getMenuFile());
        menuBar.add(getMenuSettings());

        setJMenuBar(menuBar);
    }

    private JMenu getMenuFile() {
        restartCurrentGameMenu.setEnabled(false);
        restartCurrentGameMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentGameTerminal.startGame();
            }
        });

        final JMenu menuGameType = new JMenu("Game type");

        final JMenuItem menuStartAlone = new JMenuItem("One player");
        menuStartAlone.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                startOnePlayerGame();
            }
        });

        final JMenuItem menuStartPair = new JMenuItem("Two players");
        menuStartPair.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                startTwoPlayerGame();
            }
        });

        final JMenu menuMultiplayerGame = new JMenu("Network game");

        final JMenuItem menuStartServer = new JMenuItem("Create");
        menuStartServer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                startNetworkGame();
            }
        });

        final JMenuItem menuConnectToServer = new JMenuItem("Join");
        menuConnectToServer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                connectToNetworkGame();
            }
        });

        menuMultiplayerGame.add(menuStartServer);
        menuMultiplayerGame.add(menuConnectToServer);

        menuGameType.add(menuStartAlone);
        menuGameType.add(menuStartPair);
        menuGameType.add(menuMultiplayerGame);

        final JMenu menuFile = new JMenu("Game");
        menuFile.add(restartCurrentGameMenu);
        menuFile.add(menuGameType);
        menuFile.add(new ExitMenu());

        return menuFile;
    }

    private JMenu getMenuSettings() {
        final JMenu menuSettings = new JMenu("Settings");
        menuSettings.add(new ChangeLookAndFeelMenu("Lookup", this, true));

        return menuSettings;
    }

    public void showGameTerminal() {
        layout.show(getContentPane(), PANEL_GAME);
        restartCurrentGameMenu.setEnabled(true);
    }
}
