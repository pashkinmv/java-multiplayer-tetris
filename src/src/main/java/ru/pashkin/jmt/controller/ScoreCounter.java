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

public class ScoreCounter {

    private int[] LEVELS = new int[]{200, 600, 1200, 2000, 3000, 4200, 5600, 7200, 9000, 11000, 13200};
    private int SPEED_LOWEST = 5000;
    private int SPEED_FASTEST = 90;

    private int scores;
    private int level;
    private int speed;
    private boolean isMaximum;

    public ScoreCounter() {
        reset();
    }

    public void reset() {
        scores = 0;
        level = 1;
        speed = SPEED_LOWEST;
        isMaximum = false;
    }

    public void increaseScores(int removedRowNumber) {
        scores += Math.pow(2, removedRowNumber - 1) * level;

        if (level != LEVELS.length + 2) {
            if (scores >= LEVELS[LEVELS.length - 1]) {
                level = LEVELS.length + 1;
                speed = SPEED_FASTEST;
            } else {
                for (int i = LEVELS.length - 1; i >= 0; i--) {
                    if (scores >= LEVELS[i]) {
                        if (level != i + 2) {
                            level = i + 2;
                            speed = SPEED_LOWEST - (level - 1) * (SPEED_LOWEST - SPEED_FASTEST) / LEVELS.length;
                        }
                        break;
                    }
                }
            }
        }
    }

    public String getNextLevelScores() {
        return isMaximum ? "" : String.valueOf(LEVELS[level - 1]);
    }

    public String getScores() {
        return String.valueOf(scores);
    }

    public String getLevel() {
        return isMaximum ? "Maximum" : String.valueOf(level);
    }

    public long getSpeed() {
        return speed;
    }
}
