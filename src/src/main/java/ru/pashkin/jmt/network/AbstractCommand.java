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

import java.io.IOException;

public abstract class AbstractCommand {

    protected String commandName;
    private CommandSender commandSender;
    
    public void setCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }
    
    public boolean execute(String inputLine) {
        if (commandName == null || commandName.trim().isEmpty()
                || inputLine == null || inputLine.trim().isEmpty()) {
            return false;
        }
        
        if (inputLine.startsWith(commandName + ": ")) {
            process(inputLine.substring((commandName + ": ").length()));
        }
        
        return false;
    }
    
    public void sendCommand() throws IOException {
        commandSender.send(commandName + ": " + composeCommand());
    }
    
    protected abstract void process(String inputLine);
    protected abstract String composeCommand();
}
