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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.datacrow.console.ComponentFactory;
import net.datacrow.console.Layout;
import net.datacrow.console.components.DcTree;
import net.datacrow.console.views.MasterView;
import net.datacrow.console.views.View;
import net.datacrow.core.objects.DcObject;

import org.apache.log4j.Logger;

public abstract class TreePanel extends JPanel implements TreeSelectionListener {
    
    private static Logger logger = Logger.getLogger(TreePanel.class.getName());
    
    protected DcTree tree;
    private JScrollPane scroller;
    protected DcDefaultMutableTreeNode top;
    
    private boolean listenForSelection = true;
    
    protected GroupingPane gp;
    protected Object currentUserObject;
    
    private boolean saveChanges = true;
    
    public TreePanel(GroupingPane gp) {
        this.gp = gp;
        
        setLayout(Layout.getGBL());
        
        JMenuBar menu = getMenu();
        if (menu != null) {
            add(menu, Layout.getGBC( 0, 0, 1, 1, 1.0, 1.0
                    ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                     new Insets(5, 5, 0, 5), 0, 0));
        }
        
        build();
    }
    
    public MasterView getView() {
        return gp.getView();
    }
    
    public boolean isListeningForSelection() {
        return listenForSelection;
    }
    
    public void setListeningForSelection(boolean b) {
        listenForSelection = b;
    }
    
    public boolean isHoldingItems() {
        return top != null;
    }
    
    public Object getLastSelectedPathComponent() {
        return tree.getLastSelectedPathComponent();
    }
    
    public DcDefaultMutableTreeNode getTopNode() {
        return top;
    }
    
    public void setSaveChanges(boolean b) {
        saveChanges = b;
    }
    
    public boolean isSaveChanges() {
        return saveChanges;
    }
    
    public int getModule() {
        return gp.getModule();
    }
    
    public boolean isLoaded() {
        return top != null && top.getItemCount() > 0;
    }
    
    public void add(DcObject dco) {
        DcDefaultMutableTreeNode path = getFullPath(dco);
        
        if (top != null) {
            NodeElement ne = (NodeElement) top.getUserObject();
            ne.addItem(dco.getID());
            add(dco, path, top);
        }
    }
    
    /**
     * Adds recursive
     * @param child Does not need to exist!
     * @param parent Existing parent
     */
    private void add(DcObject dco, DcDefaultMutableTreeNode node, DcDefaultMutableTreeNode parent) {
    	DcDefaultMutableTreeNode child;
    	DcDefaultMutableTreeNode existingChild;
    	for (int i = 0; i < node.getChildCount(); i++) {
    	    child = (DcDefaultMutableTreeNode) node.getChildAt(i);
    	    existingChild = findNode(child, parent, dco.getID(), false);
    	    
    	    if (existingChild == null) {
    	        child.addItem(dco.getID());
    	        ((NodeElement) child.getUserObject()).addItem(dco.getID());
    	        insertNode(child, parent);
    	        existingChild = child;
    	    } else {
    	        existingChild.addItem(dco.getID());
    	    }
    	    
    	    add(dco, child, existingChild);
    	}
    }
    
    public void remove(DcObject dco) {
        DcDefaultMutableTreeNode path = getFullPath(dco);
        
        if (top != null) {
            NodeElement ne = (NodeElement) top.getUserObject();
            ne.removeItem(dco.getID());
            remove(dco.getID(), path, top);
        }
    }
    
    /**
     * Adds recursive
     * @param child Does not need to exist!
     * @param parent Existing parent
     */
    private void remove(String item, DcDefaultMutableTreeNode node, DcDefaultMutableTreeNode parent) {
        DcDefaultMutableTreeNode child;
        DcDefaultMutableTreeNode existingChild;
        for (int i = 0; i < node.getChildCount(); i++) {
            child = (DcDefaultMutableTreeNode) node.getChildAt(i);
            existingChild = findNode((DcDefaultMutableTreeNode) node.getChildAt(i), parent, item, false);
            
            if (existingChild == null)
                existingChild = findNode((DcDefaultMutableTreeNode) node.getChildAt(i), parent, item, false);
            
            if (existingChild != null) {
                existingChild.removeItem(item);
                
                if (existingChild.getItemCount() == 0)
                    removeNode(existingChild);
                
                remove(item, child, existingChild);
            }
        }
    }
    
