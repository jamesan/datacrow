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

package net.datacrow.core.db.upgrade;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.datacrow.core.DataCrow;
import net.datacrow.core.DcRepository;
import net.datacrow.core.Version;
import net.datacrow.core.db.DatabaseManager;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcMapping;
import net.datacrow.core.objects.Picture;
import net.datacrow.settings.DcSettings;
import net.datacrow.util.DcSwingUtilities;
import net.datacrow.util.Utilities;

import org.apache.log4j.Logger;

/**
 * If possible, perform upgrades / changes in the DataManagerConversion class!
 * 
 * Converts the current database before the actual module tables are created / updated.
 * This means that the code here defies workflow logic and is strictly to be used for
 * table conversions and migration out of the scope of the normal module upgrade code.
 * 
 * The automatic database correction script runs after this manual upgrade.
 * 
 * @author Robert Jan van der Waals
 */
public class DatabaseUpgradeBeforeInitialization {
    
private static Logger logger = Logger.getLogger(DatabaseUpgradeBeforeInitialization.class.getName());
    
    public void start() {
        try {
            boolean upgraded = false;
            Version v = DatabaseManager.getVersion();
            if (v.isOlder(new Version(3, 9, 0, 0))) {
                DataCrow.showSplashScreen(false);
            	cleanupReferences();
            	DcSettings.set(DcRepository.Settings.stGarbageCollectionIntervalMs, Long.valueOf(0));
            	upgraded |= createIndexes();
            }
            
            if (v.isOlder(new Version(3, 9, 11, 0))) {
                DataCrow.showSplashScreen(false);
                removeCompanyFields();
            }
            
            if (upgraded) {
                DataCrow.showSplashScreen(false);
                DcSwingUtilities.displayMessage("The upgrade was successful. Data Crow will now continue.");
            }
            
            DataCrow.showSplashScreen(true);
            
        } catch (Exception e) {
            String msg = e.toString() + ". Data conversion failed. " +
                "Please restore your latest Backup and retry. Contact the developer " +
                "if the error persists";
            
            DcSwingUtilities.displayErrorMessage(msg);
            logger.error(msg, e);
        }            
    }

    private void removeCompanyFields() {
        String sql;
        for (DcModule module : DcModules.getAllModules()) {
            
            if (module.getType() == DcModule._TYPE_ASSOCIATE_MODULE) {
                try {
                   sql = "ALTER TABLE " + module.getTableName() + " DROP COLUMN COMPANY";
                   DatabaseManager.execute(sql);
                } catch (SQLException e) {
                    logger.debug(e, e);
                }
            }
        }
    }

    
    
    private boolean createIndexes() throws Exception {
        Connection conn = DatabaseManager.getConnection();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
        } catch (SQLException se) {
            logger.error(se, se);
        }

