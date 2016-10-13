package pl.itcity.cg.desktop.integration.service;

public class DesktopTokenDTO {

    public DesktopTokenDTO(String token) {
        this.token = token;
    }

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
