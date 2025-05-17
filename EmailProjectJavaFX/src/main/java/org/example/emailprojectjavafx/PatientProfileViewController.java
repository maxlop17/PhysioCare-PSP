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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Appointment.AppointmentListResponse;
import org.example.emailprojectjavafx.models.Appointment.AppointmentResponse;
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Patient.PatientResponse;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Physio.PhysioResponse;
import org.example.emailprojectjavafx.models.Record.Record;
import org.example.emailprojectjavafx.models.Record.RecordListResponse;
import org.example.emailprojectjavafx.models.Record.RecordResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

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
    public ImageView imgProfile;
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
                        controller.setAppointment(lvAppointments.getSelectionModel().getSelectedItem());
                        controller.setPatient(patient);
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
                    ServiceUtils.makePetition(new GenericPetition<>(
                            "physios", item.getPhysio(), "GET", null, PhysioResponse.class,
                            physioResponse -> {
                                Platform.runLater(() -> {
                                    setText(getAppointmentText(item, physioResponse.getPhysio()));
                                });
                            }, "Failed to fetch physio"
                    ));
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
        ServiceUtils.makePetition(new GenericPetition<>(
                "records", patient.getId( )+ "/patient", "GET", null, RecordListResponse.class,
                recordListResponse -> {
                    Record record = recordListResponse.getRecords().getFirst();
                    lblRecord.setText(record.getMedicalRecord());
                    System.out.println(record.getAppointments());
                    record.getAppointments().forEach(this::getAppointmentById);
                }, "Failed to fetch appointments"
        ));
    }

    private void getAppointmentById(String id){
        ServiceUtils.makePetition(new GenericPetition<>(
                "records", "/appointments/" + id, "GET", null, AppointmentResponse.class,
                appointmentResponse -> {
                    Platform.runLater(() -> {
                        lvAppointments.getItems().add(appointmentResponse.getAppointment());
                    });
                }, "Failed to fetch appointment"
        ));
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

    public void onAddAppointment(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/appointment-detail-view.fxml";
        String title = "New appointment | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void onChangePhotoClick(ActionEvent actionEvent) {
    }
}
