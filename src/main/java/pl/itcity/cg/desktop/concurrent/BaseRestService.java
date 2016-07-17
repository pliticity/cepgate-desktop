package pl.itcity.cg.desktop.concurrent;

import javax.annotation.Resource;

import org.springframework.http.HttpHeaders;

import javafx.concurrent.Service;
import pl.itcity.cg.desktop.backend.service.ServiceInvoker;

/**
 * base class for async services calling cepgate rest service
 *
 * @author Michal Adamczyk
 */
public abstract class BaseRestService<V> extends Service<V> {
    /**
     * cookie header name
     */
    protected static final String SET_COOKIE = "Set-Cookie";

    @Resource
    protected ServiceInvoker serviceInvoker;

    /**
     * gets auth headers based on user context
     *
     * @return http headers or null if user is not authenticated
     */
    protected HttpHeaders getAuthHeaders() {
        return serviceInvoker.getAuthHeaders();
    }
}
