package org.example.emailprojectjavafx;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Appointment.AppointmentResponse;
import org.example.emailprojectjavafx.models.Patient.PatientResponse;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Record.RecordResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;

public class AppointmentDetailViewController implements Initializable {
    @FXML
    public DatePicker dpDate;
    @FXML
    public ComboBox<Physio> cbPhysio;
    @FXML
    public TextArea txtDiagnosis;
    @FXML
    public TextArea txtTreatment;
    @FXML
    public TextArea txtObservations;
    @FXML
    public Label lblConfirmationStatus;
    private Appointment appointment;
    Gson gson = new Gson();
    private boolean isPatient = false;

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void setIsPatient(boolean isPatient) {
        this.isPatient = isPatient;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(appointment != null){
            txtDiagnosis.setText(appointment.getDiagnosis());
            txtTreatment.setText(appointment.getTreatment());
            txtObservations.setText(appointment.getObservations());

        }

    }

    public void onToggleConfirmation(ActionEvent actionEvent) {
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        changeWindow(actionEvent);
    }

    public void onAddAppointment(ActionEvent actionEvent) {
    }

    public void onUpdateAppointment(ActionEvent actionEvent) {
        String url = ServiceUtils.SERVER + "/appointments/" + appointment.getId();
        ServiceUtils.getResponseAsync(url, null, "PUT")
                .thenApply(json ->
                        gson.fromJson(json, AppointmentResponse.class)
                ).thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            showAlert("Updated appointment", response.getAppointment().getDiagnosis() + " updated", 1);
                        });
                    } else {
                        showAlert("Error", response.getError(), 2);
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to fetch appointments", 2);
                    return null;
                });
    }

    public void onDeleteAppointment(ActionEvent actionEvent) {
        String url = ServiceUtils.SERVER + "/appointments/" + appointment.getId();
        String jsonRequest = "";

        ServiceUtils.getResponseAsync(url, jsonRequest, "DELETE")
                .thenApply(json -> gson.fromJson(json, AppointmentResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            showAlert("Deleted Appointment", response.getAppointment().getDiagnosis() +
                                    " -  " + response.getAppointment().getDate() + " deleted", 1);
                        });
                        changeWindow(actionEvent);
                    } else {
                        Platform.runLater(() -> showAlert("Error deleting patient", response.getError(), 2));
                    }
                })
                .exceptionally(_ -> {
                    showAlert("Error", "Failed to delete patient", 2);
                    return null;
                });
    }

    private void changeWindow(ActionEvent actionEvent){
        Node source = (Node) actionEvent.getSource();
        String fxmlFile;
        String title;
        if(isPatient) {
            fxmlFile = "/fxml/patient-profile-view.fxml";
            title = "Patients | PhysioCare";
        } else {
            fxmlFile = "/fxml/physio-profile-view.fxml";
            title = "Physio | PhysioCare";
        }
        Utils.switchView(source, fxmlFile, title);
    }

}
