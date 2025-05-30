package org.example.emailprojectjavafx.models.Patient;

public class PatientRequest {
    private Patient patient;
    private String login;
    private String password;

    public PatientRequest(Patient patient, String login, String password) {
        this.patient = patient;
        this.login = login;
        this.password = password;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
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
