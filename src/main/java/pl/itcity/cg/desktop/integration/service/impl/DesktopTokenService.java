package pl.itcity.cg.desktop.integration.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.itcity.cg.desktop.integration.service.TokenService;

import java.text.MessageFormat;
import java.util.Date;

/**
 * {@link TokenService}
 *
 * @author Patryk Majchrzycki
 */
@Service
public class DesktopTokenService implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DesktopTokenService.class);

    private static final String TOKEN_PATTERN = "desktop.token.{0}.{1}";

    @Override
    public String generateToken(String username) {
        return MessageFormat.format(TOKEN_PATTERN, username,String.valueOf(new Date().getTime()));
    }

    @Override
    public void registerToken(String token) {
        LOGGER.info(MessageFormat.format("Registering {0} token", token));
        //TODO
    }
}
