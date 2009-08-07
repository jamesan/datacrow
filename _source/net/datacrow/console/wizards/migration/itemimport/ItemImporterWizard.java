package net.datacrow.console.wizards.migration.itemimport;

import java.util.ArrayList;
import java.util.List;

import net.datacrow.console.wizards.IWizardPanel;
import net.datacrow.console.wizards.Wizard;
import net.datacrow.console.wizards.WizardException;
import net.datacrow.core.resources.DcResources;

public class ItemImporterWizard extends Wizard {

    public static final int _STEP_MAPPING = 2;
    
	private ItemImporterDefinition definition;
	
	public ItemImporterWizard(int moduleIdx) {
		super(moduleIdx);
		this.definition = new ItemImporterDefinition();
	}
	
	protected ItemImporterDefinition getDefinition() {
		return definition;
	}

	@Override
    protected boolean isRestartSupported() {
	    return false;
    }
	
    @Override
    public void finish() throws WizardException {
        close();
    }

    @Override
    protected String getWizardName() {
        return DcResources.getText("lblMigrationWizard");
    }
    
    @Override
    public void next() throws WizardException {
        if (getDefinition() != null && getDefinition().getReader() != null) {
            if (!getDefinition().getReader().requiresMapping()) {
                if (!skip.contains(Integer.valueOf(_STEP_MAPPING)))
                    skip.add(Integer.valueOf(_STEP_MAPPING));
            } else {
                while (skip.contains(Integer.valueOf(_STEP_MAPPING)))
                    skip.remove(Integer.valueOf(_STEP_MAPPING));
            }
        }
        
        super.next();
    }

    @Override
    protected List<IWizardPanel> getWizardPanels() {
    	List<IWizardPanel> panels = new ArrayList<IWizardPanel>();
    	panels.add(new ItemImporterSelectionPanel(this));
    	panels.add(new ItemImportDefinitionPanel(this));
    	panels.add(new ItemImporterMappingPanel(this));
    	panels.add(new ItemImporterPanel(this));
    	return panels;
    }

    @Override
    protected void initialize() {}

    @Override
    protected void saveSettings() {}
}