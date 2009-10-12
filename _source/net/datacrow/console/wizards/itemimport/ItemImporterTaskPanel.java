package net.datacrow.console.wizards.itemimport;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import net.datacrow.console.Layout;
import net.datacrow.console.components.panels.TaskPanel;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.data.DataManager;
import net.datacrow.core.migration.itemimport.IItemImporterClient;
import net.datacrow.core.migration.itemimport.ItemImporter;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.ValidationException;
import net.datacrow.core.resources.DcResources;

import org.apache.log4j.Logger;

public class ItemImporterTaskPanel extends ItemImporterWizardPanel implements IItemImporterClient  {

	private static Logger logger = Logger.getLogger(ItemImporterTaskPanel.class.getName());
	
	private int created = 0;
	private int updated = 0;
	
    private ItemImporterWizard wizard;
    private ItemImporter importer;
    
    private TaskPanel tp = new TaskPanel(TaskPanel._SINGLE_PROGRESSBAR);
    
    public ItemImporterTaskPanel(ItemImporterWizard wizard) {
        this.wizard = wizard;
        build();
    }
    
	public Object apply() throws WizardException {
        return wizard.getDefinition();
    }

    public void destroy() {
    	if (importer != null) importer.cancel();
    	importer = null;
        if (tp != null) tp.destroy();
        tp = null;
    	wizard = null;
    }

    public String getHelpText() {
        return DcResources.getText("msgImportProcess");
    }
    
    @Override
    public void onActivation() {
    	if (wizard.getDefinition() != null) {
    		this.importer = wizard.getDefinition().getImporter();
    		start();
    	}
	}

    @Override
	public void onDeactivation() {
		cancel();
	}

    private void start() {
    	importer.setClient(this);
    	
    	try { 
    	    
    	    created = 0;
    	    updated = 0;
    	    
    	    if (importer.getFile() == null)
    	        importer.setFile(wizard.getDefinition().getFile());
    	    
    	    importer.start();
    	    
    	} catch (Exception e ) {
    	    notifyMessage(e.getMessage());
    	    logger.error(e, e);
    	}
    }
    
    private void build() {
        setLayout(Layout.getGBL());
        add(tp,  Layout.getGBC( 0, 01, 1, 1, 1.0, 1.0
                ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                 new Insets( 5, 5, 5, 5), 0, 0));
    }
    
    private void cancel() {
        if (importer != null) importer.cancel();
        notifyStopped();
    }    
    
    public void notifyMessage(String msg) {
        tp.addMessage(msg);
    }

    public void notifyStarted(int count) {
        tp.clear();
        tp.initializeTask(count);
    }

    public void notifyStopped() {
        notifyMessage("\n");
        notifyMessage(DcResources.getText("msgItemsCreated", String.valueOf(created)));
        notifyMessage(DcResources.getText("msgItemsUpdated", String.valueOf(updated)));
        notifyMessage(DcResources.getText("msgItemsImported", String.valueOf(updated + created)));
        notifyMessage("\n");
        notifyMessage(DcResources.getText("msgImportFinished"));
    }

    public void notifyProcessed(DcObject item) {
        if (    1 == 0 &&
                wizard != null && 
                wizard.getModule().isSelectableInUI() && 
                wizard.getModule().getCurrentInsertView() != null) {
            
            wizard.getModule().getCurrentInsertView().add(item);
        } else {
            DcObject other = DataManager.getObjectForDisplayValue(item.getModule().getIndex(), item.toString());
            // Check if the item exists and if so, update the item with the found values. Else just create a new item.
            // This is to make sure the order in which XML files are processed (first software, then categories)
            // is of no importance (!).
            try {
                if (other != null) {
                    updated++;
                    other.copy(item, true);
                    other.saveUpdate(true, false);
                } else {
                    created++;
                    item.setValidate(false);
                    item.saveNew(true);
                }
            } catch (ValidationException ve) {
                // will not occur as validation has been disabled.
                notifyMessage(ve.getMessage());
            }
        }
        
        tp.updateProgressTask();
        notifyMessage(DcResources.getText("msgImportedX", item.toString()));
    }
}