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

package ru.pashkin.jmt.controller.multiplayercommands;

import ru.pashkin.jmt.controller.MultiplayerGameController;
import ru.pashkin.jmt.network.AbstractCommand;
import ru.pashkin.jmt.utils.StringUtils;
import ru.pashkin.jmt.view.ScoresViewer;

public class UpdateScoresCommand extends AbstractCommand {
    
    private MultiplayerGameController multiplayerGameController;
    
    public UpdateScoresCommand(MultiplayerGameController multiplayerGameController) {
        commandName = "updateScores";
        
        this.multiplayerGameController = multiplayerGameController;
    }

    @Override
    protected void process(String inputLine) {
        if (StringUtils.isEmpty(inputLine)) {
            return;
        }
        
        final String[] splittedLine = inputLine.split(":");
        if (splittedLine.length != 3) {
            return;
        }
        
        final String scores = splittedLine[0];
        final String level = splittedLine[1];
        final String nextLevel = splittedLine[2];
        
        final ScoresViewer scoresViewer1 = multiplayerGameController.getPlayer1ScoresViewer();
        scoresViewer1.setScores(scores);
        scoresViewer1.setLevel(level);
        scoresViewer1.setNextLevel(nextLevel);
        
        final ScoresViewer scoresViewer2 = multiplayerGameController.getPlayer2ScoresViewer();
        scoresViewer2.setScores(scores);
        scoresViewer2.setLevel(level);
        scoresViewer2.setNextLevel(nextLevel);
    }

    @Override
    protected String composeCommand() {
        final ScoresViewer scoresViewer = multiplayerGameController.getPlayer1ScoresViewer();
        
        final String scores = scoresViewer.getScores();
        final String level = scoresViewer.getLevel();
        final String nextLevel = scoresViewer.getNextLevel();
        
        final StringBuilder result = new StringBuilder();
        result.append(scores).append(":");
        result.append(level).append(":");
        result.append(nextLevel);
        
        return result.toString();
    }
    
}
