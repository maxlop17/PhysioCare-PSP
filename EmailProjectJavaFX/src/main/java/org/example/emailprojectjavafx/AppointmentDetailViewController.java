package org.example.emailprojectjavafx;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Appointment.AppointmentResponse;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Physio.PhysioListResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
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
    private Appointment appointment = new Appointment();
    Gson gson = new Gson();
    private Patient patient = null;
    private Physio physio = null;

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        fillData();
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setPhysio(Physio physio) {
        this.physio = physio;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private void fillData(){
        if(appointment != null){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            dpDate.getEditor().setText(formatter.format(appointment.getDate()));
            txtDiagnosis.setText(appointment.getDiagnosis());
            txtTreatment.setText(appointment.getTreatment());
            txtObservations.setText(appointment.getObservations());
            if(appointment.getConfirmed() != null){
                lblConfirmationStatus.setText(appointment.getConfirmed() ? "Appointment confirmed" :
                        "Pending verification");
            }
            getPhysios();
        }
    }

    public void onToggleConfirmation(ActionEvent actionEvent) {
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        changeWindow(actionEvent);
    }

    public void onAddAppointment(ActionEvent actionEvent) {
    }

    private Appointment getValidatedDataFromForm() {
        String diagnosis = txtDiagnosis.getText();
        String treatment = txtTreatment.getText();
        String observations = txtObservations.getText();
        Date date = toDate(dpDate.getEditor().getText());
        Boolean confirmed = lblConfirmationStatus.equals("Appointment confirmed");
        Physio physio = cbPhysio.getSelectionModel().getSelectedItem();

        if (diagnosis.isEmpty() || treatment.isEmpty() || observations.isEmpty() ||
                Objects.requireNonNull(date).before(new Date())) {
            showAlert("Error", "Please fill all the fields correctly.", 2);
            return null;
        }
        return new Appointment(date, physio.getId(), diagnosis, treatment, observations, confirmed);
    }

    /**
     * Method that gets a date in a string format and transforms it to date
     * @param date the date that needs to be transformed
     * @return the date in Date format
     */
    private Date toDate (String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            return formatter.parse(date);
        } catch(ParseException e){
            showAlert("Error", e.getMessage(), 2);
        }
        return null;
    }

    public void onUpdateAppointment(ActionEvent actionEvent) {
        String url = ServiceUtils.SERVER + "/appointments/" + appointment.getId();
        String jsonRequest = gson.toJson(getValidatedDataFromForm());
        ServiceUtils.getResponseAsync(url, jsonRequest, "PUT")
                .thenApply(json ->
                        gson.fromJson(json, AppointmentResponse.class)
                ).thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            showAlert("Updated appointment", "Appointment updated", 1);
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
        try {
            Parent root;
            String title;
            Node source = (Node) actionEvent.getSource();
            if(patient != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/patient-profile-view.fxml"));
                root = loader.load();
                PatientProfileViewController controller =  loader.getController();
                controller.setPatient(patient);
                title = "Patient | PhysioCare ";
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/physio-profile-view.fxml"));
                root = loader.load();
                PhysioProfileViewController controller = loader.getController();
                controller.setPhysio(physio);
                title = "Physio | PhysioCare";
            }
            Utils.switchView(source, root, title);
        } catch (IOException e) {
            Utils.showAlert("Error", "Error getting the profile", 2);
        }
    }

    private void getPhysios(){
        String url = ServiceUtils.SERVER + "/physios";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json ->
                        gson.fromJson(json, PhysioListResponse.class)
                ).thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            cbPhysio.getItems().addAll(response.getPhysios());
                            if(appointment.getPhysio() != null){
                                for(Physio p : response.getPhysios()){
                                    if(p.getId().equals(appointment.getPhysio())){
                                        cbPhysio.getSelectionModel().select(p);
                                    }
                                }
                            }
                        });
                    } else {
                        showAlert("Error", response.getError(), 2);
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to fetch appointments", 2);
                    return null;
                });
    }

}
