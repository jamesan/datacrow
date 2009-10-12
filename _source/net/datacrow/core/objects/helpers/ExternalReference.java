package net.datacrow.core.objects.helpers;

import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;

public class ExternalReference extends DcObject {

    private static final long serialVersionUID = 9031499353731926500L;

    public static final int _EXTERNAL_ID = 1;
    public static final int _EXTERNAL_ID_TYPE = 2;
    
    public ExternalReference() {
        super(DcModules._EXTERNALREFERENCE);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ExternalReference && ((DcObject) o).getID() != null &&  
               ((DcObject) o).getID().equals(getID());
    }

    @Override
    public String toString() {
        return getDisplayString(_EXTERNAL_ID_TYPE) + ": " + getDisplayString(_EXTERNAL_ID); 
    }
}
