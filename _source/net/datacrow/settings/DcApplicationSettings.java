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

package net.datacrow.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;

import javax.swing.JFrame;

import net.datacrow.console.ComponentFactory;
import net.datacrow.core.DataCrow;
import net.datacrow.core.DcRepository;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcLookAndFeel;
import net.datacrow.core.settings.Setting;
import net.datacrow.core.settings.SettingsGroup;
import net.datacrow.settings.definitions.ProgramDefinitions;
import net.datacrow.synchronizers.Synchronizer;

/**
 * Holder for application settings.
 * 
 * @see DcSettings
 * @see DcRepository.Settings
 * 
 * @author Robert Jan van der Waals
 */
public class DcApplicationSettings extends net.datacrow.settings.Settings {
    
    public static final String _General = "lblGroupGeneral";
    public static final String _FileHashing = "lblFileHashing";
    public static final String _Regional = "lblGroupRegional";
    public static final String _Module = "lblModuleSettings";
    public static final String _DriveMappings = "lblDriveMappings";
    public static final String _FileHandlers = "lblProgramDefinitions";
    public static final String _SelectionColor = "lblSelectionColor";
    public static final String _HTTP = "lblHTTPSettings";
    public static final String _Font = "lblFont";

    /**
     * Initializes and loads all settings.
     */
    public DcApplicationSettings() {
        super();
        createSettings();
        getSettings().setSettingsFile(new File(DataCrow.installationDir + "data/data_crow.properties"));
        load();
    }

    @Override
    protected void createGroups() {
        SettingsGroup generalGroup = new SettingsGroup(_General, "dc.settings.general");
        SettingsGroup regionalGroup = new SettingsGroup(_Regional, "dc.settings.regional");
        SettingsGroup colorGroup = new SettingsGroup(_SelectionColor, "dc.settings.colors");
        SettingsGroup http = new SettingsGroup(_HTTP, "dc.settings.http");
        SettingsGroup fileHandlers = new SettingsGroup(_FileHandlers, "dc.settings.fileassociations");
        SettingsGroup moduleGroup = new SettingsGroup(_Module, "dc.settings.module");
        SettingsGroup fontGroup = new SettingsGroup(_Font, "dc.settings.font");
        SettingsGroup fileHashingGroup = new SettingsGroup(_FileHashing, "dc.settings.filehash");
        SettingsGroup driveMappings = new SettingsGroup(_DriveMappings, "dc.settings.drivemappings");

        getSettings().addGroup(_Module, moduleGroup);
        getSettings().addGroup(_Regional, regionalGroup);
        getSettings().addGroup(_General, generalGroup);
        getSettings().addGroup(_Font, fontGroup);
        getSettings().addGroup(_HTTP, http);
        getSettings().addGroup(_SelectionColor, colorGroup);
        getSettings().addGroup(_FileHandlers, fileHandlers);
        getSettings().addGroup(_DriveMappings, driveMappings);
        getSettings().addGroup(_FileHashing, fileHashingGroup);
    }

