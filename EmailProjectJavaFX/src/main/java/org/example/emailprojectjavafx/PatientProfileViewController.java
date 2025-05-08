package org.example.emailprojectjavafx;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Appointment.AppointmentListResponse;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Record.RecordResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;

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
    Gson gson = new Gson();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(patient != null) {
            lblId.setText(String.valueOf(patient.getId()));
            lblName.setText(patient.getName());
            lblSurname.setText(patient.getSurname());
            lblBirthdate.setText(String.valueOf(patient.getBirthDate()));
            lblAddress.setText(patient.getAddress());
            lblEmail.setText(patient.getEmail());
            getAppointments();
        }

        lvAppointments.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-profile-view.fxml"));
                        Parent root = loader.load();
                        AppointmentDetailViewController controller = loader.getController();
                        controller.setAppointment(lvAppointments.getSelectionModel().getSelectedItem());
                        controller.setIsPatient(true);
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        Utils.showAlert("Error", "Error getting the detail", 2);
                    }
                }
            }
        });
    }

    private void getAppointments(){
        String url = ServiceUtils.SERVER + "/records/" + patient.getId() + "/patient";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json ->
                        gson.fromJson(json, RecordResponse.class)
                ).thenAccept(response -> {
                    if (response.isOk()) {
                        List<Appointment> apps = response.getRecord().getAppointments();
                        Platform.runLater(() -> {
                            lvAppointments.getItems().setAll(apps);
                        });
                    } else {
                        showAlert("Error", response.getError(), 2);
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to fetch appointments", 2);
                    return null;
                });
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/patients-view.fxml";
        String title = "Patients | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

}
