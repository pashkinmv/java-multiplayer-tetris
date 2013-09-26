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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.*;
import ru.pashkin.jmt.network.NetworkManager;
import ru.pashkin.jmt.view.GameTerminal;
import ru.pashkin.jmt.view.MultiplayerGameTerminal;
import ru.pashkin.jmt.view.OnePlayerGameTerminal;
import ru.pashkin.jmt.view.TwoPlayerGameTerminal;
import ru.pashkin.jmt.view.components.ChangeLookAndFeelMenu;
import ru.pashkin.jmt.view.components.ExitMenu;
import ru.pashkin.jmt.view.components.InfoPanel;

public class MainFrame extends JFrame implements TerminalController {

    private CardLayout layout = new CardLayout();
    private InfoPanel panelInfo = new InfoPanel();
    private JPanel panelGame = new JPanel();
    private JPanel panelMenu = new JPanel();
    private NetworkManager serverNetworkManager;
    private JMenuItem restartCurrentGameMenu = new JMenuItem("Start new game");
    private static final String PANEL_INFO = "info";
    private static final String PANEL_GAME = "game";
    private static final String PANEL_MENU = "menu";

    public MainFrame() {
        super("Java multiplayer tetris");

        initGui();
    }

    private void initGui() {
        setLayout(layout);

        add(panelMenu, PANEL_MENU);
        add(panelInfo, PANEL_INFO);
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
        GameTerminal gameTerminal = getCurrentGameTerminal(OnePlayerGameTerminal.class);

        if (gameTerminal == null) {
            gameTerminal = new OnePlayerGameTerminal();
            setGameTerminal(gameTerminal);
        }

        showGameTerminal();
        gameTerminal.startGame();
    }

    /**
     * Starts two player game
     */
    private void startTwoPlayerGame() {
        GameTerminal gameTerminal = getCurrentGameTerminal(TwoPlayerGameTerminal.class);

        if (gameTerminal == null) {
            gameTerminal = new TwoPlayerGameTerminal();
            setGameTerminal(gameTerminal);
        }

        showGameTerminal();
        gameTerminal.startGame();
    }

    /**
     * Starts game as network server
     */
    private void startNetworkGame() {
        GameTerminal gameTerminal = getCurrentGameTerminal(MultiplayerGameTerminal.class);

        /*
         * Wait for connection until game start
         */
        if (gameTerminal == null) {
            showMessage("Wait for the second player connection");

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        serverNetworkManager = new NetworkManager();
                        serverNetworkManager.runAsServer();
                        /*
                         * Network game may be interrapted by another game starting
                         */
                        if (serverNetworkManager == null) {
                            return;
                        }

                        final GameTerminal newGameTerminal = new MultiplayerGameTerminal(serverNetworkManager, MainFrame.this);
                        setGameTerminal(newGameTerminal);

                        showGameTerminal();
                        newGameTerminal.startGame();
                    } catch (IOException ex) {
                        showMessage("Connection error: " + ex.getMessage());
                    } finally {
                        serverNetworkManager = null;
                    }
                }
            }).start();
        } else {
            /*
             * Start game
             */
            gameTerminal.startGame();
        }
    }

    /**
     * Starts the game as network client
     */
    private void connectToNetworkGame() {
        GameTerminal gameTerminal = getCurrentGameTerminal(MultiplayerGameTerminal.class);

        if (gameTerminal == null) {
            try {
                final String serverAddress = JOptionPane.showInternalInputDialog(getContentPane(), "Input server address");
                if (serverAddress == null || serverAddress.trim().isEmpty()) {
                    return;
                }
                final NetworkManager networkManager = new NetworkManager();
                networkManager.connectToClient(serverAddress);

                gameTerminal = new MultiplayerGameTerminal(networkManager, this);
                setGameTerminal(gameTerminal);

                showGameTerminal();
            } catch (UnknownHostException e) {
                showMessage("Remote computer not found");
            } catch (IOException ex) {
                showMessage("Remote computer has not started game");
            }
        } else {
            gameTerminal.startGame();
        }
    }

    /**
     * Get current game terminal and desproy another
     */
    private GameTerminal getCurrentGameTerminal(Class terminalClass) {
        /*
         * Close awiting network server if necessary
         */
        if (serverNetworkManager != null) {
            serverNetworkManager.closeServerSocket();
            serverNetworkManager = null;
        }

        GameTerminal gameTerminal = null;

        final Component[] components = panelGame.getComponents();
        if (components.length > 0) {
            if (terminalClass.isInstance(components[0])) {
                gameTerminal = (GameTerminal) components[0];
            } else {
                restartCurrentGameMenu.setEnabled(false);
                ((GameTerminal) components[0]).destroyTerminal();
            }
        }

        return gameTerminal;
    }

    private void restartCurrentGame() {
        final Component[] components = panelGame.getComponents();
        if (components.length > 0) {
            if (components[0] instanceof GameTerminal) {
                ((GameTerminal) components[0]).startGame();
            }
        }
    }

    private void setGameTerminal(GameTerminal gameTerminal) {
        for (Component component : panelGame.getComponents()) {
            panelGame.remove(component);
        }
        panelGame.add(gameTerminal);
        /*
         * Remove old listeners
         */
        for (KeyListener keyListener : getKeyListeners()) {
            removeKeyListener(keyListener);
        }

        for (FocusListener focusListener : getFocusListeners()) {
            removeFocusListener(focusListener);
        }

        /*
         * Add new listeners
         */
        addKeyListener(gameTerminal.getKeyListener());
        addFocusListener(gameTerminal.getFocusListener());

        requestFocusInWindow();
        System.out.println("request");

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
                restartCurrentGame();
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

    @Override
    public void showMessage(String message) {
        panelInfo.setMessage(message);
        layout.show(getContentPane(), PANEL_INFO);
    }

    @Override
    public void showGameTerminal() {
        layout.show(getContentPane(), PANEL_GAME);
        restartCurrentGameMenu.setEnabled(true);
    }
}
