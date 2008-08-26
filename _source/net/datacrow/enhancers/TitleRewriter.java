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

package net.datacrow.enhancers;

import java.util.StringTokenizer;

import net.datacrow.core.modules.DcMediaModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcAssociate;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcMediaObject;

public class TitleRewriter implements IValueEnhancer {

    private boolean enabled = false;
    private String wordList;
    
    public TitleRewriter() {
    }    
    
    public TitleRewriter(boolean enabled, String list) {
        this.enabled = enabled;
        this.wordList = list;
    }
    
    public int getField() {
        if (DcModules.getCurrent() instanceof DcMediaModule)
            return DcMediaObject._A_TITLE;
        else if (DcModules.getCurrent().getDcObject() instanceof DcAssociate)
            return DcAssociate._A_NAME;
        else
            return DcModules.getCurrent().getDcObject().getDisplayFieldIdx();
    }    

    public String getWordList() {
        return wordList;
    } 
    
    public String toSaveString() {
        return enabled + "/&/" + wordList;
    }

    public int getIndex() {
        return ValueEnhancers._TITLEREWRITERS;
    }
    
    public void parse(String s) {
        enabled = Boolean.valueOf(s.substring(0, s.indexOf("/&/"))).booleanValue();
        wordList = s.substring(s.indexOf("/&/") + 3, s.length());
    }

    public Object apply(DcField field, Object value) {
        Object result = value;

        if (value != null && value instanceof String) {
            String s = (String) result;
            StringTokenizer st = new StringTokenizer(wordList, ",");
            while (st.hasMoreElements()) {
                String keyword = ((String) st.nextElement()) + " ";
                
                if (s.toLowerCase().startsWith(keyword.toLowerCase())) {
                    int start = keyword.length();
                    int end = s.length();
                    
                    if (start < end) 
                        result = s.substring(start, end) + ", " + keyword.trim();
                }
            }
        }
        return result;
    }
    
    public boolean isRunOnUpdating() {
        return true;
    }
    
    public boolean isRunOnInsert() {
        return true;
    }
    

    public boolean isEnabled() {
        return enabled;
    }
}
