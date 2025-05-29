package org.example.emailprojectjavafx.models.Physio;

public class UpdatePhysio {
    private Physio physio;
    private String login;
    private String password;

    public UpdatePhysio(Physio physio, String login, String password) {
        this.physio = physio;
        this.login = login;
        this.password = password;
    }

    public Physio getPhysio() {
        return physio;
    }

    public void setPhysio(Physio physio) {
        this.physio = physio;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
