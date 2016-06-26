package pl.itcity.cg.desktop.concurrent;

import java.util.Collections;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javafx.concurrent.Service;
import pl.itcity.cg.desktop.model.LoginResult;
import pl.itcity.cg.desktop.user.UserContext;

/**
 * base class for async services calling cepgate rest service
 *
 * @author Michal Adamczyk, HYCOM S.A.
 */
public abstract class BaseRestService<V> extends Service<V> {
    /**
     * cookie header name
     */
    protected static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";
    @Autowired
    protected String baseUrl;
    @Resource
    protected RestTemplate restTemplate;

    @Resource
    UserContext userContext;

    /**
     * gets auth headers based on user context
     *
     * @return http headers or null if user is not authenticated
     */
    protected HttpHeaders getAuthHeaders() {
        if (userContext.isAuthorized()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.put(COOKIE, Collections.singletonList(userContext.getContext()
                                                                      .getCookie()));
            return httpHeaders;
        }
        return null;
    }
}
