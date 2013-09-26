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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import javax.net.ServerSocketFactory;

public class NetworkManager {
    private static final int DEFAULT_PORT = 10001;
    private final LinkedHashSet<AbstractCommand> commandList = new LinkedHashSet<>();
    private Socket socket;
    private CommandSender commandSender;
    private boolean isServer;
    private ServerSocket serverSocket = null;
    
    public boolean isServer() {
        return isServer;
    }
    
    public void addCommand(AbstractCommand command) {
        commandList.add(command);
        command.setCommandSender(commandSender);
    }
    
    public void runAsServer() throws IOException {
        isServer = true;
        socket = waitForConnection();
        runCommunication();
    }
    
    public void connectToClient(String host) throws IOException {
        isServer = false;
        socket = connectTo(host);
        runCommunication();
    }
    
    public synchronized void closeServerSocket() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                // DO NOTHING
            } finally {
                serverSocket = null;
            }
        }
    }
    
    private void runCommunication() throws IOException {
        if (socket == null) {
            return;
        }
        
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        commandSender = new CommandSender(){

            @Override
            public void send(String command) throws IOException {
                writer.write(command);
                writer.write("\n\r");
                writer.flush();
            }
        };
        for (AbstractCommand command : commandList) {
            command.setCommandSender(commandSender);
        }
        
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String string;
                    while ((string = reader.readLine()) != null) {
                        if (string.trim().length() > 0) {
                            processCommand(string);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
    
    private void processCommand(String string) {
        for (AbstractCommand command : commandList) {
            if (command.execute(string)) {
                break;
            }
        }
    }
    
    private Socket waitForConnection() {
        try {
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(DEFAULT_PORT);
            return serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Server socket closed");
        } finally {
            closeServerSocket();
        }
        
        return null;
    }
    
    private Socket connectTo(String address) throws IOException {
        return new Socket(address, DEFAULT_PORT);
    }
    
}
