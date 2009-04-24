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

package net.datacrow.core.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.datacrow.core.DataCrow;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.modules.xml.XmlField;
import net.datacrow.core.objects.DcField;
import net.datacrow.util.Directory;
import net.datacrow.util.Utilities;

import org.apache.log4j.Logger;

/**
 * Manages table conversions when
 * @author Robert Jan van der Waals
 */
public class Conversions {

    private static Logger logger = Logger.getLogger(Conversions.class.getName());

    private Collection<Conversion> conversions = new ArrayList<Conversion>();
    private String filename = DataCrow.installationDir + "upgrade" + File.separator + "conversions.properties";
    
    public Conversions() {}
    
    public void calculate() {
        for (DcModule module : DcModules.getAllModules()) {
            
            if (module.getXmlModule() == null)
                continue;
            
            for (XmlField xmlField : module.getXmlModule().getFields()) {
                DcField field = module.getField(xmlField.getIndex());
                
                if (field.getFieldType() != xmlField.getFieldType()) {
                    Conversion conversion = new Conversion(module.getIndex());
                    conversion.setColumnName(field.getDatabaseFieldName());
                    conversion.setOldFieldType(field.getFieldType());
                    conversion.setNewFieldType(xmlField.getFieldType());
                    conversion.setReferencingModuleIdx(xmlField.getModuleReference());
                    conversions.add(conversion);
                }
            }
        }
    }

    public void load() {
        List<String> filenames = Directory.read(DataCrow.installationDir + "upgrade", false, false, new String[] {"properties"});
        
        // sort them in their natural order
        Collections.sort(filenames);

        for (String filename : filenames) {
            
            if (!filename.endsWith("conversions.properties"))
                continue;
            
            FileInputStream fos = null;
            try {
                fos = new FileInputStream(filename);
                Properties properties = new Properties();
                properties.load(fos);
                for (Object value : properties.values())
                    conversions.add(new Conversion((String) value));
                
            } catch (IOException e) {
                logger.error("Failed to load database column conversion scripts", e);
            } finally {
                try {
                    if (fos != null) fos.close();
                } catch (IOException e) {
                    logger.error("Could not release conversion file " + filename, e);
                }
            }
        }
    }
    
    public void execute() {
        boolean converted = false;
        for (Conversion conversion : conversions) {
            if (conversion.isNeeded()) 
                converted |= conversion.execute();
        }
        
        // rename the old file
        File file = new File(filename);
        if (converted && file.exists()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
            String prefix = sdf.format(new Date());
            File newFile = new File(file.getParent(), prefix + file.getName());
            
            try {
                Utilities.rename(new File(filename), newFile);
            } catch (IOException e) {
                logger.error("Could not rename the conversion file from " + file + " to " + newFile, e);
            }
        }
        
//        if (converted) {
//            DataCrow.showSplashScreen(false);
//            new MessageBox("Conversions have been applied. Data Crow needs to be restarted.", MessageBox._INFORMATION);
//            DatabaseManager.closeDatabases(false);
//            System.exit(0);
//        }
    }
    
    public void save() {
        Properties properties = new Properties();
        int count = 0;
        for (Conversion conversion : conversions) {
            properties.put(String.valueOf(count), conversion.toString());
        }
        
        try {
            properties.store(new FileOutputStream(new File(filename)), "");
        } catch (IOException e) {
            logger.error("Failed to persist database column conversion scripts", e);
        }            
    }
}