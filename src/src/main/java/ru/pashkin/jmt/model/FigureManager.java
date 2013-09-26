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

import java.util.ArrayList;
import java.util.Random;

import ru.pashkin.jmt.model.figures.Curve1;
import ru.pashkin.jmt.model.figures.Curve2;
import ru.pashkin.jmt.model.figures.LeftBoot;
import ru.pashkin.jmt.model.figures.RightBoot;
import ru.pashkin.jmt.model.figures.Snag;
import ru.pashkin.jmt.model.figures.Square;
import ru.pashkin.jmt.model.figures.Stick;
import ru.pashkin.jmt.view.components.TetrisView;

public class FigureManager {

    private final Random random = new Random();
    private final ArrayList<AbstractFigure> figureList = new ArrayList<AbstractFigure>();
    
    private int nextFigure;
    private int nextOrientation;
    private int nextMode;
    private int currentFigure;
    private int currentOrientation;
    private int currentMode;

    public FigureManager() {
        figureList.add(new Stick());
        figureList.add(new Square());
        figureList.add(new Snag());
        figureList.add(new LeftBoot());
        figureList.add(new RightBoot());
        figureList.add(new Curve1());
        figureList.add(new Curve2());
        
        restart();
    }

    public void pregenerateFigure() {
        if (nextFigure != -1) {
            /*
             * Current value = Next value
             */
            currentFigure = nextFigure;
            currentOrientation = nextOrientation;
            currentMode = nextMode;
        } else {
            /*
             * The first load
             * Generate current value
             */
            currentFigure = random.nextInt(figureList.size());
            currentMode = random.nextInt(TetrisView.colors.length) + 1;
            final AbstractFigure figure = figureList.get(currentFigure);
            if (figure instanceof Stick) {
                currentOrientation = random.nextInt(2);
            } else {
                currentOrientation = random.nextInt(4);
            }
        }
        /*
         * Generate next values
         */
        nextFigure = random.nextInt(figureList.size());
        nextMode = random.nextInt(TetrisView.colors.length) + 1;
        final AbstractFigure figure = figureList.get(nextFigure);
        if (figure instanceof Stick) {
            nextOrientation = random.nextInt(2);
        } else {
            nextOrientation = random.nextInt(4);
        }
    }

    public final void restart() {
        nextFigure = -1;
    }
    
    public AbstractFigure getCurrentFigure() {
        return getFigure(currentFigure, currentOrientation, currentMode);
    }

    public AbstractFigure getNextFigure() {
        return getFigure(nextFigure, nextOrientation, nextMode);
    }

    private AbstractFigure getFigure(int figureNumber, int orientation, int mode) {
        final AbstractFigure figure = figureList.get(figureNumber);
        
        figure.setOrientation(orientation);
        figure.setMode(mode);

        return figure;
    }
    
    public void fromString(String string) {
        if (string == null) {
            return;
        }
        
        final String[] result = string.split(",");
        
        if (result.length != 6) {
            return;
        }
        
        nextFigure = Integer.parseInt(result[1]);
        nextOrientation = Integer.parseInt(result[2]);
        nextMode = Integer.parseInt(result[3]);
        currentFigure = Integer.parseInt(result[4]);
        currentOrientation = Integer.parseInt(result[5]);
        currentMode = Integer.parseInt(result[6]);
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(nextFigure).append(",");
        result.append(nextOrientation).append(",");
        result.append(nextMode).append(",");
        result.append(currentFigure).append(",");
        result.append(currentOrientation).append(",");
        result.append(currentMode).append(",");
        return result.toString();
    }
}
