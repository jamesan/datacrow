package net.datacrow.console.components.panels.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.datacrow.core.objects.DcObject;
import net.datacrow.util.DcImageIcon;

public class NodeElement {

    protected Object key;
    protected String displayValue;
    protected DcImageIcon icon;
    
    private List<String> items = new ArrayList<String>();
    
    public NodeElement(Object key, String displayValue, DcImageIcon icon) {
        this.key = key;   
        this.displayValue = displayValue;
        this.icon = icon;
    }
    
    public void addItem(String item) {
    	if (!items.contains(item))
    		items.add(item);
    }
    
    public void removeItem(String item) {
    	items.remove(item);
    }
    
    public int getCount() {
        return items.size();
    }
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    public List<String> getItems() {
    	return new ArrayList(items);
    }
    
    public List<String> getItemsSorted(List<String> allOrderedItems) {
    	
    	if (allOrderedItems.size() == items.size()) 
    		return new ArrayList<String>(items);
    	
    	List<String> result = new ArrayList<String>();
    	for (String orderedItem : allOrderedItems) {
    		for (String item : items) {
    			if (item.equals(orderedItem)) {
    				result.add(item);
    			}
    		}
    	}
    	return result;
    }
    
    public void setItems(List<String> items) {
    	this.items = items;
    }

    public String getComparableKey() {
        return getDisplayValue().toLowerCase();
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    public Object getKey() {
        return key;
    }
    
    public void clear() {
        if (key instanceof DcObject) ((DcObject) key).release();
        if (icon != null) icon.flush();
        
        key = null;
        icon = null;
        displayValue = null;
        
        if (items != null) {
        	items.clear();
        	items = null;
        }
        
    }

    @Override
    public String toString() {
        return getDisplayValue() + " (" + String.valueOf(getCount()) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof NodeElement))
            return false;
        else 
            return getComparableKey().equals(((NodeElement) o).getComparableKey());
    }

    @Override
    public int hashCode() {
        return getComparableKey().hashCode();
    }
}
