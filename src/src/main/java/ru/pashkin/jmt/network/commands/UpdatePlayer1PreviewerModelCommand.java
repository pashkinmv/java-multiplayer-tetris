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

package ru.pashkin.jmt.network.commands;

import ru.pashkin.jmt.network.AbstractCommand;
import ru.pashkin.jmt.network.NetworkManager;

public class UpdatePlayer1PreviewerModelCommand extends AbstractCommand {
    
    public UpdatePlayer1PreviewerModelCommand(NetworkManager networkManager) {
        super(UpdatePlayer1PreviewerModelCommand.class.getName(), networkManager);
    }

    @Override
    protected void receiveData(String inputLine) {
        networkManager.getGameController().getBoard().getPlayer1PreviewerModel().fromString(inputLine);
        networkManager.getGameController().repaintPlayer1Previewer();
    }

    @Override
    protected String sendData() {
        return networkManager.getGameController().getBoard().getPlayer1PreviewerModel().toString();
    }
}
