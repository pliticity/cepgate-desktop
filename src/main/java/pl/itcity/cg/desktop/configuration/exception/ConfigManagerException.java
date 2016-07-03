package pl.itcity.cg.desktop.configuration.exception;

/**
 * exception encapsulating config manager problems
 * @author Michal Adamczyk
 */
public class ConfigManagerException extends RuntimeException{
    public ConfigManagerException() {
    }

    public ConfigManagerException(String message) {
        super(message);
    }

    public ConfigManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
