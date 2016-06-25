package pl.itcity.cg.desktop.user;

import pl.itcity.cg.desktop.model.SessionContext;

/**
 * Iterface for user context implementation. Implementations should be either thread-safe or accessed only from main thread
 *
 * @author Michal Adamczyk
 */
public interface UserContext {

    /**
     * gets current context
     * @return current context or null if context was not initialized yet
     */
    SessionContext getContext();

    /**
     * sets current session context
     * @param current session context to set
     */
    void setContext(SessionContext current);

    /**
     * @return true if user is authorized
     */
    boolean isAuthorized();

    /**
     * set authorized flag
     *
     * @param authorized
     *         authorized
     */
    void setAuthorized(boolean authorized);
}
