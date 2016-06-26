package pl.itcity.cg.desktop.concurrent;

import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javafx.concurrent.Task;
import pl.itcity.cg.desktop.model.JsonResponse;
import pl.itcity.cg.desktop.model.LoginResult;
import pl.itcity.cg.desktop.model.Principal;

/**
 * @author Michal Adamczyk
 */
@Component
@Scope("prototype")
public class LoginService extends BaseRestService<LoginResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private Principal principal;

    private static final String LOGIN_URL = "auth";


    protected Task<LoginResult> createTask() {
        return new Task<LoginResult>() {

            private final Principal taskPrincipal = Principal.copyOf(principal);
            @Override
            protected LoginResult call() throws Exception {
                LOGGER.info("about to login principal: "+taskPrincipal);
                HttpEntity<Principal> requestEntity = new HttpEntity<>(taskPrincipal, getAuthHeaders());
                ResponseEntity<JsonResponse> exchange = restTemplate.exchange(baseUrl + LOGIN_URL, HttpMethod.POST, requestEntity, JsonResponse.class);
                LoginResult loginResult = new LoginResult(exchange.getBody());
                if (loginResult.getJsonResponse().isSuccess()){
                    HttpHeaders headers = exchange.getHeaders();
                    Optional.ofNullable(headers.get(SET_COOKIE))
                            .orElse(Collections.emptyList())
                            .stream()
                            .findFirst().ifPresent(loginResult::setCookie);
                }
                return loginResult;
            }
        };
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }
}
