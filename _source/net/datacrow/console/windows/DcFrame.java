/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                       This file is part of Data Crow.                      *
 *       Data Crow is free software; you can redistribute it and/or           *
 *        modify it under the terms of the GNU General Public                 *
 *       License as published by the Free Software Foundation; either         *
 *              version 3 of the License, or any later version.               *
 *                                                                            *
 *        Data Crow is distributed in the hope that it will be useful,        *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             *
 *           See the GNU General Public License for more details.             *
 *                                                                            *
 *        You should have received a copy of the GNU General Public           *
 *  License along with this program. If not, see http://www.gnu.org/licenses  *
 *                                                                            *
 ******************************************************************************/

package net.datacrow.console.windows;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.IWindow;
import net.datacrow.core.DataCrow;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.plugin.PluginHelper;
import net.datacrow.util.DcSwingUtilities;
import net.datacrow.util.Utilities;

public class DcFrame extends JFrame implements WindowFocusListener, IWindow {

    private String helpIndex = null;

    public DcFrame(String title, ImageIcon icon) {
        super(title);
        
        setIconImage(icon == null ? IconLibrary._icoMain.getImage() : icon.getImage());
        
        DcSwingUtilities.setRootFrame(this);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        
        addWindowFocusListener(this);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        PluginHelper.registerKey(getRootPane(), "Help");
        PluginHelper.registerKey(getRootPane(), "CloseWindow");
        
        DcSwingUtilities.addOpenWindow(this);
    }
    
    @Override
    public void close() {

        DcSwingUtilities.removeOpenWindow(this);
        
        for (WindowListener wl : getWindowListeners())
            removeWindowListener(wl);
        
        helpIndex = null;
        
        if (rootPane.getInputMap() != null)
            rootPane.getInputMap().clear();

        if (rootPane.getActionMap() != null) {
            rootPane.getActionMap().clear();
            rootPane.setActionMap(null);
        }
        
        ComponentFactory.clean(getContentPane());
        dispose();
        
        DcSwingUtilities.setRootFrame(DataCrow.mainFrame);
    }
    
    @Override
    public void setSize(Dimension d) {
        if (d == null || d.height == 20 || d.width < 20)
            return;
        
        super.setSize(d);
    }

    protected void addKeyListener(KeyStroke keyStroke, Action action, String name) {
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, name);
        rootPane.getActionMap().put(name, action);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) DcSwingUtilities.setRootFrame(this);
    }

    public void setHelpIndex(String helpID) {
        helpIndex =  helpID;
    }
    
    public String getHelpIndex() {
        return helpIndex;
    }

    protected void setCenteredLocation() {
        setLocation(Utilities.getCenteredWindowLocation(getSize(), false));
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        DcSwingUtilities.setRootFrame(this);
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(DcSwingUtilities.setRenderingHint(g));
    }
}
