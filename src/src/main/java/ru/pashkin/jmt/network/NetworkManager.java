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

package ru.pashkin.jmt.network;

import ru.pashkin.jmt.controller.GameController;
import ru.pashkin.jmt.network.commands.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Starts communication with server or client.
 * Sends commands.
 */
public class NetworkManager {
    private Map<Class, AbstractCommand> commandMap = new HashMap<>();

    private Socket socket;
    private CommandSender commandSender;
    private GameController gameController;

    public NetworkManager(GameController gameController) {
        this.gameController = gameController;
        addCommands();
    }

    public GameController getGameController() {
        return gameController;
    }

    public void sendString(String string) {
        try {
            commandSender.send(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendCommand(Class commandClass) throws IOException {
        final AbstractCommand command = commandMap.get(commandClass);
        if (command != null) {
            command.sendCommand();
        }
    }

    private void addCommands() {
        commandMap.put(PauseGameCommand.class, new PauseGameCommand(this));
        commandMap.put(PushLeftCommand.class, new PushLeftCommand(this));
        commandMap.put(PushRightCommand.class, new PushRightCommand(this));
        commandMap.put(PushDownCommand.class, new PushDownCommand(this));
        commandMap.put(TurnClockwiseCommand.class, new TurnClockwiseCommand(this));
        commandMap.put(TurnCounterClockwiseCommand.class, new TurnCounterClockwiseCommand(this));
        commandMap.put(UpdateTetrisModelCommand.class, new UpdateTetrisModelCommand(this));
        commandMap.put(UpdatePlayer1PreviewerModelCommand.class, new UpdatePlayer1PreviewerModelCommand(this));
        commandMap.put(UpdatePlayer2PreviewerModelCommand.class, new UpdatePlayer2PreviewerModelCommand(this));
        commandMap.put(ResumeGameCommand.class, new ResumeGameCommand(this));
        commandMap.put(DoShiftCommand.class, new DoShiftCommand(this));
        commandMap.put(BlinkCommand.class, new BlinkCommand(this));
        commandMap.put(UpdateScoresCommand.class, new UpdateScoresCommand(this));
        commandMap.put(GameOverCommand.class, new GameOverCommand(this));
    }
    
    public void startCommunication(Socket socket) throws IOException {
        stopCommunication();

        this.socket = socket;
        
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        commandSender = new CommandSender(){

            @Override
            public void send(String command) throws IOException {
                writer.write(command);
                writer.write("\n\r");
                writer.flush();
            }
        };

        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(NetworkManager.this.socket.getInputStream()));
                    String string;
                    while ((string = reader.readLine()) != null) {
                        if (string.trim().length() > 0) {
                            for (AbstractCommand command : commandMap.values()) {
                                if (command.execute(string)) {
                                    break;
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public void stopCommunication() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }
    
    private interface CommandSender {

        public void send(String command) throws IOException;
    }
    
}
