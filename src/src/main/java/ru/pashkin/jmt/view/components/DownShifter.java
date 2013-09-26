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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

public class DownShifter extends Container {

    private static final int MAX_OFFSET = 4;
    private int currentOffset;
    private Component shiftSource;
    private boolean shiftDown;
    private Timer timer;

    public DownShifter(Component shiftSource) {
        super();

        final Dimension sourceSize = shiftSource.getSize();
        final Dimension newSize = new Dimension(sourceSize.width, sourceSize.height + MAX_OFFSET);

        setSize(newSize);
        setPreferredSize(newSize);
        setMinimumSize(newSize);
        setMaximumSize(newSize);

        this.shiftSource = shiftSource;
    }

    @Override
    public void paint(Graphics g) {
        final int threadSafeOffset = currentOffset;

        g.translate(0, threadSafeOffset);

        shiftSource.paint(g);

        g.translate(0, -threadSafeOffset);
    }

    public void doShift() {
        shiftDown = true;

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (shiftDown) {
                    if (currentOffset == MAX_OFFSET) {
                        shiftDown = false;
                    } else {
                        currentOffset++;
                    }
                } else {
                    currentOffset--;
                }

                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        repaint();
                    }

                });

                if (currentOffset == 0) {
                    timer.cancel();
                    timer = null;
                }
            }

        }, 0, 15);
    }

}
