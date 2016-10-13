package pl.itcity.cg.desktop.integration.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.itcity.cg.desktop.backend.service.RestServiceInvoker;
import pl.itcity.cg.desktop.integration.service.DesktopTokenDTO;
import pl.itcity.cg.desktop.integration.service.TokenService;

import java.text.MessageFormat;
import java.util.Date;

/**
 * {@link TokenService}
 *
 * @author Patryk Majchrzycki
 */
@Component
@Scope("prototype")
public class DesktopTokenService implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DesktopTokenService.class);

    private static final String TOKEN_PATTERN = "desktop.token.{0}.{1}";

    @Autowired
    private RestServiceInvoker restServiceInvoker;

    @Override
    public String generateToken(String username) {
        return MessageFormat.format(TOKEN_PATTERN, username, String.valueOf(new Date().getTime()));
    }

    @Override
    public void registerToken(String token) {
        LOGGER.info(MessageFormat.format("Registering {0} token", token));
        DesktopTokenDTO dto = new DesktopTokenDTO(token);
        HttpEntity<DesktopTokenDTO> entity = new HttpEntity<DesktopTokenDTO>(dto,restServiceInvoker.getAuthHeaders());
        ResponseEntity<Boolean> response = restServiceInvoker.exchange("/desktop", HttpMethod.POST,entity,Boolean.class);
        if(HttpStatus.OK.equals(response.getStatusCode())){
            LOGGER.info(MessageFormat.format("Registered {0} token", token));
        }else{
            LOGGER.info(MessageFormat.format("Failed to register {0} token", token));
        }
    }
}
