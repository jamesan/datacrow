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

package net.datacrow.console.windows.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcCheckBox;
import net.datacrow.console.components.DcFontSelector;
import net.datacrow.console.components.IComponent;
import net.datacrow.core.DcRepository;
import net.datacrow.core.settings.Setting;
import net.datacrow.core.settings.SettingsGroup;

/**
 * An automatically filled panel for the settings view. Each panel represents a settings group
 * @author Robert Jan van der Waals
 * @since 1.4
 */
public class SettingsPanel extends JPanel {
    
    private SettingsGroup group;
    private Hashtable<Setting, Component> components = new Hashtable<Setting, Component>();
    
    public SettingsPanel(SettingsGroup group) {
        this.group = group;
        buildPanel(group.getSettings());
    }
    
    public String getHelpIndex() {
        return group.getHelpIndex();
    }
    
    /**
     * Creates the panel with its components to allow changing the
     * values of the settings. The components are stored in each Setting
     */
    private void buildPanel(Map<String, Setting> settings) {
        int y = 0;
        this.setLayout(Layout.getGBL());
        
        for (Setting setting : settings.values()) {
            if (setting.showToUser()) {
                JLabel label = ComponentFactory.getLabel(setting.getLabelText());
                JComponent c = setting.getUIComponent();
                
                boolean simplecomp = false;
                if (c instanceof JComboBox || c instanceof JTextField || c instanceof DcCheckBox) {
                    c.setMinimumSize(new Dimension(200, 21));
                    c.setPreferredSize(new Dimension(200,21));
                    c.setMaximumSize(new Dimension(200,21));
                    simplecomp = true;
                }
                
                if (c instanceof DcFontSelector) {
                    c.setBorder(ComponentFactory.getTitleBorder(setting.getLabelText()));
                }

                components.put(setting, c);
                
                if (setting.displayLabel()) {
                    label.setToolTipText(setting.getHelpText());
                    add(label, Layout.getGBC( 0, y, 1, 1, 1.0, 1.0
                              ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                               new Insets( 5, 5, 5, 5), 0, 0));
                    add(c,  Layout.getGBC( 1, y, 1, 1, 1.0, 1.0
                              ,GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,
                               new Insets( 5, 5, 5, 5), 0, 0));
                } else {
                    int stretch = simplecomp ? GridBagConstraints.HORIZONTAL : GridBagConstraints.BOTH;
                    double weigth = simplecomp ? 1.0 : 10.0;
                    
                    add(c,  Layout.getGBC( 0, y, 1, 1, weigth, weigth,
                        GridBagConstraints.NORTHWEST, stretch, new Insets( 5, 5, 5, 5), 0, 0));
                }
                y++;
            }
        }
        this.setBorder(new EtchedBorder());
        this.setVisible(false);
    }    

    @Override
    public String toString() {
        return group.toString();
    }

    public void initializeSettings() {
        for (Setting setting : components.keySet()) {
            Component c = components.get(setting);
            Object value = setting.getValue();
            if (c instanceof IComponent) {
                ((IComponent) c).setValue(value);
            } else if (c instanceof JTextComponent) {
                ((JTextField) c).setText(value.toString());
            } else if (c instanceof JComboBox) {
                ((JComboBox) c).setSelectedItem(value);
            } else if (c instanceof JCheckBox) {
                boolean bValue = ((Boolean) value).booleanValue();
                ((JCheckBox) c).setSelected(bValue);
            } else if (c instanceof JColorChooser) {
                Color color = (Color) value;
                ((JColorChooser) c).setColor(color);
            } 
        }
    }
    
    public void saveSettings() {
        for (Setting setting : components.keySet()) {
            Component c = components.get(setting);
            Object value = setting.getValue();
            
            if (c instanceof IComponent) {
                value = ((IComponent) c).getValue();  
            } else if (c instanceof JTextComponent) {
                value = ((JTextField) c).getText();
            } else if (c instanceof JComboBox) {
                value = ((JComboBox) c).getSelectedItem();
            } else if (c instanceof JCheckBox) {
                value = ((JCheckBox) c).isSelected();
            } else if (c instanceof JColorChooser) {
                value = ((JColorChooser) c).getColor();
            }

            if (setting.getDataType() == DcRepository.ValueTypes._DOUBLE)
                value = new Double((String) value);
            
            if (setting.getDataType() == DcRepository.ValueTypes._LONG) {
                if (!(value instanceof Long))
                    value = Long.valueOf(value.toString());
            }
            
            if (value != null)
                setting.setValue(value);
        }
    }
}
