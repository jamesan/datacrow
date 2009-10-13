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

package net.datacrow.core.objects.helpers;

import java.util.ArrayList;
import java.util.Collection;

import net.datacrow.core.data.DataFilter;
import net.datacrow.core.data.DataFilterEntry;
import net.datacrow.core.data.DataManager;
import net.datacrow.core.data.Operator;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;

public class Container extends DcObject {

    private static final long serialVersionUID = -3032927100353360734L;

    public static final int _A_NAME = 1;
    public static final int _B_TYPE = 2;
    public static final int _C_PICTUREFRONT = 3;
    public static final int _D_DESCRIPTION = 4;
    public static final int _E_ICON = 5;
    public static final int _F_PARENT = 6;
    
    private boolean isLoading = false;

    public Container() {
        super(DcModules._CONTAINER);
    }
    
    public boolean isTop() {
        return isFilled(_F_PARENT);
    }
    
    public Container getParentContainer() {
        String id = (String) getValue(_F_PARENT);
        return id != null ? (Container) DataManager.getObject(DcModules._CONTAINER, id) : null;
    }
    
    public Collection<Container> getChildContainers() {
        Collection<Container> children = new ArrayList<Container>();
        DataFilter df = new DataFilter(DcModules._CONTAINER);
        df.addEntry(new DataFilterEntry(DataFilterEntry._AND, DcModules._CONTAINER, Container._F_PARENT, Operator.EQUAL_TO, getID()));
        
        for (DcObject dco : DataManager.get(DcModules._CONTAINER, df)) {
            children.add((Container) dco);
        }
        
        return children;
    }
    
    @Override
    public Object getValue(int index) {
        if (index == _F_PARENT) {
            Object o = super.getValue(_F_PARENT);
            return o instanceof String ? DataManager.getObject(DcModules._CONTAINER, (String) o) : o;
        } else {
            return super.getValue(index);
        }
    }

    @Override
    public void loadChildren() {
        
        children.clear();
        
        if (    (getID() != null && getID().length() > 0) &&
               !isLoading &&
                DataManager.isInitialized() &&
                getModule().getChild() != null) {
            
            isLoading = true;

            try {
                DataFilter df = new DataFilter(DcModules._ITEM);
                df.addEntry(new DataFilterEntry(DataFilterEntry._AND, DcModules._ITEM, DcObject._SYS_CONTAINER, Operator.EQUAL_TO, this));
                
                DcObject[] c = DataManager.get(DcModules._ITEM, df);
                for (DcObject child : c) 
                    children.add(child);
            } finally {
                isLoading = false;
            }
        }
    } 
}