    protected void createSettings() {
        getSettings().addSetting(_Regional,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stLanguage,
                            "",
                            ComponentFactory._LANGUAGECOMBO,
                            "",
                            "lblLanguage",
                            true,
                            true));   
        getSettings().addSetting(_Regional,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stCheckedForJavaVersion,
                            Boolean.FALSE,
                            -1,
                            "",
                            "lblLanguage",
                            false,
                            false));            
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stGracefulShutdown,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));          
        getSettings().addSetting(_DriveMappings,
                new Setting(DcRepository.ValueTypes._STRINGARRAY,
                            DcRepository.Settings.stDriveMappings,
                            null,
                            ComponentFactory._DRIVEMAPPING,
                            "",
                            "lblDriveMappings",
                            false,
                            true));          
        getSettings().addSetting(_Module,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stModuleSettings,
                            "",
                            ComponentFactory._MODULESELECTOR,
                            "",
                            "",
                            false,
                            true));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stBackupLocation,
                            null,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stOnlineSearchSelectedView,
                            Long.valueOf(0),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stTreeNodeHeight,
                            Long.valueOf(20),
                            -1,
                            "",
                            "",
                            false,
                            false));         
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stTableRowHeight,
                            Long.valueOf(25),
                            -1,
                            "",
                            "",
                            false,
                            false));            
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stImportCharacterSet,
                            "UTF-8",
                            ComponentFactory._CHARACTERSETCOMBO,
                            "",
                            "lblCharacterSet",
                            false,
                            false));           
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stButtonHeight,
                            Long.valueOf(25),
                            -1,
                            "",
                            "",
                            false,
                            false));             
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stPersonOrder,
                            "lblPersonOrderByLastname",
                            ComponentFactory._PERSONORDERCOMBO,
                            "",
                            "lblPersonOrder",
                            true,
                            true));   
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stPersonDisplayFormat,
                            "lblPersonFirstnameLastName",
                            ComponentFactory._PERSONDISPLAYFORMATCOMBO,
                            "",
                            "lblPersonDisplayFormat",
                            true,
                            true));   
        getSettings().addSetting(_FileHashing,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stHashType,
                            "md5",
                            ComponentFactory._HASHTYPECOMBO,
                            "",
                            "lblFileHashType",
                            true,
                            true));
        getSettings().addSetting(_FileHashing,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stHashMaxFileSizeKb,
                            Long.valueOf(500000000),
                            ComponentFactory._FILESIZEFIELD,
                            "",
                            "lblFileHashMaxFileSize",
                            true,
                            true));             
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stWebServerPort,
                            Long.valueOf(8080),
                            -1,
                            "",
                            "",
                            false,
                            false));  
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stImportCharacterSet,
                            "UTF-8",
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stImportSeperator,
                            ",",
                            ComponentFactory._SHORTTEXTFIELD,
                            "",
                            "lblValueSeperator",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowTipsOnStartup,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BIGINTEGER,
                            DcRepository.Settings.stXpMode,
                            -1,
                            -1,
                            "",
                            "",
                            false,
                            false));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowGroupingPanel,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));   
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowQuickFilterBar,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stHsqlCacheScale,
                            18,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stHsqlCacheSizeScale,
                            20,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stGarbageCollectionIntervalMs,
                            480000,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_SelectionColor,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stSelectionColor,
                            new Color(255, 255, 153),
                            ComponentFactory._COLORSELECTOR,
                            "",
                            "",
                            false,
                            true));
        getSettings().addSetting(_SelectionColor,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stQuickViewBackgroundColor,
                            Color.WHITE,
                            ComponentFactory._COLORSELECTOR,
                            "",
                            "",
                            false,
                            false));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stCardViewBackgroundColor,
                            Color.WHITE,
                            ComponentFactory._COLORSELECTOR,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stOddRowColor,
                            new Color(231, 243, 244),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stEvenRowColor,
                            new Color(224, 237, 238),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stTableHeaderColor,
                            new Color(231, 243, 244),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stCheckUniqueness,
                            true,
                            ComponentFactory._CHECKBOX,
                            "tpUniqueness",
                            "lblUniqueness",
                            false,
                            true));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stCheckRequiredFields,
                            true,
                            ComponentFactory._CHECKBOX,
                            "tpRequiredFields",
                            "lblCheckRequiredFields",
                            false,
                            true));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stConnectionString,
                            "dc",
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stDatabaseDriver,
                            "org.hsqldb.jdbcDriver",
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowTableTooltip,
                            true,
                            ComponentFactory._CHECKBOX,
                            "tpShowTableTooltips",
                            "lblShowTableTooltips",
                            false,
                            true));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stModule,
                            DcModules._SOFTWARE,
                            -1,
                            "",
                            "",
                            true,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stMassUpdateItemPickMode,
                            Synchronizer._ALL,
                            -1,
                            "",
                            "",
                            true,
                            false));
        getSettings().addSetting(_Font,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stFontAntiAliasing,
                            Boolean.TRUE,
                            ComponentFactory._CHECKBOX,
                            "",
                            "lblFontAntiAliasing",
                            false,
                            true));        
        getSettings().addSetting(_Font,
                new Setting(DcRepository.ValueTypes._FONT,
                            DcRepository.Settings.stSystemFontNormal,
                            new Font("Arial", Font.PLAIN, 11),
                            ComponentFactory._FONTSELECTOR,
                            "tpFont",
                            "lblFontNormal",
                            false,
                            true));
        getSettings().addSetting(_Font,
                new Setting(DcRepository.ValueTypes._FONT,
                            DcRepository.Settings.stSystemFontBold,
                            new Font("Arial", Font.PLAIN, 11),
                            ComponentFactory._FONTSELECTOR,
                            "tpFont",
                            "lblFontBold",
                            false,
                            true));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LOOKANDFEEL,
                            DcRepository.Settings.stLookAndFeel,
                            new DcLookAndFeel("JTattoo - Mint", "com.jtattoo.plaf.mint.MintLookAndFeel", null, 1),
                            ComponentFactory._LOOKANDFEELSELECTOR,
                            "",
                            "lblLookAndFeel",
                            false,
                            false));        
        getSettings().addSetting(_HTTP,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stProxyServerName,
                            "",
                            ComponentFactory._SHORTTEXTFIELD,
                            "tpProxyServerName",
                            "lblProxyServerName",
                            true,
                            true));
        getSettings().addSetting(_HTTP,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stProxyServerPort,
                            0,
                            ComponentFactory._NUMBERFIELD,
                            "tpProxyServerPort",
                            "lblProxyServerPort",
                            true,
                            true));
        getSettings().addSetting(_HTTP,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stProxyUserName,
                            "",
                            ComponentFactory._SHORTTEXTFIELD,
                            "tpProxyUserName",
                            "lblProxyUserName",
                            true,
                            true));
        getSettings().addSetting(_HTTP,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stProxyPassword,
                            "",
                            ComponentFactory._PASSWORDFIELD,
                            "tpProxyPassword",
                            "lblProxyPassword",
                            true,
                            true));
        getSettings().addSetting(_HTTP,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stBrowserPath,
                            "",
                            ComponentFactory._FILEFIELD,
                            "",
                            "lblBrowserPath",
                            true,
                            true));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stExpertFormSize,
                            new Dimension(700, 600),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stItemFormSettingsDialogSize,
                            new Dimension(700, 600),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stDriveManagerDialogSize,
                            new Dimension(573, 548),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stHelpFormSize,
                            new Dimension(700, 600),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stTabbedPaneSize,
                            new Dimension(575, 463),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stReferencesDialogSize,
                            new Dimension(300, 600),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stSelectItemDialogSize,
                            new Dimension(300, 600),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stWebServerFrameSize,
                            new Dimension(300, 300),
                            -1,
                            "",
                            "",
                            false,
                            false));    
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stItemWizardFormSize,
                            new Dimension(800, 550),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stModuleWizardFormSize,
                            new Dimension(800, 550),
                            -1,
                            "",
                            "",
                            false,
                            false));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stUpdateAllDialogSize,
                            new Dimension(600, 600),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stMainViewSize,
                            new Dimension(800, 650),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stMainViewState,
                            JFrame.NORMAL,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stTextViewerSize,
                            new Dimension(500, 700),
                            -1,
                            "",
                            "",
                            false,
                            false));

        getSettings().addSetting(_General,
                    new Setting(DcRepository.ValueTypes._DIMENSION,
                             DcRepository.Settings.stReportingDialogSize,
                             new Dimension(700, 490),
                             -1,
                             "",
                             "",
                             false,
                             false));
        getSettings().addSetting(_General,
                    new Setting(DcRepository.ValueTypes._DIMENSION,
                             DcRepository.Settings.stModuleSelectDialogSize,
                             new Dimension(300, 500),
                             -1,
                             "",
                             "",
                             false,
                             false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowModuleList,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stBroadband,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stLastDirectoryUsed,
                            "",
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowMenuBarLabels,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stUpdateAllSelectedItemsOnly,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowQuickView,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stDeleteImageFileAfterImport,
                            Boolean.FALSE,
                            ComponentFactory._CHECKBOX,
                            "tpDeleteImageFile",
                            "lblDeleteImageFile",
                            false,
                            true));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stQuickViewDividerLocation,
                            419,
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stTreeDividerLocation,
                            124,
                            -1,
                            "",
                            "",
                            false,
                            false));        
        getSettings().addSetting(_FileHandlers,
                new Setting(DcRepository.ValueTypes._DEFINITIONGROUP,
                            DcRepository.Settings.stProgramDefinitions,
                            new ProgramDefinitions(),
                            ComponentFactory._PROGRAMDEFINITIONFIELD,
                            "tpProgramDefinitions",
                            "",
                            false,
                            true));

        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stResourcesEditorViewSize,
                            new Dimension(542, 680),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stBackupDialogSize,
                            new Dimension(500, 500),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stFileRenamerDialogSize,
                            new Dimension(300, 300),
                            -1,
                            "",
                            "",
                            false,
                            false));

        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stFileRenamerPreviewDialogSize,
                            new Dimension(400, 600),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stSortDialogSize,
                            new Dimension(300, 200),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stGroupByDialogSize,
                            new Dimension(300, 200),
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stReportFile,
                            "",
                            -1,
                            "",
                            "",
                            false,
                            false));
        getSettings().addSetting(_Regional,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stDecimalGroupingSymbol,
                            ".",
                            ComponentFactory._CHARACTERFIELD,
                            "lblDecimalGroupingSymbol",
                            "lblDecimalGroupingSymbol",
                            true,
                            true));
        getSettings().addSetting(_Regional,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stDecimalSeparatorSymbol,
                            ",",
                            ComponentFactory._CHARACTERFIELD,
                            "lblDecimalSeperatorSymbol",
                            "lblDecimalSeperatorSymbol",
                            true,
                            true));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stRestoreDatabase,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stRestoreModules,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stRestoreReports,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false));          
        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRINGARRAY,
                            DcRepository.Settings.stDriveManagerDrives,
                            null,
                            -1,
                            "",
                            "",
                            false,
                            false));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRINGARRAY,
                            DcRepository.Settings.stDriveManagerExcludedDirs,
                            null,
                            -1,
                            "",
                            "",
                            false,
                            false));          
    }
}
