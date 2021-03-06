package net.datacrow.console.views;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.core.DcRepository;
import net.datacrow.core.plugin.PluginHelper;
import net.datacrow.core.resources.DcResources;
import net.datacrow.core.security.SecuredUser;
import net.datacrow.core.security.SecurityCentre;
import net.datacrow.settings.DcSettings;

public class ViewActionPanel extends JPanel implements ActionListener {

    private static final FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
    
    protected JPanel panelActionsLeft = new JPanel();
    protected JPanel panelActionsRight = new JPanel();
    
    private JButton buttonSave;
    private JButton buttonClear;
    private JButton buttonCancel;
    
    private JButton buttonRemove;
    private JButton buttonAdd;
    
    private final View view;
    
    public ViewActionPanel(View view) {
        this.view = view;
        build();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        buttonClear.setEnabled(enabled);
        buttonRemove.setEnabled(enabled);
        buttonSave.setEnabled(enabled);
    }
    
    public void applySettings() {
        panelActionsLeft.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
        panelActionsRight.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
        buttonSave.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
        buttonClear.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
        buttonCancel.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
        buttonRemove.setFont(DcSettings.getFont(DcRepository.Settings.stSystemFontNormal));
    }
    
    private void build() {
        panelActionsLeft.setLayout(layout);
        panelActionsRight.setLayout(layout);
    
        // Create the components
        buttonAdd = ComponentFactory.getButton(DcResources.getText("lblAdd"));
        buttonClear = ComponentFactory.getButton(DcResources.getText("lblClear"));
        buttonCancel = ComponentFactory.getButton(DcResources.getText("lblCancel"));
        buttonSave = ComponentFactory.getButton(DcResources.getText("lblSave"));
        buttonRemove = ComponentFactory.getButton(DcResources.getText("lblRemove"));
    
        buttonAdd.setToolTipText(DcResources.getText("tpAddRow"));
        buttonClear.setToolTipText(DcResources.getText("tpClear"));
        buttonCancel.setToolTipText(DcResources.getText("tpCancel"));
        buttonSave.setToolTipText(DcResources.getText("tpSaveChanges"));
        buttonRemove.setToolTipText(DcResources.getText("tpRemoveRow"));
    
        PluginHelper.addListener(buttonAdd, "AddRow", view.getModule().getIndex(), view.getType());
        
        buttonClear.addActionListener(this);
        buttonClear.setActionCommand("clear");
        
        buttonCancel.addActionListener(this);
        buttonCancel.setActionCommand("cancelTask");
        
        PluginHelper.addListener(buttonSave, "SaveAll", view.getModule().getIndex(), view.getType());
        PluginHelper.addListener(buttonRemove, "RemoveRow", view.getModule().getIndex(), view.getType());
    
        // Build the panel
        SecuredUser user = SecurityCentre.getInstance().getUser();
            
        if (view.getType() == View._TYPE_INSERT)
            panelActionsRight.add(buttonClear);
        
        if (user == null || user.isEditingAllowed(view.getModule()))
        	panelActionsRight.add(buttonSave);
        
        buttonSave.setEnabled(user == null || user.isEditingAllowed(view.getModule()));
        
        if (	view.getType() == View._TYPE_INSERT &&
                (user == null || user.isEditingAllowed(view.getModule()))) { 
            panelActionsLeft.add(buttonRemove);
            panelActionsLeft.add(buttonAdd);
        }
    
        for (Component c: view.getAdditionalActions())
            panelActionsLeft.add(c);
    
//        if (user == null || user.isEditingAllowed(view.getModule()))
//        	panelActionsRight.add(buttonCancel);
        
        setLayout(Layout.getGBL());
        add(panelActionsLeft, Layout.getGBC(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(panelActionsRight, Layout.getGBC(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST,
            GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("cancelTask"))
            view.cancelTask();
        else if (e.getActionCommand().equals("clear"))
            view.clear();
    }
}
