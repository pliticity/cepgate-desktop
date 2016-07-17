package pl.itcity.cg.desktop.backend.service;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Cepgate service invoker
 *
 * @author Michal Adamczyk
 */
public interface ServiceInvoker {

    /**
     * invokes exchange on service
     *
     * @param url
     *         url
     * @param method
     *         method
     * @param requestEntity
     *         request entity with auth headers prepared
     * @param responseType
     *         response type
     * @param uriVariables
     *         uri variables
     * @param <T>
     *         type of response entity body
     * @return result of service invoke
     */
    <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables);

    /**
     * gets authentication headers
     *
     * @return authentication headers
     */
    HttpHeaders getAuthHeaders();
}
