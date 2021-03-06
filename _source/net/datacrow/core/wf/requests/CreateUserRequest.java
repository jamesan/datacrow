package net.datacrow.core.wf.requests;

import net.datacrow.core.db.DatabaseManager;
import net.datacrow.core.objects.helpers.User;

/**
 * Creates a new user including all her permissions.
 * 
 * @author Robert Jan van der Waals
 */
public class CreateUserRequest implements IRequest {

    private static final long serialVersionUID = -300657035562085171L;
    private User user;

    public CreateUserRequest(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        DatabaseManager.createUser(user, "");
    }
    
    @Override
    public void end() {
        user = null;
    }
    
    @Override
    public boolean getExecuteOnFail() {
        return false;
    }

    @Override
    public void setExecuteOnFail(boolean b) {}
}
