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

public class CellValue {
    public boolean isFigure;
    public int figureMode;
    
    public boolean isFree() {
        return figureMode == 0;
    }
    
    public void fromString(String string) {
        if (string == null) {
            return;
        }
        
        final String[] result = string.split(",");
        if (result.length != 2) {
            return;
        }
        
        isFigure = "1".equals(result[0]);
        figureMode = Integer.parseInt(result[1]);
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        
        result.append(isFigure ? "1" : "0");
        result.append(",");
        result.append(figureMode);
        
        return result.toString();
    }
}
