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

package ru.pashkin.jmt.model.figures;

import ru.pashkin.jmt.model.AbstractFigure;
import java.awt.Point;

public class RightBoot extends AbstractFigure {

    private static final Point[] leftOrientation = new Point[4];
    private static final Point[] rightOrientation = new Point[4];
    private static final Point[] upOrientation = new Point[4];
    private static final Point[] downOrientation = new Point[4];
    
    static {
        leftOrientation[0] = new Point(2, 0);
        leftOrientation[1] = new Point(0, 1);
        leftOrientation[2] = new Point(1, 1);
        leftOrientation[3] = new Point(2, 1);
        
        rightOrientation[0] = new Point(0, 1);
        rightOrientation[1] = new Point(1, 1);
        rightOrientation[2] = new Point(2, 1);
        rightOrientation[3] = new Point(0, 2);
        
        upOrientation[0] = new Point(1, 0);
        upOrientation[1] = new Point(1, 1);
        upOrientation[2] = new Point(1, 2);
        upOrientation[3] = new Point(2, 2);
        
        downOrientation[0] = new Point(0, 0);
        downOrientation[1] = new Point(1, 0);
        downOrientation[2] = new Point(1, 1);
        downOrientation[3] = new Point(1, 2);
    }
    
    @Override
    public Point[] getPoints(int orientation) {
        switch (orientation) {
            case ORIENTATION_DOWN:
                return downOrientation;
            case ORIENTATION_LEFT:
                return leftOrientation;
            case ORIENTATION_RIGHT:
                return rightOrientation;
            case ORIENTATION_UP:
                return upOrientation;
        }
        
        return downOrientation;
    }

}