    public void update(DcObject old, DcObject dco) {
    	remove(old);
    	add(dco);
    }
    
    public abstract DcDefaultMutableTreeNode getFullPath(DcObject dco);
    
    public NodeElement getNodeElement(Object key) {
    	if (key instanceof DcObject) {
    		DcObject dco = (DcObject) key;
    		return new NodeElement(key, dco.toString(), dco.getIcon());
    	} else {
    		return new NodeElement(key, key.toString(), null);
    	}
    }
    
    public void collapseAll() {
        if (top == null) return;
        
        collapseChildren(top);
        tree.collapsePath(new TreePath(top.getPath()));
    }   
    
    public void expandAll() {
        expandAll(top);
    }   
    
    private void expandAll(DefaultMutableTreeNode node) {
        try {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            int count = node.getChildCount();
            tree.expandPath(new TreePath(model.getPathToRoot(node)));
            
            DefaultMutableTreeNode current;
            for (int i = 0; i < count; i++) {
                current = (DefaultMutableTreeNode) node.getChildAt(i);
                tree.expandPath(new TreePath(model.getPathToRoot(current)));
                expandAll(current);
            }
        } catch (Exception e) {
            logger.warn("Failed to expand all nodes", e);
        }
    }
    
    protected void updateView(List<String> keys) {
        getView().getCurrent().add(keys);  
    }
    
    public void setDefaultSelection() {
        setListeningForSelection(true);
        try {
            if (isActive())
                tree.setSelectionInterval(1, 1);
            else 
                tree.setSelectionInterval(0, 0);
        } catch (Exception e) {
            logger.error(e, e);
        }
    }
    
    private void collapseChildren(DefaultMutableTreeNode parent) {
        int size = parent.getChildCount();
        
        DefaultMutableTreeNode child;
        for (int i = 0; i < size; i++) {
            try {
                child = (DefaultMutableTreeNode) parent.getChildAt(i);
                collapseChildren(child);
                tree.collapsePath(new TreePath(child.getPath()));
            } catch (Exception e) {
                logger.error("An error occurred while collapsing leafs of " + parent, e);
            }
        }
    }

    public void clear() {
        if (tree == null)
            return;

        if (top != null) {
            DefaultMutableTreeNode pn;
            DefaultMutableTreeNode cn;
            for (int i = 0; i < top.getChildCount(); i++) {
                pn = (DefaultMutableTreeNode) top.getChildAt(i);
                if (pn.getUserObject() != null) {
                    ((NodeElement) pn.getUserObject()).clear();
                    for (int j = 0; j < pn.getChildCount(); j++) {
                        cn = (DefaultMutableTreeNode) pn.getChildAt(j);
                        if (cn.getUserObject() != null)
                            ((NodeElement) cn.getUserObject()).clear();
                    }
                }
            }
        
            ComponentFactory.clean(tree);
            tree.removeTreeSelectionListener(this);
            tree = null;

            top.removeAllChildren();
            top = null;
        }
        
        if (scroller != null)
            remove(scroller);
        
        currentUserObject = null;
    }
    
    @Override
    public void setFont(Font font) {
    	super.setFont(font);
        if (tree != null)
            tree.setFont(font);
    }
    
    protected void build() {
        clear();
        
        createTopNode();
        
        tree = new DcTree(new DefaultTreeModel(top));
        tree.setFont(ComponentFactory.getStandardFont());
        tree.addTreeSelectionListener(this);
        
        scroller = new JScrollPane(tree);
        
        add(scroller,  Layout.getGBC( 0, 1, 1, 1, 50.0, 50.0
           ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
            new Insets(0, 5, 5, 5), 0, 0));
    }     
    
    public void reset() {
        createTree();
    }
    
