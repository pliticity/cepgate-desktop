package pl.itcity.cg.desktop.model;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Michal Adamczyk
 */
public class Principal {

    /**
     * email
     */
    private String email;
    /**
     * password
     */
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * default constructor
     */
    public Principal() {

    }

    /**
     * constructor initializing all fields
     * @param email email
     * @param password password
     */
    public Principal(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("email", email)
                .append("password", password)
                .toString();
    }
}
