package pl.itcity.cg.desktop.backend.service;

import java.util.Collections;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pl.itcity.cg.desktop.user.UserContext;

/**
 * Service invoker implementation based on rest template
 *
 * @author Michal Adamczyk
 */
@Component
public class RestServiceInvoker implements ServiceInvoker{

    /**
     * cookie header key
     */
    private static final String COOKIE = "Cookie";

    @Autowired
    private String baseUrl;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private UserContext userContext;

    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
        return restTemplate.exchange(baseUrl + url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * gets auth headers based on user context
     *
     * @return http headers or null if user is not authenticated
     */
    @Override
    public HttpHeaders getAuthHeaders() {
        if (userContext.isAuthorized()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.put(COOKIE, Collections.singletonList(userContext.getContext()
                                                                      .getCookie()));
            return httpHeaders;
        }
        return null;
    }
}
