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
import org.example.emailprojectjavafx.models.Appointment.AppointmentResponse;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Physio.PhysioResponse;
import org.example.emailprojectjavafx.models.Record.Record;
import org.example.emailprojectjavafx.models.Record.RecordListResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;

public class PatientProfileViewController implements Initializable {
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

    public void setPatient(Patient patient) {
        this.patient = patient;
        loadData();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Configuration to change the window on double click to the appointment
        lvAppointments.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/appointment-detail-view.fxml"));
                        Parent root = loader.load();
                        AppointmentDetailViewController controller = loader.getController();
                        controller.setPatient(patient);
                        controller.setAppointment(lvAppointments.getSelectionModel().getSelectedItem());
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        Utils.showAlert("Error", "Error getting the detail", 2);
                    }
                }
            }
        });

        //Configuration to set the text on the listview
        lvAppointments.setCellFactory(appointmentListView -> new ListCell<Appointment>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String url = ServiceUtils.SERVER + "/physios/" + item.getPhysio();
                    ServiceUtils.getResponseAsync(url, null, "GET")
                            .thenApply(json -> gson.fromJson(json, PhysioResponse.class)
                            ).thenAccept(response -> {
                                if (response.isOk()) {
                                    Platform.runLater(() -> {
                                        setText(getAppointmentText(item, response.getPhysio()));
                                    });
                                } else {
                                    showAlert("Error", response.getError(), 2);
                                }
                            }).exceptionally(_ -> {
                                showAlert("Error", "Failed to fetch physio", 2);
                                return null;
                            });
                }
            }
        });
    }

    private void loadData(){
        if(patient != null) {
            lblName.setText(patient.getName());
            lblSurname.setText(patient.getSurname());
            lblBirthdate.setText(String.valueOf(patient.getBirthDate()));
            lblAddress.setText(patient.getAddress());
            lblEmail.setText(patient.getEmail());
            lblInsurance.setText(patient.getInsuranceNumber());
            getAppointments();
        }
    }

    private void getAppointments(){
        String url = ServiceUtils.SERVER + "/records/" + patient.getId() + "/patient";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, RecordListResponse.class)
                ).thenAccept(response -> {
                    if (response.isOk()) {
                        Record record = response.getRecords().getFirst();
                        lblRecord.setText(record.getMedicalRecord());
                        System.out.println(record.getAppointments());
                        record.getAppointments().forEach(this::getAppointmentById);
                    } else {
                        showAlert("Error", response.getError(), 2);
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to fetch appointments", 2);
                    return null;
                });
    }

    private void getAppointmentById(String id){
        String url = ServiceUtils.SERVER + "/appointments/" + id;
        System.out.println(url);
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> {
                            System.out.println(gson.fromJson(json, AppointmentResponse.class));
                            return gson.fromJson(json, AppointmentResponse.class);
                        }
                ).thenAccept(response -> {
                    if (response.isOk()) {
                        System.out.println(response);
                        Platform.runLater(() -> {
                            lvAppointments.getItems().add(response.getAppointment());
                        });
                    } else {
                        showAlert("Error", response.getError(), 2);
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to fetch appointment", 2);
                    return null;
                });
    }

    public String getAppointmentText(Appointment appointment, Physio physio){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String confirmed = "Not specified";
        if(appointment.getConfirmed() != null)
            confirmed = appointment.getConfirmed() ? "Confirmed" : "Pending verification";
        return formatter.format(appointment.getDate()) + " | Physio: " + physio.getName() +
        " " + physio.getSurname() + " | " + appointment.getDiagnosis() + " | " + confirmed;
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/patients-view.fxml";
        String title = "Patients | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void onAddAppointment(MouseEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/appointment-detail-view.fxml"));
        Parent root = null;
        try{
            root = loader.load();
        }catch(IOException e){
            showAlert("ERROR", e.getMessage(), 2);
        }
        // Obtener el controlador y pasarle el objeto
        AppointmentDetailViewController controller = loader.getController();
        controller.setPatient(patient);

        Node source = (Node) mouseEvent.getSource();
        String title = "New Appointment| PhysioCare";
        Utils.switchView(source, root, title);
    }
}
