package pl.itcity.cg.desktop.integration.service;

/**
 * Service class for token manipulation
 *
 * @author Patryk Majchrzycki
 */
public interface TokenService {

    String generateToken(String username);

    void registerToken(String token);

}
