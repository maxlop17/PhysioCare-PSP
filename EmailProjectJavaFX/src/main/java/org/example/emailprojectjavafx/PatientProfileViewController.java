package org.example.emailprojectjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Patient.Patient;

import java.net.URL;
import java.util.ResourceBundle;

public class PatientProfileViewController implements Initializable {
    @FXML
    public Label lblId;
    @FXML
    public Label lblName;
    @FXML
    public Label lblSurname;
    @FXML
    public Label lblBirthdate;
    @FXML
    public Label lblInsurance;
    @FXML
    public Label lblEmail;
    @FXML
    public Label lblAddress;
    @FXML
    public Label lblRecord;
    @FXML
    public ListView<Appointment> lvAppointments;
    public Patient patient;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblId.setText(String.valueOf(patient.getId()));
        lblName.setText(patient.getName());
        lblSurname.setText(patient.getSurname());
        lblBirthdate.setText(String.valueOf(patient.getBirthDate()));
        lblAddress.setText(patient.getAddress());
        lblEmail.setText(patient.getEmail());
        lblRecord.setText(patient.);
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
    }

}
