package pl.itcity.cg.desktop.model;

/**
 * Object for base json response
 * @author Michal Adamczyk
 */
public class JsonResponse {

    private String message;
    private boolean success;
    private String exceptionName;

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
