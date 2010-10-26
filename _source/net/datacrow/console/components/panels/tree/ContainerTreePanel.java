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

package net.datacrow.console.components.panels.tree;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JMenuBar;
import javax.swing.tree.DefaultTreeModel;

import net.datacrow.console.menu.ContainerTreePanelMenuBar;
import net.datacrow.core.DcRepository;
import net.datacrow.core.db.DatabaseManager;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.helpers.Container;
import net.datacrow.settings.Settings;
import net.datacrow.util.DcImageIcon;
import net.datacrow.util.Utilities;

import org.apache.log4j.Logger;

/**
 * Hierarchical container view.
 * 
 * @author Robert Jan van der Waals
 */
public class ContainerTreePanel extends TreePanel {
	
    private final static Logger logger = Logger.getLogger(ContainerTreePanel.class.getName());
	
    private TreeHugger treeHugger;
    
    public ContainerTreePanel(GroupingPane gp) {
        super(gp);
    }
    
    @Override
    protected JMenuBar getMenu() {
        return new ContainerTreePanelMenuBar(getModule(), this);
    }
    
    @Override
    protected void createTopNode() {
        NodeElement ne = new NodeElement(getModule(), DcModules.get(DcModules._CONTAINER).getLabel(), null);
        top = new DcDefaultMutableTreeNode(ne);
    }

    @Override
    public void groupBy() {}
    
    @Override
	public void sort() {}
    
    @Override
    public DcDefaultMutableTreeNode getFullPath(DcObject dco) {
    	DcDefaultMutableTreeNode node = new DcDefaultMutableTreeNode(DcModules.get(DcModules._CONTAINER).getLabel());
    	node.add(new DcDefaultMutableTreeNode(new ContainerNodeElement(dco.getID(), dco.toString(), dco.getIcon())));
        return node;
    }  

	@Override
    public String getName() {
        return DcModules.get(DcModules._CONTAINER).getObjectNamePlural();
    }    
    
    @Override
    protected void createTree() {
    	
        build();
    	
        if (treeHugger != null) {
            treeHugger.cancel();
            while (treeHugger.isAlive()) {} // allow it to hang until the thread ends..
        }
        
        activated = true;
        
        treeHugger = new TreeHugger();
        treeHugger.start();
    }
    
    @Override
	public boolean isChanged(DcObject dco) {
    	return dco.isChanged(Container._A_NAME) || dco.isChanged(Container._F_PARENT);
	}
    
    private class TreeHugger extends Thread {
        
        private boolean stop = false;
        
        @Override
        public void run() {
            createTree();
        }
        
        public void cancel() {
            stop = true;
        }
        
        protected void createTree() {
            build();
        }
    
	    private void build() {
	    	Connection conn = null;
	    	Statement stmt = null;
	    	ResultSet rs = null;   	
	    	String sql = null;
	    	
	    	try {
	    	
		    	DcModule module = DcModules.get(DcModules._CONTAINER);
		    	
		    	StringBuffer sb = new StringBuffer();
		    	sb.append("SELECT ID, ");
		    	sb.append(module.getField(Container._A_NAME).getDatabaseFieldName());
		    	sb.append(", ");
		    	sb.append(module.getField(Container._F_PARENT).getDatabaseFieldName());
		    	sb.append(", ");
		    	sb.append(module.getField(Container._E_ICON).getDatabaseFieldName());
		    	sb.append(" FROM ");
		    	sb.append(module.getTableName());
		    	sb.append(" ORDER BY ");
		    	sb.append(module.getField(Container._A_NAME).getDatabaseFieldName());
		    	
		    	conn = DatabaseManager.getConnection();
		    	stmt = conn.createStatement();
		    	
		    	sql = sb.toString();
		    	logger.debug(sql);
		    	rs = stmt.executeQuery(sql);
		    	
		    	String name;
		    	String id;
		    	String parentId;
		    	String icon;
		    	
		    	Map<String, DcImageIcon> icons = new HashMap<String, DcImageIcon>();
		    	Map<String, String> parents = new LinkedHashMap<String, String>();
		    	Map<String, String> all = new LinkedHashMap<String, String>();
		    	Map<String, Collection<String>> relations = new HashMap<String, Collection<String>>();
		    	
		    	
		    	Settings settings = DcModules.get(DcModules._CONTAINER).getSettings();
		    	boolean flatView = settings.getBoolean(DcRepository.ModuleSettings.stContainerTreePanelFlat);
		    	
		    	while (rs.next()) {
		    		id = rs.getString(1); 
		    		name = rs.getString(2);
		    		parentId = rs.getString(3);
		    		icon = rs.getString(4);
		    		
		    		if (parentId != null && !flatView) {
		    			Collection<String> children = relations.get(parentId);
		    			children = children == null ? new ArrayList<String>() : children;
		    			children.add(id);
		    			relations.put(parentId, children);
		    		} else {
		    			parents.put(id, name);	
		    		}
		    		
		    		if (icon != null) icons.put(id, Utilities.base64ToImage(icon));
		    		all.put(id, name);
		    	}
		    	
		    	DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		    	int counter = 0;
		    	for (String parentKey : parents.keySet()) {
		    		
		    		if (stop) break;
		    		
		    		ContainerNodeElement ne = new ContainerNodeElement(parentKey, (String) parents.get(parentKey), icons.get(parentKey));
		    		DcDefaultMutableTreeNode current = new DcDefaultMutableTreeNode(ne);
		    		model.insertNodeInto(current, top, counter++);
		    		top.addItem(parentKey, Integer.valueOf(module.getIndex()));
		    		createChildren(model, parentKey, current, relations, all, icons);
		    	}
		    	
	    	} catch (Exception e) {
	    		logger.error("Error while building the container tree", e);
	    	}
	    	
	    	try {
	    		if (rs != null) rs.close();
	    		if (stmt != null) stmt.close();
	    		if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Error while closing connection (statement, resultset and/or connection)", e);
			}
	
	    }

	    private void createChildren(DefaultTreeModel model,
	    							String parentKey, 
	    							DcDefaultMutableTreeNode parentNode, 
	    							Map<String, Collection<String>> relations,
	    							Map<String, String> all,
	    							Map<String, DcImageIcon> icons) {
	    	
	    	if (relations.containsKey(parentKey)) {
	    		int counter = 0;
	    		for (String childKey : relations.get(parentKey)) {
		    		ContainerNodeElement ne = new ContainerNodeElement(childKey, all.get(childKey), icons.get(childKey));
		    		DcDefaultMutableTreeNode node = new DcDefaultMutableTreeNode(ne);
		    		model.insertNodeInto(node, parentNode, counter++);
		    		top.addItem(childKey, Integer.valueOf(DcModules._CONTAINER));
		    		createChildren(model, childKey, node, relations, all, icons);
	    		}
	    	}
	    }
 	}
}
