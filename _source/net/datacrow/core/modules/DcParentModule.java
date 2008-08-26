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

import net.datacrow.core.modules.xml.XmlModule;

public class DcParentModule extends DcModule  {

    private static final long serialVersionUID = 4714961191122806192L;

    public DcParentModule(XmlModule xmlModule) {
        super(xmlModule);
    }
    
    public DcParentModule(int index, boolean topModule, String name, String description, String objectName, String objectNamePlural, String tableName, String tableShortName, String tableJoin) {
        super(index, topModule, name, description, objectName, objectNamePlural,
              tableName, tableShortName, tableJoin);
    }
    
    @Override
    public boolean isParentModule() {
        return true;
    }      
    
    @Override
    public boolean isTopModule() {
        return true;
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof DcParentModule ? ((DcParentModule) o).getIndex() == getIndex() : false);
    }     
}