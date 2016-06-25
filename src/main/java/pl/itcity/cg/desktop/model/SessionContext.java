package pl.itcity.cg.desktop.model;

import java.util.Objects;

/**
 * session context bean
 *
 * @author Michal Adamczyk
 */
public class SessionContext {

    public static final String ANONYMOUS_USER = "anonymous";

    public static final SessionContext ANONYMOUS = new SessionContext(ANONYMOUS_USER, null);

    /**
     * proncipal
     */
    private String user;

    /**
     * auth cookie value
     */
    private String cookie;

    /**
     * useful constructor initializing all fields
     *
     * @param user
     *         user
     * @param cookie
     *         cookie
     */
    public SessionContext(String user, String cookie) {
        this.user = user;
        this.cookie = cookie;
    }

    public String getUser() {
        return user;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    /**
     * checks if context is anonymous
     * @return true iff context is anonymous
     */
    public boolean isAnonymous(){
        return ANONYMOUS_USER == user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionContext)) {
            return false;
        }
        SessionContext that = (SessionContext) o;
        return Objects.equals(user, that.user) && Objects.equals(cookie, that.cookie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, cookie);
    }
}
