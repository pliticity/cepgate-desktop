package pl.itcity.cg.desktop.integration.service;

/**
 * Service class for jms operations
 *
 * @author Patryk Majchrzycki
 */
public interface JMSService {

    void initChannel(String token);

    void connect(String token);

}