    protected void setSelected(DefaultMutableTreeNode node) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        TreePath treePath = new TreePath(model.getPathToRoot(node));
        tree.setSelectionPath(treePath);
        tree.expandPath(treePath);
        tree.scrollPathToVisible(treePath);
        
        currentUserObject = node.getUserObject();
    }
    
    /**
     * Inserts a node. The node needs to have a valid user object defined
     * to insert it at the right position (ordering)
     */
    protected void insertNode(DefaultMutableTreeNode node, DefaultMutableTreeNode parent) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        
        NodeElement ne = (NodeElement) node.getUserObject();
        
        List<String> elements = new ArrayList<String>();
        NodeElement child;
        for (int i = 0; i < parent.getChildCount(); i++) {
            child = ((NodeElement) ((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject());
            elements.add(child.getComparableKey());
        }
        
        elements.add(ne.getComparableKey());
        Collections.sort(elements);
        int idx = elements.indexOf(ne.getComparableKey());
        
        model.insertNodeInto(node, parent, idx);
        tree.expandPath(new TreePath(model.getPathToRoot(node)));
    }

    /**
     * Recursive search method for tree nodes.
     * @param key
     * @param parent
     * @param recurse
     */
    protected DcDefaultMutableTreeNode findNode(DcDefaultMutableTreeNode child, 
                                                DcDefaultMutableTreeNode parent, 
                                                String item,
                                                boolean recurse) {
        
        int count = parent != null ? parent.getChildCount() : 0;
        
        if (parent == null && getTopNode().equals(child))
            return getTopNode();
        
        DcDefaultMutableTreeNode node;
        DcDefaultMutableTreeNode result = null;
        for (int i = 0; i < count; i++) {
            node = (DcDefaultMutableTreeNode) parent.getChildAt(i);

            if (child.equals(node) &&  node.contains(item))
                result = node;
            
            if (result == null && recurse)
                result = findNode(child, node, item, recurse);
            
            if (result != null) return result;
        }
        
        return null;
    }  
    
    protected abstract JMenuBar getMenu();
    protected abstract void createTopNode();
    protected abstract void createTree();
    
    protected void refresh() {
        TreePath path = tree.getSelectionPath();
        groupBy();
        tree.setSelectionPath(path);
        
        if (tree.getSelectionPath() == null)
            setDefaultSelection();
    }
    
    protected boolean isActive() {
        return true;
    }
    
    public abstract void groupBy();
    
    private void removeNode(DefaultMutableTreeNode child) {
    	 DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    	 model.removeNodeFromParent(child);
    
    	 // remove empty branches above (needed for the file tree panel)
    	 DefaultMutableTreeNode parent = (DefaultMutableTreeNode) child.getParent();
    	 NodeElement ne;
    	 while (parent != null) {
    		 ne = ((NodeElement) child.getUserObject());
    		 if (ne.getCount() == 0 && parent.getChildCount() == 0) {
    			 DefaultMutableTreeNode newParent = null;

    			 try {
    				 newParent = (DefaultMutableTreeNode) parent.getParent();
    			 } catch (Exception e) {}
   			    
    			 try {
    				 model.removeNodeFromParent(parent);
    				 parent = newParent;
    			 } catch (IllegalArgumentException iae) {
   			  	  	parent = null;
   				}
    		 } else {
    		     parent = null;
    		 }
    	 }
    }
    
    /************************************************************************
     * Selection listener
     ************************************************************************/
    
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        
        if (!isListeningForSelection())
            return;
        
        if (e.getNewLeadSelectionPath() == null || 
            e.getNewLeadSelectionPath().equals(e.getOldLeadSelectionPath()))
            return;
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node == null) {
            currentUserObject = null;
            return;
        }

        Object o = node.getUserObject();
        
        View currentView = getView().getCurrent();
        if (currentView != null) {
            currentView.clear(isSaveChanges());
            NodeElement currentNode = (NodeElement) o;
            setSelected(node);
            updateView(currentNode.getItems());
        }
    }
}
