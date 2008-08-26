/****************************************************************************** *                                     __                                     * *                              <-----/@@\----->                              * *                             <-< <  \\//  > >->                             * *                               <-<-\ __ /->->                               * *                               Data /  \ Crow                               * *                                   ^    ^                                   * *                              info@datacrow.net                             * *                                                                            * *                       This file is part of Data Crow.                      * *       Data Crow is free software; you can redistribute it and/or           * *        modify it under the terms of the GNU General Public                 * *       License as published by the Free Software Foundation; either         * *              version 3 of the License, or any later version.               * *                                                                            * *        Data Crow is distributed in the hope that it will be useful,        * *      but WITHOUT ANY WARRANTY; without even the implied warranty of        * *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             * *           See the GNU General Public License for more details.             * *                                                                            * *        You should have received a copy of the GNU General Public           * *  License along with this program. If not, see http://www.gnu.org/licenses  * *                                                                            * ******************************************************************************/package net.datacrow.console;import java.awt.Font;import java.awt.GridBagConstraints;import java.awt.Insets;import java.util.ArrayList;import java.util.Collection;import javax.swing.JMenuBar;import javax.swing.JTabbedPane;import javax.swing.SwingUtilities;import javax.swing.event.ChangeEvent;import javax.swing.event.ChangeListener;import net.datacrow.console.components.DcFrame;import net.datacrow.console.components.DcPanel;import net.datacrow.console.components.panels.ChartPanel;import net.datacrow.console.components.panels.ModuleListPanel;import net.datacrow.console.components.renderers.DcTableHeaderRenderer;import net.datacrow.console.components.renderers.DcTableHeaderRendererRequired;import net.datacrow.console.components.renderers.DcTableRowIndexCellRenderer;import net.datacrow.console.menu.DcQuickFilterToolBar;import net.datacrow.console.menu.DcToolBar;import net.datacrow.console.views.MasterView;import net.datacrow.console.views.View;import net.datacrow.console.windows.AboutDialog;import net.datacrow.console.windows.LogForm;import net.datacrow.console.windows.drivemanager.DriveManagerDialog;import net.datacrow.console.windows.itemforms.DcMinimalisticItemView;import net.datacrow.console.windows.messageboxes.MessageBox;import net.datacrow.console.windows.messageboxes.QuestionBox;import net.datacrow.console.windows.settings.SettingsView;import net.datacrow.console.windows.webserver.WebServerFrame;import net.datacrow.core.DcRepository;import net.datacrow.core.IconLibrary;import net.datacrow.core.data.DataFilters;import net.datacrow.core.data.DataManager;import net.datacrow.core.db.DatabaseManager;import net.datacrow.core.modules.DcModule;import net.datacrow.core.modules.DcModules;import net.datacrow.core.modules.DcPropertyModule;import net.datacrow.core.modules.TemplateModule;import net.datacrow.core.objects.DcLookAndFeel;import net.datacrow.core.plugin.PluginHelper;import net.datacrow.core.resources.DcResources;import net.datacrow.drivemanager.DriveManager;import net.datacrow.filerenamer.FilePatterns;import net.datacrow.settings.DcSettings;import net.datacrow.settings.definitions.DcFieldDefinitions;import org.apache.log4j.Logger;public class MainFrame extends DcFrame {    private static Logger logger = Logger.getLogger(MainFrame.class.getName());    	private static final long serialVersionUID = 9L;	public static final int _SEARCHTAB = 0;    public static final int _INSERTTAB = 1;    private ModuleListPanel moduleListPanel = new ModuleListPanel();    private JTabbedPane tabbedPane = ComponentFactory.getTabbedPane();    private SettingsView settingsView;    private DcToolBar toolBar;    private DcQuickFilterToolBar quickFilterTb;    private DcModule activeModule = null;    private boolean initialized = false;    public MainFrame() {        super("Data Crow", IconLibrary._icoMain);    }        public void initialize() {        this.getContentPane().setLayout(Layout.getGBL());                toggleModuleList(DcSettings.getBoolean(DcRepository.Settings.stShowModuleList));        tabbedPane.getModel().addChangeListener(new TabIndexListener());        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);                getContentPane().add(moduleListPanel, Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL,                             new Insets(0, 0, 0, 0), 0, 0));        getContentPane().add(tabbedPane,      Layout.getGBC( 1, 2, 1, 1, 100.0, 100.0                             ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,                             new Insets(0, 0, 0, 0), 0, 0));        tabbedPane.setPreferredSize(DcSettings.getDimension(DcRepository.Settings.stTabbedPaneSize));        tabbedPane.setSize(DcSettings.getDimension(DcRepository.Settings.stTabbedPaneSize));        if (DcModules.getCurrent() == null)            changeModule(DcModules.get(DcModules._MOVIE).getIndex());        else            changeModule(DcModules.getCurrent().getIndex());                applySettings();                validate();                setResizable(true);        setSize(DcSettings.getDimension(DcRepository.Settings.stMainViewSize));        setExtendedState(DcSettings.getInt(DcRepository.Settings.stMainViewState));        registerShortCuts();        pack();        setCenteredLocation();        initialized = true;    }        private void registerShortCuts() {        PluginHelper.registerKey(getRootPane(), "Report");        PluginHelper.registerKey(getRootPane(), "Filter");        PluginHelper.registerKey(getRootPane(), "ApplyFilter");        PluginHelper.registerKey(getRootPane(), "FileImport");        PluginHelper.registerKey(getRootPane(), "Log");        PluginHelper.registerKey(getRootPane(), "Settings");        PluginHelper.registerKey(getRootPane(), "CreateNew");        PluginHelper.registerKey(getRootPane(), "OpenItem");        PluginHelper.registerKey(getRootPane(), "FileLauncher");        PluginHelper.registerKey(getRootPane(), "ResourceEditor");        PluginHelper.registerKey(getRootPane(), "SaveAll");        PluginHelper.registerKey(getRootPane(), "FieldSettings");        PluginHelper.registerKey(getRootPane(), "OnlineSearch");        PluginHelper.registerKey(getRootPane(), "NewItemWizard");        PluginHelper.registerKey(getRootPane(), "UndoFilter");                PluginHelper.registerKey(getRootPane(), "ChangeView", MasterView._TABLE_VIEW, -1);        PluginHelper.registerKey(getRootPane(), "ChangeView", MasterView._LIST_VIEW, -1);        PluginHelper.registerKey(getRootPane(), "ToggleQuickFilterBar");        PluginHelper.registerKey(getRootPane(), "ToggleModuleList");        PluginHelper.registerKey(getRootPane(), "ToggleQuickView");        PluginHelper.registerKey(getRootPane(), "ToggleGroupingPane");        PluginHelper.registerKey(getRootPane(), "ToggleToolbarLabels");        for (DcModule module : DcModules.getModules()) {            if (module.isTopModule() && module.getKeyStroke() != null)                PluginHelper.registerKey(getRootPane(), "OpenModule", -1, module.getIndex());        }            }        public DcToolBar getToolBar() {        return toolBar;    }    public void setSelectedTab(int index) {    	tabbedPane.setSelectedIndex(index);    }    public int getSelectedTab() {        return tabbedPane.getSelectedIndex();    }    public void showSettingsView() {        settingsView = DcSettings.getView();    	settingsView.setVisible(true);        settingsView.dispose();        settingsView = null;    }    public void showAboutDialog() {        new AboutDialog(this).setVisible(true);    }    private void toggleModuleList(boolean enable) {        moduleListPanel.setVisible(enable);    }    public void toggleModuleList() {    	moduleListPanel.setVisible(!moduleListPanel.isVisible());        DcSettings.set(DcRepository.Settings.stShowModuleList, moduleListPanel.isVisible());    }    public void updateQuickFilterBar() {        boolean show = DcSettings.getBoolean(DcRepository.Settings.stShowQuickFilterBar);                if (quickFilterTb != null)            getContentPane().remove(quickFilterTb);                if (show) {            quickFilterTb = new DcQuickFilterToolBar(activeModule);            getContentPane().add(quickFilterTb, Layout.getGBC( 0, 1, 2, 1, 1.0, 1.0                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL,                     new Insets(0, 0, 0, 0), 0, 0));                             quickFilterTb.revalidate();            quickFilterTb.setFloatable(false);        }                repaint();    }    public void applySettings() {                // apply module specific settings        for (DcModule module : DcModules.getModules()) {            applySettings(module);            }                Font fontNormal = DcSettings.getFont(DcRepository.Settings.stSystemFontNormal);        Font fontSystem = DcSettings.getFont(DcRepository.Settings.stSystemFontBold);                LogForm.getInstance().setFont(fontNormal);        if (DriveManagerDialog.isInitialized())            DriveManagerDialog.getInstance().setFont(fontNormal);        tabbedPane.setFont(fontSystem);        DcTableHeaderRenderer.getInstance().applySettings();        DcTableHeaderRendererRequired.getInstance().applySettings();        DcTableRowIndexCellRenderer.getInstance().applySettings();        moduleListPanel.setFont(fontSystem);        getContentPane().add(moduleListPanel, Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL,                             new Insets(0, 0, 0, 0), 0, 0));                moduleListPanel.setFont(fontSystem);        moduleListPanel.setSelectedModule(DcModules.getCurrent().getIndex());        moduleListPanel.revalidate();        moduleListPanel.setVisible(DcSettings.getBoolean(DcRepository.Settings.stShowModuleList));        moduleListPanel.rebuild();                if (settingsView != null)            settingsView.setFont(fontNormal);                if (toolBar != null) {            toolBar.setFloatable(false);            toolBar.setFont(fontSystem);        }                if (getJMenuBar() != null)            getJMenuBar().setFont(fontSystem);                if (quickFilterTb != null) {            quickFilterTb.setFloatable(false);            quickFilterTb.setFont(fontSystem);        }    }            private void applySettings(DcModule module) {        Font fontNormal = DcSettings.getFont(DcRepository.Settings.stSystemFontNormal);                if (module.getFileRenamerDialog(false) != null)            module.getFileRenamerDialog(false).setFont(fontNormal);                if (module.getChartPanel(false) != null)            module.getChartPanel(false).setFont(fontNormal);                if (initialized) {            if (module.isTopModule() && module.getFilterForm(false) != null)                 module.getFilterForm(false).setFont(fontNormal);        }                if (module instanceof DcPropertyModule) {             DcMinimalisticItemView form = ((DcPropertyModule) module).getForm();            form.setFont(fontNormal);        }        DcFieldDefinitions definitions = module.getFieldDefinitions();        module.getDcObject().applySettings(definitions);        if (module.getSearchView() != null)            module.getSearchView().applySettings();        if (module.getInsertView() != null)            module.getInsertView().applySettings();    }        public void updateLAF(final DcLookAndFeel laf) {        try {            DcSettings.set(DcRepository.Settings.stLookAndFeel, laf);                        ComponentFactory.setLookAndFeel();                tabbedPane.removeAll();                        SwingUtilities.updateComponentTreeUI(moduleListPanel);            SwingUtilities.updateComponentTreeUI(tabbedPane);            SwingUtilities.updateComponentTreeUI(toolBar);                        if (quickFilterTb != null)                SwingUtilities.updateComponentTreeUI(quickFilterTb);                SwingUtilities.updateComponentTreeUI(LogForm.getInstance().getContentPane());                        if (DriveManagerDialog.isInitialized())                SwingUtilities.updateComponentTreeUI(DriveManagerDialog.getInstance().getContentPane());                        if (WebServerFrame.isInitialized())                 SwingUtilities.updateComponentTreeUI(WebServerFrame.getInstance().getContentPane());                        SwingUtilities.updateComponentTreeUI(getContentPane());                        if (initialized && DcModules.getCurrent().getFilterForm(false) != null)                SwingUtilities.updateComponentTreeUI(DcModules.getCurrent().getFilterForm(false).getContentPane());                        SwingUtilities.updateComponentTreeUI(DcTableHeaderRenderer.getInstance().getButton());            SwingUtilities.updateComponentTreeUI(DcTableHeaderRendererRequired.getInstance().getButton());            SwingUtilities.updateComponentTreeUI(DcTableRowIndexCellRenderer.getInstance().getButton());            if (getJMenuBar() != null)                SwingUtilities.updateComponentTreeUI(getJMenuBar());                        for (DcModule module : DcModules.getModules()) {                                if (module.getChartPanel(false) != null)                    SwingUtilities.updateComponentTreeUI(module.getChartPanel(false));                                if (initialized) {                    if (module.isTopModule() && module.getFilterForm(false) != null)                         SwingUtilities.updateComponentTreeUI(module.getFilterForm(false).getContentPane());                }                                if (module.getFileRenamerDialog(false) != null)                    SwingUtilities.updateComponentTreeUI(module.getFileRenamerDialog(false).getContentPane());                                                if (module instanceof DcPropertyModule) {                     DcMinimalisticItemView view = ((DcPropertyModule) module).getForm();                    if (view != null)                          SwingUtilities.updateComponentTreeUI(((DcPropertyModule) module).getForm().getContentPane());                }                                    if (module instanceof TemplateModule)                    SwingUtilities.updateComponentTreeUI(((TemplateModule) module).getForm().getContentPane());                                MasterView[] views = module.getViews();                if (views != null) {                    for (int i = 0; i < views.length; i++)  {                        MasterView view = views[i];                        if (view.get(MasterView._LIST_VIEW) != null)                            SwingUtilities.updateComponentTreeUI(view.get(MasterView._LIST_VIEW));                        if (view.get(MasterView._TABLE_VIEW) != null)                            SwingUtilities.updateComponentTreeUI(view.get(MasterView._TABLE_VIEW));                    }                }            }                                changeModule(DcModules.getCurrent().getIndex());            setViews();                        applySettings();                    validate();        } catch (Exception e) {            logger.error(e, e);        }    }            public void applyView(final int newView) {        int previousView = DcSettings.getInt(DcRepository.Settings.stSelectedView);        if (previousView != newView) {            DcSettings.set(DcRepository.Settings.stSelectedView, newView);            tabbedPane.removeAll();            MasterView[] masterViews = activeModule.getViews();            for (int i = 0; i < masterViews.length; i++) {                View view = masterViews[i].getCurrent();                tabbedPane.addTab(view.getTitle(), view.getIcon(), view);                view.applyViewDividerLocation();            }                        MasterView searchView = activeModule.getSearchView();            if (searchView != null && searchView.getItemCount() == 0)                DataManager.bindData(searchView, activeModule.getIndex(), DataFilters.getCurrent(activeModule.getIndex()));                        ChartPanel chartPanel = DcModules.getCurrent().getChartPanel(true);            if (chartPanel.isSupported())                tabbedPane.addTab(chartPanel.getTitle(), chartPanel.getIcon(), chartPanel);            else                 chartPanel.clear();        }    }        public void updateMenuBar() {        JMenuBar menubar = getJMenuBar();        ComponentFactory.clean(menubar);        setJMenuBar(null);                setJMenuBar(activeModule.getMenuBar());        repaint();        validate();    }    public void changeModule(final int index) {        activeModule = DcModules.get(index);        activeModule = activeModule == null ? DcModules.get(DcModules._SOFTWARE) : activeModule;                while (!activeModule.isEnabled()) {        	for (DcModule m : DcModules.getModules()) {        		if (m.isTopModule() && m.isEnabled()) {        			activeModule = m;        			break;        		}        	}        }                DcSettings.set(DcRepository.Settings.stModule, activeModule.getIndex());        moduleListPanel.setSelectedModule(index);                updateMenuBar();        if (toolBar != null)            getContentPane().remove(toolBar);        toolBar = new DcToolBar(activeModule);        getContentPane().add(toolBar, Layout.getGBC( 0, 0, 2, 1, 1.0, 1.0                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL,                             new Insets(0, 0, 0, 0), 0, 0));                updateQuickFilterBar();        tabbedPane.removeAll();        repaint();    }        public void setViews() {        try {            MasterView[] masterViews = activeModule.getViews();            for (int i = 0; i < masterViews.length; i++) {                View view = masterViews[i].getCurrent();                if (view != null) {                    tabbedPane.addTab(view.getTitle(), view.getIcon(), view);                    view.applyViewDividerLocation();                }            }            if (activeModule.getSearchView() != null && activeModule.getSearchView().getItemCount() == 0)                DataManager.bindData(activeModule.getSearchView(),                                      activeModule.getIndex(),                                      DataFilters.getCurrent(activeModule.getIndex()));                        ChartPanel chartPanel = DcModules.getCurrent().getChartPanel(true);                        if (chartPanel != null) {                if (chartPanel.isSupported())                    tabbedPane.addTab(chartPanel.getTitle(), chartPanel.getIcon(), chartPanel);                else                     chartPanel.clear();            }                    } catch (Exception e) {            logger.error("An error occurred while appending items to the view(s)", e);        }    }    @Override    public void close() {        finish(true, true);    }        public void finish(boolean checkForChanges, boolean saveSettings) {        if (DatabaseManager.getQueueSize() > 0) {            new MessageBox(DcResources.getText("msgCannotExitQueriesInQueue"),                            MessageBox._INFORMATION);            return;        }        DriveManager.getInstance().stopScanners();        DriveManager.getInstance().stopDrivePoller();        DriveManager.getInstance().stopFileSynchronizer();        if (checkForChanges) {            Collection<View> viewsWithChanges = new ArrayList<View>();            for (DcModule module : DcModules.getModules()) {                Collection<MasterView> masterViews = new ArrayList<MasterView>();                if (module.getInsertView() != null)                    masterViews.add(module.getInsertView());                if (module.getSearchView() != null)                    masterViews.add(module.getSearchView());                                for (MasterView masterView : masterViews) {                    for (View view: masterView.getViews()) {                        if (!view.isChangesSaved())                            viewsWithChanges.add(view);                    }                }            }                        if (viewsWithChanges.size() > 0) {                QuestionBox qb = new QuestionBox(DcResources.getText("msgCancelExitAndSave"));                if (qb.isAffirmative()) {                    for (View view : viewsWithChanges)                        view.save();                    return;                }            }         }                logger.info(DcResources.getText("msgApplicationStops"));                setVisible(false);        DataManager.serialize();                DatabaseManager.closeDatabases(false);        if (saveSettings) {            DcSettings.set(DcRepository.Settings.stTabbedPaneSize, tabbedPane.getSize());            DcSettings.set(DcRepository.Settings.stMainViewSize, getSize());            DcSettings.set(DcRepository.Settings.stMainViewState, getExtendedState());                        DataFilters.save();            FilePatterns.save();            DcSettings.save();            DcModules.save();        }                System.exit(0);    }        private class TabIndexListener implements ChangeListener {        public void stateChanged(ChangeEvent ce) {            int index = tabbedPane.getSelectedIndex();            if (index > -1) {            	DcPanel panel = (DcPanel) tabbedPane.getComponent(index);            	setHelpIndex(panel.getHelpIndex());                if (panel instanceof View)                    ((View) panel).applyViewDividerLocation();            }	    }    }}