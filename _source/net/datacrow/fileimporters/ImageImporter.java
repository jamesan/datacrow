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

package net.datacrow.fileimporters;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;
import net.datacrow.core.objects.helpers.Image;
import net.datacrow.core.resources.DcResources;
import net.datacrow.util.DcImageIcon;
import net.datacrow.util.Utilities;
import net.datacrow.util.svg.SVGtoBufferedImageConverter;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.iptc.IptcDirectory;

/**
 * Imports image files.
 * 
 * @author Robert Jan van der Waals
 */
public class ImageImporter extends FileImporter {

    private static final Calendar cal = Calendar.getInstance();
    
    /**
     * Creates a new instance.
     */
    public ImageImporter() {
        super(DcModules._IMAGE);
    }
    
    /**
     * The default supported file types.
     */
    @Override
    public String[] getDefaultSupportedFileTypes() {
        return new String[] {"jpg", "gif", "jpeg", "png", "svg"};
    }
    
    @Override
    public boolean allowReparsing() {
        return true;
    }    
    
    @Override
    public boolean canImportArt() {
        return false;
    }    
    
    @Override
    public DcObject parse(String filename, int directoryUsage) {
        DcObject image = DcModules.get(DcModules._IMAGE).getItem();
        
        try {
            image.setIDs();
            image.setValue(Image._A_TITLE, getName(filename, directoryUsage));
            image.setValue(Image._SYS_FILENAME, filename);
            
            Picture pic = (Picture) DcModules.get(DcModules._PICTURE).getItem();
            DcImageIcon icon;
            if (filename.toLowerCase().endsWith(".svg")) {
                SVGtoBufferedImageConverter converter = new SVGtoBufferedImageConverter();
                BufferedImage bi = converter.renderSVG(filename);
                icon = new DcImageIcon(Utilities.getScaledImage(new DcImageIcon(bi), 400, 400));
                filename = File.createTempFile(String.valueOf(Utilities.getUniqueID()), ".png").toString();
                icon.setFilename(filename);
                icon.save();
                bi.flush();
            } else {
                icon = new DcImageIcon(Utilities.getScaledImage(new DcImageIcon(filename), 400, 400));
                pic.setValue(Picture._G_EXTERNAL_FILENAME, filename);
            }
            
            int width = icon.getIconWidth();
            int height = icon.getIconHeight();
            
            image.setValue(Image._F_WIDTH, width != -1 ? Long.valueOf(width) : null);
            image.setValue(Image._G_HEIGHT, height != -1 ? Long.valueOf(height) : null);
            
            icon.getImage().flush();
            
            pic.setValue(Picture._A_OBJECTID, image.getID());
            pic.setValue(Picture._B_FIELD, image.getField(Image._I_IMAGE).getDatabaseFieldName());
            pic.setValue(Picture._D_IMAGE, icon);
            pic.isEdited(true);
            
            image.setValue(Image._I_IMAGE, pic);
            
            File jpegFile = new File(filename); 
            
            try {
                Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
                if (metadata.containsDirectory(ExifDirectory.class)) {
                    Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);
                    
                    try {
                        String camera = exifDirectory.getString(ExifDirectory.TAG_MODEL);
                        image.setValue(Image._Q_CAMERA, camera);
                    } catch (Exception me) {}

                    try {
                        int compression = exifDirectory.getInt(ExifDirectory.TAG_COMPRESSION);
                        image.setValue(Image._O_COMPRESSION, Long.valueOf(compression));
                    } catch (Exception me) {}


                    try {
                        String description = exifDirectory.getString(ExifDirectory.TAG_IMAGE_DESCRIPTION);
                        image.setValue(Image._B_DESCRIPTION, description);
                    } catch (Exception me) {}

                    try {
                        Date date = exifDirectory.getDate(ExifDirectory.TAG_DATETIME);
                        if (date != null) {
                            cal.setTime(date);
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            image.setValue(Image._N_DATE, cal.getTime());
                        }
                    } catch (Exception me) {}
                }
                
                if (metadata.containsDirectory(IptcDirectory.class)) {
                    try {
                        Directory iptcDirectory = metadata.getDirectory(IptcDirectory.class);
                        String city = iptcDirectory.getString(IptcDirectory.TAG_CITY);
                        String country = iptcDirectory.getString(IptcDirectory.TAG_COUNTRY_OR_PRIMARY_LOCATION);
                        String state = iptcDirectory.getString(IptcDirectory.TAG_PROVINCE_OR_STATE);
                        
                        String location = "";
                        location += country != null ? country + ", " : "";
                        location += state != null ? state  + ", " : "";
                        location += city != null ? city : "";
                        
                        image.setValue(Image._P_PLACE, location);
                    } catch (Exception me) {}
                }
                
            } catch (JpegProcessingException jpe) {}
            
        } catch (Exception exp) {
            getClient().addMessage(DcResources.getText("msgCouldNotReadInfoFrom", filename));
        }
        
        return image;
    }
}
