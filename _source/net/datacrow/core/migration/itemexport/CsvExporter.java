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

package net.datacrow.core.migration.itemexport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import net.datacrow.console.ComponentFactory;
import net.datacrow.core.DataCrow;
import net.datacrow.core.DcThread;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.resources.DcResources;

public class CsvExporter extends ItemExporter {
    
    private static Logger logger = Logger.getLogger(CsvExporter.class.getName());
    
    public CsvExporter(int moduleIdx, int mode) throws Exception {
        super(moduleIdx, "CSV", mode);
    }

    private void writeBytes(String value, boolean addTab) throws IOException {
        String s = value;
        s = s.replaceAll("\t", "");
        if (!s.equals("\r\n")) {
            s = s.replaceAll("\r", " ");
            s = s.replaceAll("\n", " ");
        }
        
        if (addTab)
            s = "\t" + s;
        
        bos.write(s.getBytes("UTF8"));
    }

    @Override
    public String getName() {
        return "TXT Exporter";
    }

    @Override
    public String getFileType() {
        return "txt";
    }
    
    @Override
    public DcThread getTask() {
        return new Task(items);
    }

    private class Task extends DcThread {
        
        private List<DcObject> items;
        
        public Task(Collection<DcObject> items) {
            super(null, "CSV export to " + file);
            
            this.items = new ArrayList<DcObject>();
            this.items.addAll(items);
        }

        @Override
        public void run() {
            try {
                create();
            } catch (Exception exp) {
                success = false;
                logger.error(DcResources.getText("msgErrorWhileCreatingReport", exp.toString()), exp);
                client.notifyMessage(DcResources.getText("msgErrorWhileCreatingReport", exp.toString()));
            } finally {
                client.notifyStopped();
            }
            
            items.clear();
            items = null;
        }
        
       public void create() throws Exception {
            
            if (items == null || items.size() == 0) return;
            
            // create the table and the header
            DcObject dco = items.get(0);
            int counter = 0;
            
            for (DcField field : dco.getFields()) {
                if (isCanceled()) break;
            
                if (field.isEnabled()) {
                    writeBytes(field.getSystemName(), counter != 0);
                    counter++;
                }                
            }        
            
            writeBytes("\r\n", false);
            
            counter = 0;
            
            for (DcObject o : items) {
                
                if (isCanceled()) break;
                
                client.notifyMessage(DcResources.getText("msgAddingToReport", o.toString()));
                int fieldCounter = 0;
                
                for (DcField field : dco.getFields()) {
                    if (isCanceled()) break;

                    if (field.isEnabled()) { 
                        String s = "";
                        if (field.getFieldType() == ComponentFactory._PICTUREFIELD) {
                            if (o.getValue(field.getIndex()) == null || o.getValue(field.getIndex()).toString().length() < 10)
                                s = "";
                            else 
                                s = DataCrow.imageDir + ((Picture) o.getValue(field.getIndex())).getFilename();
                        } else {
                            s = o.getDisplayString(field.getIndex());
                        }
                        
                        writeBytes(s, fieldCounter != 0);
                        fieldCounter++;
                    }
                }
                
                writeBytes("\r\n", false);
                counter++;
                client.notifyProcessed();
                bos.flush();
            }
            
            bos.close();
            client.notifyMessage(DcResources.getText("lblExportHasFinished"));
        }        
    }       
}
