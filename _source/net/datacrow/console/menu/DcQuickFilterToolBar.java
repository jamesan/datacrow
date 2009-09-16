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

package net.datacrow.console.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.components.DcComboBox;
import net.datacrow.console.components.DcEditableComboBox;
import net.datacrow.console.components.DcReferenceField;
import net.datacrow.console.components.renderers.ComboBoxRenderer;
import net.datacrow.core.DcRepository;
import net.datacrow.core.IconLibrary;
import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.DataFilters;
import net.datacrow.core.data.DataManager;
import net.datacrow.core.data.Operator;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.util.DcSwingUtilities;

public class DcQuickFilterToolBar extends JToolBar implements ActionListener, MouseListener, KeyListener {

    private final DcEditableComboBox comboCriteria = new DcEditableComboBox();
    private final DcComboBox comboFields = ComponentFactory.getComboBox(new DefaultComboBoxModel());
    private final DcComboBox comboFilters = ComponentFactory.getComboBox();
    
    private final DcModule module;
    
    public DcQuickFilterToolBar(DcModule module) {
        this.module = module;
        build();
        
        // set default value (if applicable)
        int fieldIdx = module.getSettings().getInt(DcRepository.ModuleSettings.stQuickFilterDefaultField);
        comboFields.setSelectedItem(module.getField(fieldIdx));
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        for (Component c : getComponents()) 
            c.setFont(font);
    }    
    
    private void search() {
        search(getDataFilter());
    }
    
    private void search(DataFilter df) {
        DcObject[] result = DataManager.get(module.getIndex(), df);
        if (result.length == 0)
            DcModules.getCurrent().getSearchView().clear();

        DataManager.bindData(module.getSearchView(), result);
        DataFilters.setCurrent(module.getIndex(), df);
    }
    
    private Object getValue() {
        DcField field = (DcField) comboFields.getSelectedItem();
        
        Object value = comboCriteria.getSelectedItem();
        if (field.getFieldType() == ComponentFactory._RATINGCOMBOBOX)
            value = value != null && value.equals(Long.valueOf(-1)) ? null : value;
        
        value = value != null && value instanceof String ? ((String) value).trim() : value;
        
        return value;
    }
    
    private DataFilter getDataFilter() {
        DcField field = (DcField) comboFields.getSelectedItem();
        Object value = getValue();
        
        if (value != null && !value.equals("")) {
            Operator operator = Operator.CONTAINS;
            
            if (value.equals(DcResources.getText("lblEmptySearchValue"))) {
                operator = Operator.IS_EMPTY;
                value = null;
            } else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                Collection<Object> c = new ArrayList<Object>();
                c.add(value);
                value = c;
            } else if (field.getValueType() == DcRepository.ValueTypes._LONG) {
                operator = Operator.EQUAL_TO;
            }
            
            DataFilter df = DataFilters.getDefaultDataFilter(module.getIndex());
            DataFilterEntry dfe = new DataFilterEntry(DataFilterEntry._AND, 
                                                      module.getIndex(), 
                                                      field.getIndex(), 
                                                      operator,
                                                      value);
            df.addEntry(dfe);
            return df;
        } else {
            return null;
        }
    }

    private void setSearchField(DcField field) {
        JComponent c = ComponentFactory.getComponent(field.getModule(), field.getReferenceIdx(), field.getIndex(), field.getFieldType(), field.getLabel(), 255);
        
        if (c instanceof DcReferenceField) 
            c = ((DcReferenceField) c).getComboBox();
        
        comboCriteria.removeAllItems();
        
        if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
            DcObject[] objects = DataManager.get(field.getReferenceIdx(), null);
            comboCriteria.addItem(" ");
            for (int i = 0; objects != null && i < objects.length; i++)
                comboCriteria.addItem(objects[i]);
            
            comboCriteria.setRenderer(ComboBoxRenderer.getInstance());
            comboCriteria.setEditable(false);
            
        } else if (c instanceof JComboBox) {
            JComboBox combo = (JComboBox) c;
            for (int i = 0; i < combo.getItemCount(); i++)
                comboCriteria.addItem(combo.getItemAt(i));
            
            comboCriteria.setRenderer(combo.getRenderer());
            comboCriteria.setEditable(false);
        } else {
        	comboCriteria.addItem(" ");
            comboCriteria.setEditable(true);
        }

        comboCriteria.setEnabled(true);
        comboCriteria.addItem(DcResources.getText("lblEmptySearchValue"));

        revalidate();
    }
    
    private void applySelectedField() {
        comboCriteria.removeAllItems();
        DcField field = (DcField) comboFields.getSelectedItem();
        
        if (field != null) {
            setSearchField(field);
            module.getSettings().set(DcRepository.ModuleSettings.stQuickFilterDefaultField, field.getIndex());
        }
    }
    
    private void applySelectedFilter() {
        DataFilter df = comboFilters.getSelectedIndex() > 0 ? (DataFilter) comboFilters.getSelectedItem() : null;
        search(df);
    }
    
    private void build() {
        for (DcField field : module.getFields()) {
            if (field.isSearchable() && field.isEnabled())
                comboFields.addItem(field);
        }
        
        Collection<DataFilter> filters = DataFilters.get(module.getIndex());
        comboFilters.addItem(" ");
        for (DataFilter df : filters)
            comboFilters.addItem(df);
        
        comboCriteria.getEditor().getEditorComponent().addKeyListener(this);
        comboCriteria.getEditor().getEditorComponent().addMouseListener(this);
        comboCriteria.setRenderer(new DefaultListCellRenderer());
        
        comboFilters.addActionListener(this);
        comboFilters.setActionCommand("filterSelected");
        
        comboFields.addActionListener(this);
        comboFields.setActionCommand("fieldSelected");
        
        comboFilters.setPreferredSize(new Dimension(200, ComponentFactory.getPreferredFieldHeight()));
        comboFields.setPreferredSize(new Dimension(200, ComponentFactory.getPreferredFieldHeight()));
        
        add(ComponentFactory.getLabel(DcResources.getText("lblQuickFilter") + " "));
        add(comboFields);
        add(comboCriteria);

        JButton buttonCancel = ComponentFactory.getIconButton(IconLibrary._icoRemove);
        buttonCancel.setActionCommand("cancel");
        buttonCancel.addActionListener(this);
        
        JButton button1 = ComponentFactory.getIconButton(IconLibrary._icoAccept);
        button1.setActionCommand("search");
        button1.addActionListener(this);

        JButton button2 = ComponentFactory.getIconButton(IconLibrary._icoAccept);
        button2.setActionCommand("searchOnSelectedFilter");
        button2.addActionListener(this);

        add(button1);
        add(buttonCancel);
        
        if (filters.size() > 0) {
            addSeparator();
            
            add(ComponentFactory.getLabel(DcResources.getText("lblFilters") + " "));
            add(comboFilters);
            add(button2);
        }
    }    
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("searchOnSelectedFilter"))
            search(comboFilters.getSelectedIndex() > 0 ? (DataFilter) comboFilters.getSelectedItem() : null);    
        else if (ae.getActionCommand().equals("search"))
            search();
        else if (ae.getActionCommand().equals("cancel"))
            search(null);
        else if (ae.getActionCommand().equals("fieldSelected"))
            applySelectedField();
        else if (ae.getActionCommand().equals("filterSelected"))
            applySelectedFilter();
    }
    
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            search();
    }

    public void keyPressed(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {
        if (e.getClickCount() == 2) {
            search();
        }            
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(DcSwingUtilities.setRenderingHint(g));
    }
}
