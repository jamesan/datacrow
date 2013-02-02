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

package net.datacrow.core.backup;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import net.datacrow.core.DataCrow;
import net.datacrow.core.DcRepository;
import net.datacrow.core.db.DatabaseManager;
import net.datacrow.core.resources.DcResources;
import net.datacrow.settings.DcSettings;
import net.datacrow.util.Directory;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileWriter;
import de.schlichtherle.truezip.file.TVFS;

/**
 * Performs a backup of the Data Crow data, settings, modules and reports.
 * 
 * @author Robert Jan van der Waals
 */
public class Backup extends Thread {

    private File directory;
    private IBackupRestoreListener listener;
    private String comment;
 
    /**
     * Creates a new instance.
     * @param listener The listener which will be informed of events and errors.
     * @param directory The directory where the backup will be created.
     */
    public Backup(IBackupRestoreListener listener, File directory, String comment) {
        this.directory = directory;
        this.comment = comment;
        this.listener = listener;
    }
    
    /**
     * Retrieves all the files to be backed up.
     * @return A collection of fully classified filenames.
     */
    private Collection<String> getFiles() {
        Directory dir = new Directory(DataCrow.userDir, true, null);
        return dir.read();
    }

    private String getZipFile(String target) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmm");
        String date = format.format(cal.getTime());

        String filename = "datacrow_backup_" + date + ".zip";
        String zipFile = target.endsWith(File.separator) ? target + filename :
                                                           target + File.separator + filename;
        return zipFile;
    }    
    
    private void addEntry(String zipName, String source) {
        try {
            TFile src = new TFile(source);
            TFile dst = new TFile(zipName);
            src.cp_rp(dst);
        } catch (IOException ex) {
            listener.sendError(ex);
        }
    }
    
    /**
     * Performs the actual back up and informs the listener on the progress.
     */
    @Override
    public void run() {
        listener.notifyStarted();
        
        if (!directory.exists())
            directory.mkdirs();
        
        listener.sendMessage(DcResources.getText("msgStartBackup"));
        DatabaseManager.closeDatabases(true);
        listener.sendMessage(DcResources.getText("msgClosingDb"));

        try {
            Collection<String> files = getFiles();
            listener.notifyProcessingCount(files.size());

            String zipFileName = getZipFile(directory.toString());
            
            File entry = new TFile(zipFileName + File.separator +  "version.txt");
            Writer writer = new TFileWriter(entry);
            try {
                writer.write(DataCrow.getVersion().toString());
                if (comment.length() > 0)
                    writer.write("\n" + comment);
            } catch (IOException e) {
                listener.sendError(e);
            } finally {
                writer.close();
            }
            
            for (String file : files) {
                
                listener.notifyProcessed();

                if (file.contains("wwwroot") && !file.contains("mediaimages")) 
                    continue;
                if (file.endsWith(".log") || file.indexOf(".log.") > -1) 
                    continue;

                addEntry(zipFileName + File.separator + file.substring(DataCrow.userDir.length()), file);
                
                listener.sendMessage(DcResources.getText("msgCreatingBackupOfFile", file));
                
                try {
                    sleep(10);
                } catch (Exception e) {
                    listener.sendError(e);
                }
            }
            
            listener.sendMessage(DcResources.getText("msgWritingBackupFile"));
            
            TVFS.umount();
            
        } catch (Exception e) {
            listener.sendMessage(DcResources.getText("msgBackupError", e.getMessage()));
            listener.sendError(e);
        }
        
        DcSettings.set(DcRepository.Settings.stBackupLocation, directory.toString());
        
        listener.sendMessage(DcResources.getText("msgBackupFinished"));
        listener.sendMessage(DcResources.getText("msgRestartingDb"));
        DatabaseManager.initialize();
        
        listener.notifyStopped();
        
        listener = null;
        directory = null;
    }
}
