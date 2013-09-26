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

package ru.pashkin.jmt.view.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import ru.pashkin.jmt.model.CellValue;
import ru.pashkin.jmt.model.TetrisModel;

public class TetrisView extends Component {

    private static final int squareSize = 20;
    private static final int paddingSize = 1;
    private TetrisModel model;
    private Dimension size;
    private Color borderColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    public static final Color[] colors = new Color[] {
        new Color(250, 0, 0), //red
        new Color(249, 106, 11), //orange
        new Color(249, 249, 11), // yellow
        new Color(0, 200, 0), // green
        new Color(171, 50, 135), // red - blue
        new Color(0, 0, 255), // dark blue
        new Color(64, 0, 128) // purple
    };

    public TetrisView(TetrisModel model) {
        this.model = model;

        final int modelWidth = model.getWidth();
        final int modelHeight = model.getHeight();

        final int height = modelHeight * squareSize + (modelHeight + 1) * paddingSize;
        final int width = modelWidth * squareSize + (modelWidth + 1) * paddingSize;
        size = new Dimension(width, height);
    }
    
    public void setBorderColor(Color borderColor) {
        if (borderColor != null) {
            this.borderColor = borderColor;
        }
    }
    
    public void setBackgroundColor(Color background) {
        if (background != null) {
            this.backgroundColor = background;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        /*
         * Fill background
         */
        g.setColor(backgroundColor);
        g.fillRect(1, 1, size.width - 1, size.height - 1);
        /*
         * Draw border
         */
        g.setColor(borderColor);
        g.drawRect(0, 0, size.width - 1, size.height - 1);

        for (int i = 0; i < model.getWidth(); i++) {
            for (int j = 0; j < model.getHeight(); j++) {
                fillCell(i, j, g);
            }
        }

        writeMessage(g);
    }

    /**
     * Draws cell
     */
    private void fillCell(int x, int y, Graphics g) {
        final CellValue cellValue = model.getCellValue(x, y);
        if (cellValue.figureMode > 0 && cellValue.figureMode <= colors.length) {
            g.setColor(colors[cellValue.figureMode - 1]);
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }

        final int xOffset = x * squareSize + (x + 1) * paddingSize;
        final int yOffset = y * squareSize + (y + 1) * paddingSize;

        g.fill3DRect(xOffset, yOffset, squareSize, squareSize, true);
    }

    private void writeMessage(Graphics g) {
        final String message = model.getMessage();

        if (message == null) {
            return;
        }

        final int fontSize = 27;

        final Font currentFont = g.getFont();
        final Font newFont = new Font(currentFont.getName(), Font.BOLD, fontSize);
        g.setFont(newFont);

        final int messageWidth = g.getFontMetrics().stringWidth(message);

        drawOpaqueRectangle(g, .8F, (getWidth() - messageWidth) / 2 - 5, (getHeight() - fontSize) / 2, messageWidth + 10, fontSize + 10);

        g.setColor(Color.BLACK);

        final int x = (getWidth() - messageWidth) / 2;
        final int y = (getHeight() + fontSize) / 2;
        g.drawString(message, x, y);
    }

    private void drawOpaqueRectangle(Graphics g, float alpha, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setPaint(Color.WHITE);
        g2d.fillRect(x, y, width, height);
        g2d.setComposite(originalComposite);
    }

    @Override
    public int getHeight() {
        return size.height;
    }

    @Override
    public Dimension getMaximumSize() {
        return size;
    }

    @Override
    public Dimension getMinimumSize() {
        return size;
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    public Dimension getSize() {
        return size;
    }

    @Override
    public Dimension getSize(Dimension rv) {
        return size;
    }

    @Override
    public int getWidth() {
        return size.width;
    }
}
