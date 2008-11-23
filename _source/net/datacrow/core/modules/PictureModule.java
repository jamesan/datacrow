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

package net.datacrow.core.modules;

import net.datacrow.console.ComponentFactory;
import net.datacrow.core.DcRepository;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.Picture;

public class PictureModule extends DcModule {
    
    private static final long serialVersionUID = 4278200439507269874L;

    public PictureModule() {
        super(DcModules._PICTURE,
              false,
              "",
              "",
              "",
              "",
              "picture",
              "pic");
    }
    
    @Override
    public DcObject getDcObject() {
        return new Picture();
    }
    
    @Override
    protected void initializeFields() {
        addField(new DcField(Picture._A_OBJECTID, getIndex(), "ObjectID", 
                             false, true, false, false, false,
                             50, ComponentFactory._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._BIGINTEGER, 
                             "ObjectID"));
        addField(new DcField(Picture._B_FIELD, getIndex(), "Field", 
                             false, true, false, false, false,
                             100, ComponentFactory._SHORTTEXTFIELD, getIndex(), DcRepository.ValueTypes._STRING, 
                             "Field"));
        addField(new DcField(Picture._C_FILENAME, getIndex(), "Filename", 
                             false, true, true, false, false, 
                             500, ComponentFactory._FILELAUNCHFIELD, getIndex(), DcRepository.ValueTypes._STRING, 
                             "Filename"));
        addField(new DcField(Picture._D_IMAGE, getIndex(), "Image", 
                             true, true, true, false, false, 
                             0, ComponentFactory._PICTUREFIELD, getIndex(), DcRepository.ValueTypes._IMAGEICON, 
                             "Image"));        
        addField(new DcField(Picture._E_HEIGHT, getIndex(), "Height", 
                             false, true, true, false, false, 
                             0, ComponentFactory._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG, 
                             "Height"));        
        addField(new DcField(Picture._F_WIDTH, getIndex(), "Width", 
                             false, true, true, false, false, 
                             0, ComponentFactory._NUMBERFIELD, getIndex(), DcRepository.ValueTypes._LONG, 
                             "Width"));        
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof PictureModule ? ((PictureModule) o).getIndex() == getIndex() : false);
    }   
}
