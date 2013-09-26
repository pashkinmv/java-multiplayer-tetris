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

public class TetrisModel {
    private FigureManager figureManager1 = new FigureManager();
    private FigureManager figureManager2 = new FigureManager();
    private CellValue[][] cellValues;
    private AbstractFigure currentFigure1;
    private AbstractFigure currentFigure2;
    //TODO move out of here
    private String message;
    private int width;
    private int height;
    /*
     * true if the model supports two players
     */
    private boolean twoPlayerMode;

    public TetrisModel(int width, int height, boolean twoPlayerMode) {
        this.width = width;
        this.height = height;
        this.twoPlayerMode = twoPlayerMode;
        
        clear();
    }
    
    public boolean getTwoPlayerMode() {
        return twoPlayerMode;
    }
    
    public AbstractFigure getFigure1() {
        return currentFigure1;
    }
    
    public void setFigure1(AbstractFigure figure1) {
        currentFigure1 = figure1;
    }
    
    public AbstractFigure getFigure2() {
        return currentFigure2;
    }
    
    public void setFigure2(AbstractFigure figure2) {
        currentFigure2 = figure2;
    }
    
    public FigureManager getFigureManager1() {
        return figureManager1;
    }
    
    public FigureManager getFigureManager2() {
        return figureManager2;
    }

    public final void clear() {
        cellValues = new CellValue[width][height];
        for (int i = 0; i < cellValues.length; i++) {
            for (int j = 0; j < cellValues[i].length; j++) {
                cellValues[i][j] = new CellValue();
            }
        }
        currentFigure1 = null;
        currentFigure2 = null;
        message = null;
    }

    /**
     * Removes fullfilled rows
     * @return Removed rows number
     */
    public int removeRows() {
        int counter = 0;
        for (int row = 0; row < height; row++) {
            boolean removeRow = true;
            for (int x = 0; x < width; x++) {
                if (isFree(x, row) || isFigure(x, row)) {
                    removeRow = false;
                    break;
                }
            }
            if (removeRow) {
                removeRow(row);
                counter++;
            }
        }

        return counter;
    }

    /**
     * Removes the row, shifts all figures down
     */
    private void removeRow(int row) {
        for (int i = row; i > 0; i--) {
            for (int x = 0; x < width; x++) {
                final CellValue upperCellValue = cellValues[x][i - 1];
                final CellValue lowerCellValue = cellValues[x][i];
                
                if (lowerCellValue.isFigure) {
                    //BUG: if upper cell is not free and not figure it will be destroyed
                    continue;
                } else if (upperCellValue.isFigure) {
                    lowerCellValue.figureMode = 0;
                    continue;
                }
                
                lowerCellValue.figureMode = upperCellValue.figureMode;
                upperCellValue.figureMode = 0;
            }
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    /**
     * @return column count
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return row count
     */
    public int getHeight() {
        return height;
    }

    public CellValue getCellValue(int x, int y) {
        return cellValues[x][y];
    }

    public void setCellValue(int x, int y, boolean isFigure, int figureMode) {
        final CellValue cellValue = cellValues[x][y];
        cellValue.isFigure = isFigure;
        cellValue.figureMode = figureMode;
    }
    
    public boolean isFree(int x, int y) {
        if (cellValues == null) {
            return false;
        }
        
        if (x < 0 || y < 0 || x > width - 1 || y > height - 1) {
            return false;
        }
        
        return cellValues[x][y].isFree();
    }
    
    public boolean isFigure(int x, int y) {
        if (cellValues == null) {
            return false;
        }
        
        if (x < 0 || y < 0 || x > width - 1 || y > height - 1) {
            return false;
        }
        
        return cellValues[x][y].isFigure;
    }
    
    public void fromString(String string) {
        if (string == null) {
            return;
        }
        
        final String[] result = string.split(";");
        if (result.length != cellValues.length * cellValues[0].length) {
            return;
        }
        
        for (int i = 0; i < cellValues.length; i++) {
            for (int j = 0; j < cellValues[0].length; j++) {
                cellValues[i][j].fromString(result[i * cellValues[0].length + j]);
            }
        }
    }
    
    @Override
    public String toString() {
        if (cellValues == null) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < cellValues.length; i++) {
            if (i > 0) {
                result.append(";");
            }
            for (int j = 0; j < cellValues[0].length; j++) {
                if (j > 0) {
                    result.append(";");
                }
                result.append(cellValues[i][j]);
            }
        }
        
        return result.toString();
    }

}
