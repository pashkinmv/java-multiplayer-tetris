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

package ru.pashkin.jmt.model;

import java.awt.Point;

public abstract class AbstractFigure {
    
    public static final int ORIENTATION_UP = 3;
    public static final int ORIENTATION_DOWN = 1;
    public static final int ORIENTATION_LEFT = 2;
    public static final int ORIENTATION_RIGHT = 0;
    public static final int RESULT_PUSHED = 1;
    public static final int RESULT_FREEZED = 2;
    public static final int RESULT_CANCELED = 3;
    
    protected int x, y;
    protected int orientation;
    protected TetrisModel model;
    protected int mode;
    
    public int getMode() {
        return mode;
    }
    
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
    
    /**
     * @return true if the figure was put succesfully.
     */
    public boolean putFigure(TetrisModel model, int x, int y) {
        //FIX if one figure is putted on another figure gameOver will occure
        this.model = model;
        this.x = x;
        this.y = y;
        
        final Point[] points = getPoints(orientation);
        
        if (!isFree(points)) {
            fillByValue(points);
            return false;
        }

        fillByValue(points);
        
        return true;
    }
    
    /**
     * @return true if the figure was pushed right successfully.
     */
    public boolean pushRight() {
        final Point[] points = getPoints(orientation);
        
        fillByFree(points);
        
        x++;
        if (!isFree(points)) {
            x--;
            fillByValue(points);
            return false;
        }
        
        fillByValue(points);
        
        return true;
    }
    
    public boolean pushLeft() {
        final Point[] points = getPoints(orientation);
        
        fillByFree(points);
        
        x--;
        if (!isFree(points)) {
            x++;
            fillByValue(points);
            return false;
        }
        
        fillByValue(points);
        
        return true;
    }
    
    public boolean turnClockwise() {
        final Point[] points = getPoints(orientation);
        final Point[] nextPoints = getPoints(getNextClockwiseOrientation());
        
        fillByFree(points);
        
        if (!isFree(nextPoints)) {
            fillByValue(points);
            return false;
        }
        
        fillByValue(nextPoints);
        orientation = getNextClockwiseOrientation();
        
        return true;
    }

    public boolean turnCounterClockwise() {
        final Point[] points = getPoints(orientation);
        final Point[] nextPoints = getPoints(getNextCounterClockwiseOrientation());
        
        fillByFree(points);
        
        if (!isFree(nextPoints)) {
            fillByValue(points);
            return false;
        }
        
        fillByValue(nextPoints);
        orientation = getNextCounterClockwiseOrientation();
        
        return true;
    }
    
    public int pushDown() {
        final Point[] points = getPoints(orientation);
        
        fillByFree(points);
        
        y++;
        if (!isFree(points)) {
            if (intersectsWithFigure(points)) {
                y--;
                fillByValue(points);
                return RESULT_CANCELED;
            } else {
                y--;
                fillByValue(points);
                freeze(points);
                return RESULT_FREEZED;
            }
        }
        
        fillByValue(points);
        
        return RESULT_PUSHED;
    }
    
    protected abstract Point[] getPoints(int orientation);
    
    private int getNextClockwiseOrientation() {
        switch (orientation) {
            case ORIENTATION_DOWN:
                return ORIENTATION_LEFT;
            case ORIENTATION_LEFT:
                return ORIENTATION_UP;
            case ORIENTATION_RIGHT:
                return ORIENTATION_DOWN;
            case ORIENTATION_UP:
                return ORIENTATION_RIGHT;
        }
        
        return ORIENTATION_DOWN;
    }
    
    private int getNextCounterClockwiseOrientation() {
        switch (orientation) {
            case ORIENTATION_DOWN:
                return ORIENTATION_RIGHT;
            case ORIENTATION_LEFT:
                return ORIENTATION_DOWN;
            case ORIENTATION_RIGHT:
                return ORIENTATION_UP;
            case ORIENTATION_UP:
                return ORIENTATION_LEFT;
        }
        
        return ORIENTATION_DOWN;
    }
    
    private boolean isFree(Point[] points) {
        for (Point point : points) {
            if (!model.isFree(x + point.x, y + point.y)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean intersectsWithFigure(Point[] points) {
        for (Point point : points) {
            if (model.isFigure(x + point.x, y + point.y)) {
                return true;
            }
        }
        
        return false;
    }
    
    private void fillByFree(Point[] points) {
        for (Point point : points) {
            model.setCellValue(point.x + x, point.y + y, false, 0);
        }
    }
    
    private void fillByValue(Point[] points) {
        for (Point point : points) {
            model.setCellValue(point.x + x, point.y + y, true, mode);
        }
    }
    
    private void freeze(Point[] points) {
        for (Point point : points) {
            model.setCellValue(point.x + x, point.y + y, false, mode);
        }
    }
}
