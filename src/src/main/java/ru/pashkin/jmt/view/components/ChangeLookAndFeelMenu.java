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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * <pre>Usage example:
 * 
 * <code>    final JMenuBar menuBar = new JMenuBar();
 *     menuBar.add(new ChangeLookAndFeelMenu("Change LAF", this, false));
 *     setJMenuBar(menuBar);</code></pre>
 * 
 * @author Pashkin
 */
public class ChangeLookAndFeelMenu extends JMenu {
    
    private Component mainComponent;
    private boolean packMainComponent;

    public ChangeLookAndFeelMenu(String title, Component mainComponent, boolean packMainComponent) {
        super(title);
        this.mainComponent = mainComponent;
        this.packMainComponent = packMainComponent;
        init();
    }

    private void init() {
        final ButtonGroup group = new ButtonGroup();
        for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            final JRadioButtonMenuItem menuFoo = new JRadioButtonMenuItem(laf.getName());
            add(menuFoo);
            group.add(menuFoo);

            if (UIManager.getLookAndFeel().getName().equals(laf.getName())) {
                menuFoo.setSelected(true);
            }

            final String className = laf.getClassName();
            menuFoo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        UIManager.setLookAndFeel(className);
                        SwingUtilities.updateComponentTreeUI(mainComponent);
                        if (packMainComponent) {
                            if (mainComponent instanceof Window) {
                                ((Window) mainComponent).pack();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            });
        }
    }

}