        for (DcModule module : DcModules.getAllModules()) {
            if (module.getIndex() == DcModules._PICTURE) {
                try { 
                	String sql = "select distinct filename from picture where filename in (select filename from picture group by filename having count(ObjectID) > 1)";
                	ResultSet rs = stmt.executeQuery(sql);
                	ResultSet rs2;
                	
                	String filename;
                	String objectID;
                	Long width;
                	Long height;
                	String field;
                	String externalFilename;
                	while (rs.next()) {
                	    
                	    sql = "select filename, objectid, width, field, height, external_filename from Picture";
                	    rs2 = stmt.executeQuery(sql);
                	    while (rs2.next()) {
                	        
                	        sql = "delete from Picture where filename = '" + rs2.getString("filename") + "'";
                	        
                	        filename = rs2.getString("filename");
                            objectID = rs2.getString("objectid");
                            width = rs2.getLong("width");
                            height = rs2.getLong("height");
                            field = rs2.getString("field");
                            externalFilename = rs2.getString("external_filename");
                            externalFilename = Utilities.isEmpty(externalFilename) ? "null" : "'" + externalFilename + "'";
                            
                            logger.info("found duplicate records in the picture table - removing duplicate: " + filename);
                            
                            stmt.execute(sql);
                            sql = "insert into Picture (filename, objectid, width, field, height, external_filename) " +
                                    "values ('" + filename + "','" + objectID + "'," + width + ",'," + field + "'," + height + "," + externalFilename + ")";
                            
                            stmt.execute(sql);
                	        
                            break;
                	    }
                	    rs2.close();
                	}
                	
                	rs.close();
                	
                	logger.info("Creating UNIQUE CONSTRAINT on " + module.getTableName());
                	
                    stmt.execute("CREATE  UNIQUE CONSTRAINT " + module.getTableName() + "_IDX ON " + module.getTableName() + " (" +
                            module.getField(Picture._A_OBJECTID).getDatabaseFieldName() + ", " +
                            module.getField(Picture._B_FIELD).getDatabaseFieldName() + ")");
                
                } catch (SQLException se) {
                    // throw new Exception("Unable to create  UNIQUE CONSTRAINT on " + module.getTableName(), se);
                    // there is no solution for this... yet.
                    logger.debug(se, se);
                }
            } else if (module.getType() == DcModule._TYPE_MAPPING_MODULE) {
                try { 
                	String sql = "select distinct objectid from " + module.getTableName() + " where objectid in " +
                			     "(select objectid from " + module.getTableName() + " group by objectid having count(referencedid) > 1)";
                	ResultSet rs = stmt.executeQuery(sql);
                	
                	ResultSet rs2;
                    String objectID;
                    String referencedID;
                	while (rs.next()) {
                	    
                        sql = "select objectID, referencedID from " + module.getTableName() + " where objectID = '" + rs.getString("objectID") + "'";
                        rs2 = stmt.executeQuery(sql);

                        while (rs2.next()) {
                            
                            referencedID = rs2.getString("referencedid");
                            objectID = rs2.getString("objectid");
                            
                            logger.info("Cleaning duplicates for " + module.getTableName() + ": " + objectID);
                            
                            sql = "delete from " + module.getTableName() + " where ObjectID = '" + objectID + "' AND referencedID = '" + referencedID + "'";
                            
                            stmt.execute(sql);
                            sql = "insert into " + module.getTableName() + " (objectid, referencedid) values ('" + objectID + "','" + referencedID + "')";
                            
                            stmt.execute(sql);
                            
                            break;
                        }

                        rs2.close();
                	    
                	    logger.info("Duplicates were successfully removed from " + module.getTableName() + ".");
                	    
                	    break;
                	}
                	
                	rs.close();
                	
                	logger.info("Creating  UNIQUE CONSTRAINT on " + module.getTableName());
                	stmt.execute("CREATE  UNIQUE CONSTRAINT " + module.getTableName() + "_IDX ON " + module.getTableName() + " (" +
                            module.getField(DcMapping._A_PARENT_ID).getDatabaseFieldName() + ", " +
                            module.getField(DcMapping._B_REFERENCED_ID).getDatabaseFieldName() + ")");
                } catch (SQLException se) {
                    throw new Exception("Unable to create UNIQUE CONSTRAINT on " + module.getTableName(), se);
                }
            } else if (module.getType() == DcModule._TYPE_EXTERNALREFERENCE_MODULE) {
                try { 
                    logger.info("Creating UNIQUE CONSTRAINT on " + module.getTableName());
                    stmt.execute("CREATE UNIQUE INDEX " + module.getTableName() + "_IDX ON " + module.getTableName() + " (" +
                            module.getField(DcMapping._A_PARENT_ID).getDatabaseFieldName() + ", " +
                            module.getField(DcMapping._B_REFERENCED_ID).getDatabaseFieldName() + ")");
                } catch (SQLException se) {
                    throw new Exception("Unable to create UNIQUE CONSTRAINT on " + module.getTableName(), se);
                }
            }
        }
        
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException ignore) {}
        
        return true;
    }
    
    private void cleanupReferences() {

        Connection conn = DatabaseManager.getConnection();
        Statement stmt = null;
        String sql;
        for (DcModule module : DcModules.getAllModules()) {
            
            if (module.getType() == DcModule._TYPE_MAPPING_MODULE) {
                
                try {
                	stmt = conn.createStatement();
                	sql = "DELETE FROM " + module.getTableName() + 
                            " WHERE " + module.getField(DcMapping._A_PARENT_ID).getDatabaseFieldName() +
                            " NOT IN (SELECT ID FROM " + 
                            DcModules.get(module.getField(DcMapping._A_PARENT_ID).getSourceModuleIdx()).getTableName() + ") " +
                            " OR " + module.getField(DcMapping._B_REFERENCED_ID).getDatabaseFieldName() + 
                            " NOT IN (SELECT ID FROM " + 
                            DcModules.get(module.getField(DcMapping._B_REFERENCED_ID).getSourceModuleIdx()).getTableName() + ")";
                    stmt.execute(sql); 
                
                } catch (SQLException se) {
                    logger.error("Could not remove references", se);
                }
            }
        }
        
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException ignore) {}
   }
}
