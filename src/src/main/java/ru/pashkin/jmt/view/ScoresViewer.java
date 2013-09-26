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

package ru.pashkin.jmt.view;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ScoresViewer extends JPanel {
    
    private final JLabel scoresLabel = new JLabel("Scores:");
    private final JTextField scores = new JTextField();
    private final JLabel levelLabel = new JLabel("Level:");
    private final JTextField level = new JTextField();
    private final JLabel nextLevelLabel = new JLabel("Next level:");
    private final JTextField nextLevel = new JTextField();
    
    public ScoresViewer() {
        initGUI();
    }
    
    private void initGUI() {
        scores.setFocusable(false);
        level.setFocusable(false);
        nextLevel.setFocusable(false);
        
        setLayout(new GridLayout(3, 2, 5, 5));
        add(levelLabel);
        add(level);
        add(scoresLabel);
        add(scores);
        add(nextLevelLabel);
        add(nextLevel);
    }
    
    public String getScores() {
        return scores.getText();
    }
    
    public void setScores(int newScores) {
        setScores(String.valueOf(newScores));
    }
    
    public void setScores(String newScores) {
        scores.setText(newScores);
    }
    
    public String getLevel() {
        return level.getText();
    }
    
    public void setLevel(int newLevel) {
        setLevel(String.valueOf(newLevel));
    }
    
    public void setLevel(String newLevel) {
        level.setText(newLevel);
    }
    
    public String getNextLevel() {
        return nextLevel.getText();
    }
    
    public void setNextLevel(int newNextLevel) {
        setNextLevel(String.valueOf(newNextLevel));
    }
    
    public void setNextLevel(String newNextLevel) {
        nextLevel.setText(newNextLevel);
    }
}
