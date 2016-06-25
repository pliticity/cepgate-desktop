package pl.itcity.cg.desktop.user.impl;

import org.springframework.stereotype.Component;

import pl.itcity.cg.desktop.model.SessionContext;
import pl.itcity.cg.desktop.user.UserContext;

/**
 * java bean implementation of user context.
 * @author Michal Adamczyk
 */
@Component
public class UserContextImpl implements UserContext{

    private SessionContext sessionContext = SessionContext.ANONYMOUS;

    private boolean authorized;

    @Override
    public synchronized SessionContext getContext() {
        return sessionContext;
    }

    @Override
    public synchronized void setContext(SessionContext current) {
        this.sessionContext = current;
    }

    @Override
    public synchronized boolean isAuthorized() {
        return authorized;
    }

    @Override
    public synchronized void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
