/****************************************************************************** *                                     __                                     * *                              <-----/@@\----->                              * *                             <-< <  \\//  > >->                             * *                               <-<-\ __ /->->                               * *                               Data /  \ Crow                               * *                                   ^    ^                                   * *                              info@datacrow.net                             * *                                                                            * *                       This file is part of Data Crow.                      * *       Data Crow is free software; you can redistribute it and/or           * *        modify it under the terms of the GNU General Public                 * *       License as published by the Free Software Foundation; either         * *              version 3 of the License, or any later version.               * *                                                                            * *        Data Crow is distributed in the hope that it will be useful,        * *      but WITHOUT ANY WARRANTY; without even the implied warranty of        * *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             * *           See the GNU General Public License for more details.             * *                                                                            * *        You should have received a copy of the GNU General Public           * *  License along with this program. If not, see http://www.gnu.org/licenses  * *                                                                            * ******************************************************************************/package net.datacrow.console;import java.awt.Dimension;import java.awt.Font;import java.awt.GridBagConstraints;import java.awt.Insets;import java.util.ArrayList;import java.util.Collection;import javax.swing.JMenuBar;import javax.swing.JTabbedPane;import javax.swing.SwingUtilities;import javax.swing.event.ChangeEvent;import javax.swing.event.ChangeListener;import net.datacrow.console.components.DcPanel;import net.datacrow.console.components.panels.ChartPanel;import net.datacrow.console.components.panels.ModuleListPanel;import net.datacrow.console.components.renderers.DcTableHeaderRenderer;import net.datacrow.console.components.renderers.DcTableHeaderRendererRequired;import net.datacrow.console.components.renderers.DcTableRowIndexCellRenderer;import net.datacrow.console.menu.DcQuickFilterToolBar;import net.datacrow.console.menu.DcToolBar;import net.datacrow.console.views.MasterView;import net.datacrow.console.views.View;import net.datacrow.console.windows.AboutDialog;import net.datacrow.console.windows.DcFrame;import net.datacrow.console.windows.LookAndFeelDialog;import net.datacrow.console.windows.drivemanager.DriveManagerDialog;import net.datacrow.console.windows.itemforms.DcMinimalisticItemView;import net.datacrow.console.windows.log.LogPanel;import net.datacrow.console.windows.settings.SettingsView;import net.datacrow.console.windows.webserver.WebServerFrame;import net.datacrow.core.DataCrow;import net.datacrow.core.DcRepository;import net.datacrow.core.IconLibrary;import net.datacrow.core.UserMode;import net.datacrow.core.data.DataFilters;import net.datacrow.core.data.DataManager;import net.datacrow.core.db.DatabaseManager;import net.datacrow.core.modules.DcModule;import net.datacrow.core.modules.DcModules;import net.datacrow.core.modules.DcPropertyModule;import net.datacrow.core.modules.TemplateModule;import net.datacrow.core.objects.DcLookAndFeel;import net.datacrow.core.plugin.PluginHelper;import net.datacrow.core.resources.DcResources;import net.datacrow.core.security.SecurityCentre;import net.datacrow.drivemanager.DriveManager;import net.datacrow.filerenamer.FilePatterns;import net.datacrow.settings.DcSettings;import net.datacrow.settings.Settings;import net.datacrow.util.DcSwingUtilities;import net.datacrow.util.Utilities;import org.apache.log4j.Logger;/** * Main GUI.  *  * @author Robert Jan van der Waals */public class MainFrame extends DcFrame {    private static Logger logger = Logger.getLogger(MainFrame.class.getName());    	private static final long serialVersionUID = 9L;	public static final int _SEARCHTAB = 0;    public static final int _INSERTTAB = 1;    private ModuleListPanel moduleListPanel = new ModuleListPanel();    private JTabbedPane tabbedPane = ComponentFactory.getTabbedPane();    private SettingsView settingsView;    private DcToolBar toolBar;    private DcQuickFilterToolBar quickFilterTb;    private DcModule activeModule = null;    private boolean initialized = false;    private boolean onExitSaveSettings = true;    private boolean onExitCheckForChanges = true;    public MainFrame() {        super("", IconLibrary._icoMain);        setTitle();    }        public void setTitle() {        int usermode = DcSettings.getInt(DcRepository.Settings.stXpMode);        setTitle(DataCrow.getVersion().getFullString() + "     (" + DcResources.getText("lblXpMode") + " = " +                 (usermode == UserMode._XP_BEGINNER ? DcResources.getText("lblBeginnerMode") : DcResources.getText("lblExpertMode")) + ")" +                 "    " + DcResources.getText("lblUser") + " = " + SecurityCentre.getInstance().getUser().getUser().getName());    }        public void initialize() {        this.getContentPane().setLayout(Layout.getGBL());                toggleModuleList(DcSettings.getBoolean(DcRepository.Settings.stShowModuleList));        tabbedPane.getModel().addChangeListener(new TabIndexListener());        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);                getContentPane().add(moduleListPanel, Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL,                             new Insets(0, 0, 0, 0), 0, 0));        getContentPane().add(tabbedPane,      Layout.getGBC( 1, 2, 1, 1, 100.0, 100.0                             ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,                             new Insets(0, 0, 0, 0), 0, 0));        tabbedPane.setPreferredSize(DcSettings.getDimension(DcRepository.Settings.stTabbedPaneSize));        tabbedPane.setSize(DcSettings.getDimension(DcRepository.Settings.stTabbedPaneSize));        if (DcModules.getCurrent() == null) {            changeModule(DcModules.get(DcModules._MOVIE).getIndex());        } else {            int moduleIdx = DcModules.getCurrent().getIndex() == DcModules._ITEM ? DcModules._CONTAINER : DcModules.getCurrent().getIndex();            changeModule(moduleIdx);        }                applySettings();                validate();                setResizable(true);        setSize(DcSettings.getDimension(DcRepository.Settings.stMainViewSize));        registerShortCuts();        pack();        if (DcSettings.getDimension(DcRepository.Settings.stMainViewLocation) == null) {            setCenteredLocation();        } else {            Dimension loc = DcSettings.getDimension(DcRepository.Settings.stMainViewLocation);            setLocation(loc.width, loc.height);        }        int viewState = DcSettings.getInt(DcRepository.Settings.stMainViewState);        setExtendedState(viewState);                        initialized = true;    }        public void setOnExitCheckForChanges(boolean b) {        onExitCheckForChanges = b;    }    public void setOnExitSaveSettings(boolean b) {        onExitSaveSettings = b;    }        private void registerShortCuts() {        PluginHelper.registerKey(getRootPane(), "Report");        PluginHelper.registerKey(getRootPane(), "Filter");        PluginHelper.registerKey(getRootPane(), "ApplyFilter");        PluginHelper.registerKey(getRootPane(), "FileImport");        PluginHelper.registerKey(getRootPane(), "Log");        PluginHelper.registerKey(getRootPane(), "Settings");        PluginHelper.registerKey(getRootPane(), "CreateNew");        PluginHelper.registerKey(getRootPane(), "OpenItem");        PluginHelper.registerKey(getRootPane(), "FileLauncher");        PluginHelper.registerKey(getRootPane(), "ResourceEditor");        PluginHelper.registerKey(getRootPane(), "SaveAll");        PluginHelper.registerKey(getRootPane(), "FieldSettings");        PluginHelper.registerKey(getRootPane(), "OnlineSearch");        PluginHelper.registerKey(getRootPane(), "NewItemWizard");        PluginHelper.registerKey(getRootPane(), "UndoFilter");                PluginHelper.registerKey(getRootPane(), "ChangeView", MasterView._TABLE_VIEW, -1);        PluginHelper.registerKey(getRootPane(), "ChangeView", MasterView._LIST_VIEW, -1);        PluginHelper.registerKey(getRootPane(), "ToggleQuickFilterBar");        PluginHelper.registerKey(getRootPane(), "ToggleModuleList");        PluginHelper.registerKey(getRootPane(), "ToggleQuickView");        PluginHelper.registerKey(getRootPane(), "ToggleGroupingPane");        PluginHelper.registerKey(getRootPane(), "ToggleToolbarLabels");        for (DcModule module : DcModules.getModules()) {            if (module.isTopModule() && module.getKeyStroke() != null)                PluginHelper.registerKey(getRootPane(), "OpenModule", -1, module.getIndex());        }            }        public DcToolBar getToolBar() {        return toolBar;    }    public void setSelectedTab(int index) {    	tabbedPane.setSelectedIndex(index);    }    public int getSelectedTab() {        return tabbedPane.getSelectedIndex();    }    public void showSettingsView() {        settingsView = DcSettings.getView();    	settingsView.setVisible(true);        settingsView.dispose();        settingsView = null;    }    public void showAboutDialog() {        new AboutDialog(this).setVisible(true);    }    private void toggleModuleList(boolean enable) {        moduleListPanel.setVisible(enable);    }    public void toggleModuleList() {    	moduleListPanel.setVisible(!moduleListPanel.isVisible());        DcSettings.set(DcRepository.Settings.stShowModuleList, moduleListPanel.isVisible());    }    public void updateQuickFilterBar() {    	boolean b = DcSettings.getBoolean(DcRepository.Settings.stShowQuickFilterBar);        quickFilterTb.setVisible(b);        repaint();    }    public void rebuildQuickFilterBar() {        if (quickFilterTb != null) getContentPane().remove(quickFilterTb);        quickFilterTb = new DcQuickFilterToolBar(activeModule);        getContentPane().add(quickFilterTb, Layout.getGBC( 0, 1, 2, 1, 1.0, 1.0                ,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL,                 new Insets(0, 0, 0, 0), 0, 0));        quickFilterTb.setVisible(DcSettings.getBoolean(DcRepository.Settings.stShowQuickFilterBar));        quickFilterTb.revalidate();        quickFilterTb.setFloatable(false);    }    public void applySettings() {        // apply module specific settings        for (DcModule module : DcModules.getModules()) {            applySettings(module);            }                Font fontNormal = DcSettings.getFont(DcRepository.Settings.stSystemFontNormal);        Font fontSystem = DcSettings.getFont(DcRepository.Settings.stSystemFontBold);                LogPanel.getInstance().setFont(fontNormal);        if (DriveManagerDialog.isInitialized())            DriveManagerDialog.getInstance().setFont(fontNormal);        tabbedPane.setFont(fontSystem);        DcTableHeaderRenderer.getInstance().applySettings();        DcTableHeaderRendererRequired.getInstance().applySettings();        DcTableRowIndexCellRenderer.getInstance().applySettings();        moduleListPanel.setFont(fontSystem);        getContentPane().add(moduleListPanel, Layout.getGBC( 0, 2, 1, 1, 1.0, 1.0                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL,                             new Insets(0, 0, 0, 0), 0, 0));                moduleListPanel.setFont(fontSystem);                int moduleIdx = DcModules.getCurrent().getIndex() == DcModules._ITEM ? DcModules._CONTAINER : DcModules.getCurrent().getIndex();                moduleListPanel.setSelectedModule(moduleIdx);        moduleListPanel.revalidate();        moduleListPanel.setVisible(DcSettings.getBoolean(DcRepository.Settings.stShowModuleList));        moduleListPanel.rebuild();                if (settingsView != null)            settingsView.setFont(fontNormal);                if (toolBar != null) {            toolBar.setFloatable(false);            toolBar.setFont(fontSystem);        }                if (getJMenuBar() != null)            getJMenuBar().setFont(fontSystem);                if (quickFilterTb != null) {            quickFilterTb.setFloatable(false);            quickFilterTb.setFont(fontSystem);        }    }            private void applySettings(DcModule module) {        Font fontNormal = DcSettings.getFont(DcRepository.Settings.stSystemFontNormal);                if (module.getFileRenamerDialog(false) != null)            module.getFileRenamerDialog(false).setFont(fontNormal);                if (module.getChartPanel(false) != null)            module.getChartPanel(false).setFont(fontNormal);                if (initialized) {            if (module.isTopModule() && module.getFilterForm(false) != null)                 module.getFilterForm(false).setFont(fontNormal);        }                if (module instanceof DcPropertyModule) {             DcMinimalisticItemView form = ((DcPropertyModule) module).getForm();            form.setFont(fontNormal);        }        module.applySettings();        if (module.getSearchView() != null)            module.getSearchView().applySettings();        if (module.getInsertView() != null)            module.getInsertView().applySettings();    }        public void updateLAF(final DcLookAndFeel laf) {        try {            DcSettings.set(DcRepository.Settings.stLookAndFeel, laf);                        ComponentFactory.setLookAndFeel();                tabbedPane.removeAll();                        SwingUtilities.updateComponentTreeUI(moduleListPanel);            SwingUtilities.updateComponentTreeUI(tabbedPane);            SwingUtilities.updateComponentTreeUI(toolBar);                        if (quickFilterTb != null)                SwingUtilities.updateComponentTreeUI(quickFilterTb);                SwingUtilities.updateComponentTreeUI(LogPanel.getInstance());                        if (DriveManagerDialog.isInitialized())                SwingUtilities.updateComponentTreeUI(DriveManagerDialog.getInstance().getContentPane());                        if (WebServerFrame.isInitialized())                 SwingUtilities.updateComponentTreeUI(WebServerFrame.getInstance().getContentPane());                        SwingUtilities.updateComponentTreeUI(getContentPane());                        if (initialized && DcModules.getCurrent().getFilterForm(false) != null)                SwingUtilities.updateComponentTreeUI(DcModules.getCurrent().getFilterForm(false).getContentPane());                        SwingUtilities.updateComponentTreeUI(DcTableHeaderRenderer.getInstance().getButton());            SwingUtilities.updateComponentTreeUI(DcTableHeaderRendererRequired.getInstance().getButton());            SwingUtilities.updateComponentTreeUI(DcTableRowIndexCellRenderer.getInstance().getButton());            if (getJMenuBar() != null)                SwingUtilities.updateComponentTreeUI(getJMenuBar());                        for (DcModule module : DcModules.getModules()) {                                if (module.getChartPanel(false) != null)                    SwingUtilities.updateComponentTreeUI(module.getChartPanel(false));                                if (initialized) {                    if (module.isTopModule() && module.getFilterForm(false) != null)                         SwingUtilities.updateComponentTreeUI(module.getFilterForm(false).getContentPane());                }                                if (module.getFileRenamerDialog(false) != null)                    SwingUtilities.updateComponentTreeUI(module.getFileRenamerDialog(false).getContentPane());                                if (module.getType() == DcModule._TYPE_PROPERTY_MODULE) {                     DcMinimalisticItemView view = ((DcPropertyModule) module).getForm();                    if (view != null)                          SwingUtilities.updateComponentTreeUI(((DcPropertyModule) module).getForm().getContentPane());                }                                    if (module.getType() == DcModule._TYPE_TEMPLATE_MODULE)                    SwingUtilities.updateComponentTreeUI(((TemplateModule) module).getForm().getContentPane());                                MasterView[] views = module.getViews();                if (views != null) {                    for (int i = 0; i < views.length; i++)  {                        MasterView view = views[i];                        if (view.get(MasterView._LIST_VIEW) != null)                            SwingUtilities.updateComponentTreeUI(view.get(MasterView._LIST_VIEW));                        if (view.get(MasterView._TABLE_VIEW) != null)                            SwingUtilities.updateComponentTreeUI(view.get(MasterView._TABLE_VIEW));                    }                }            }                        try {                super.close();            } catch (Exception e) {                setVisible(false);            }                        DataCrow.mainFrame = new MainFrame();            DataCrow.mainFrame.initialize();            DataCrow.mainFrame.setVisible(true);                        new LookAndFeelDialog().setVisible(true);                    } catch (Exception e) {            logger.error(e, e);        }    }            public void applyView(int newView) {        Settings settings = DcModules.getCurrent().getSettings();                MasterView searchView = activeModule.getSearchView();                searchView.getCurrent().saveSettings();                settings.set(DcRepository.ModuleSettings.stDefaultView, newView);                tabbedPane.removeAll();        MasterView[] masterViews = activeModule.getViews();        for (int i = 0; i < masterViews.length; i++) {            View view = masterViews[i].getCurrent();            view.activate();                        if (view.getType() == View._TYPE_INSERT && !SecurityCentre.getInstance().getUser().isEditingAllowed(view.getModule()))                continue;                            tabbedPane.addTab(view.getTitle(), view.getIcon(), view);        }                if (searchView != null && !searchView.isLoaded()) {            searchView.add(DataManager.getKeys(DataFilters.getCurrent(activeModule.getIndex())));        } else if (searchView.getGroupingPane() != null && searchView.getGroupingPane().isEnabled()) {        	searchView.getCurrent().clear();        	searchView.getGroupingPane().updateView();        }                ChartPanel chartPanel = DcModules.getCurrent().getChartPanel(true);                if (chartPanel != null) {            if (chartPanel.isSupported() && !DcModules.getCurrent().isAbstract())                tabbedPane.addTab(chartPanel.getTitle(), chartPanel.getIcon(), chartPanel);            else                 chartPanel.clear();        }    }        public void updateMenuBar() {        JMenuBar menubar = getJMenuBar();        ComponentFactory.clean(menubar);        setJMenuBar(null);                setJMenuBar(activeModule.getMenuBar());        repaint();        validate();    }    public void changeModule(final int index) {        activeModule = DcModules.get(index);        activeModule = activeModule == null ? DcModules.get(DcModules._SOFTWARE) : activeModule;        activeModule = activeModule == null ? DcModules.get(DcModules._USER) : activeModule;                while (!activeModule.isEnabled()) {        	for (DcModule m : DcModules.getModules()) {        		if (m.isTopModule() && m.isEnabled()) {        			activeModule = m;        			break;        		}        	}        }                DcSettings.set(DcRepository.Settings.stModule, activeModule.getIndex());        moduleListPanel.setSelectedModule(index);                updateMenuBar();        if (toolBar != null) getContentPane().remove(toolBar);        toolBar = new DcToolBar(activeModule);        toolBar.setVisible(DcSettings.getBoolean(DcRepository.Settings.stShowToolbar));        getContentPane().add(toolBar, 		Layout.getGBC( 0, 0, 2, 1, 1.0, 1.0                            ,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL,                             new Insets(0, 0, 0, 0), 0, 0));        rebuildQuickFilterBar();                tabbedPane.removeAll();        repaint();                // apply the default view for the selected module        applyView(activeModule.getSettings().getInt(DcRepository.ModuleSettings.stDefaultView));    }        public void setViews() {        Settings settings = DcModules.getCurrent().getSettings();                try {            MasterView[] masterViews = activeModule.getViews();            for (int i = 0; i < masterViews.length; i++) {                View view = masterViews[i].getCurrent();                                if (view == null) {                    for (View vw : masterViews[i].getViews())                        settings.set(DcRepository.ModuleSettings.stDefaultView, Long.valueOf(vw.getIndex()));                                         view = masterViews[i].getCurrent();                }                                if (view != null) {                    tabbedPane.addTab(view.getTitle(), view.getIcon(), view);                    view.applyViewDividerLocation();                }            }            ChartPanel chartPanel = DcModules.getCurrent().isAbstract() ? null : DcModules.getCurrent().getChartPanel(true);            if (chartPanel != null) {                if (chartPanel.isSupported())                    tabbedPane.addTab(chartPanel.getTitle(), chartPanel.getIcon(), chartPanel);                else                     chartPanel.clear();            }                    } catch (Exception e) {            logger.error("An error occurred while appending items to the view(s)", e);        }    }        @Override    protected void setCenteredLocation() {        setLocation(Utilities.getCenteredWindowLocation(getSize(), true));    }    /**     * Closes this frame and closed the application.     * The behavior of this method is influences by the check for changes local setting     * and the save settings local setting.     * @see #onExitCheckForChanges     * @see #onExitSaveSettings      */    @Override    public void close() {        if (DatabaseManager.getQueueSize() > 0) {            DcSwingUtilities.displayMessage("msgCannotExitQueriesInQueue");            return;        }        DriveManager.getInstance().stopScanners();        DriveManager.getInstance().stopDrivePoller();        DriveManager.getInstance().stopFileSynchronizer();        if (onExitCheckForChanges) {            Collection<View> viewsWithChanges = new ArrayList<View>();            for (DcModule module : DcModules.getModules()) {                Collection<MasterView> masterViews = new ArrayList<MasterView>();                if (module.hasInsertView())                    masterViews.add(module.getInsertView());                if (module.hasSearchView())                    masterViews.add(module.getSearchView());                                for (MasterView masterView : masterViews) {                    if (masterView != null && masterView.getViews() != null) {                        for (View view: masterView.getViews()) {                            if (!view.isChangesSaved())                                viewsWithChanges.add(view);                        }                                                masterView.saveSettings();                    }                }            }                        if (viewsWithChanges.size() > 0) {                if (!DcSwingUtilities.displayQuestion("msgCancelExitAndSave")) // user does not want to exit                    return;            }         }                logger.info(DcResources.getText("msgApplicationStops"));                setVisible(false);        DatabaseManager.closeDatabases(false);        if (onExitSaveSettings) {            DcSettings.set(DcRepository.Settings.stGracefulShutdown, Boolean.TRUE);            DcSettings.set(DcRepository.Settings.stTabbedPaneSize, tabbedPane.getSize());            DcSettings.set(DcRepository.Settings.stMainViewSize, getSize());            DcSettings.set(DcRepository.Settings.stMainViewState, getExtendedState());            DcSettings.set(DcRepository.Settings.stMainViewLocation, new Dimension(getLocation().x, getLocation().y));                        DataFilters.save();            FilePatterns.save();            DcSettings.save();            DcModules.save();        }                System.exit(0);    }        private class TabIndexListener implements ChangeListener {        @Override        public void stateChanged(ChangeEvent ce) {            int index = tabbedPane.getSelectedIndex();            if (index > -1) {            	DcPanel panel = (DcPanel) tabbedPane.getComponent(index);            	setHelpIndex(panel.getHelpIndex());                if (panel instanceof View)                    ((View) panel).applyViewDividerLocation();            }	    }    }}