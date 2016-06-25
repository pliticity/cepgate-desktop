package pl.itcity.cg.desktop.model;

/**
 * Login result object
 *
 * @author Michal Adamczyk
 */
public class LoginResult {
    /**
     * cookie value
     */
    private String cookie;

    private final JsonResponse jsonResponse;

    /**
     * constructor initializing necessary fields
     *
     * @param jsonResponse
     *         json response
     */
    public LoginResult(JsonResponse jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public JsonResponse getJsonResponse() {
        return jsonResponse;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }

}
