package pl.itcity.cg.desktop.backend.interceptors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * @author Michal Adamczyk
 */
@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor{

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws
                                                                                                                                      IOException {
        UUID uuid = UUID.randomUUID();
        logRequest(httpRequest, bytes, uuid);
        Date start = new Date();
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        logResponse(uuid, response, start);
        return response;
    }

    /**
     * logs response
     *
     * @param uuid     request uuid
     * @param response response
     * @param start    start time
     * @throws IOException
     */
    private void logResponse(UUID uuid, ClientHttpResponse response, Date start) throws IOException {
        //[madamczy] response obiect is returned before real response is obtained! After statusCode can be get from request, we know that it is obained.
        HttpStatus statusCode = response.getStatusCode();
        long duration = new Date().getTime() - start.getTime();
        LOGGER.debug("###### Response obtained for request " + uuid.toString()+" duration = "+duration+"ms ######");
        LOGGER.debug("Response code: " + statusCode);
        LOGGER.debug("Response headers: " + response.getHeaders());
        InputStream responseStream = response.getBody();
        LOGGER.debug("Response body: " + IOUtils.toString(responseStream, StandardCharsets.UTF_8));
    }

    /**
     * logs request
     *
     * @param request request
     * @param body    request body
     * @param uuid    request uuid
     */
    private void logRequest(HttpRequest request, byte[] body, UUID uuid) {
        LOGGER.debug("###### Request: " + uuid.toString() + " ######");
        LOGGER.debug("Request URI: " + request.getURI());
        LOGGER.debug("Request method: " + request.getMethod());
        LOGGER.debug("Request headers: " + request.getHeaders());
        LOGGER.debug("Request body: " + new String(body));
    }
}
