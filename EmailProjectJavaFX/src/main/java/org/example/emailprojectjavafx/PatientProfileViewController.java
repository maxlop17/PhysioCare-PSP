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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Appointment.AppointmentResponse;
import org.example.emailprojectjavafx.models.GenericPetition;
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
    public ImageView imgProfile;
    Gson gson = new Gson();

    public void setPatient(Patient patient) {
        this.patient = patient;
        loadData();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //We set the image
        Image image = new Image(getClass().getResource("/images/user_placeholder.png").toString());
        imgProfile.setImage(image);


        //Configuration to change the window on double click to the appointment
        lvAppointments.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
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

    private void loadData() {
        if (patient != null) {
            lblName.setText(patient.getName());
            lblSurname.setText(patient.getSurname());
            lblBirthdate.setText(String.valueOf(patient.getBirthDate()));
            lblAddress.setText(patient.getAddress());
            lblEmail.setText(patient.getEmail());
            lblInsurance.setText(patient.getInsuranceNumber());
            getAppointments();
        }
    }

    private void getAppointments() {
        ServiceUtils.makePetition(new GenericPetition<>(
                "records", patient.getId() + "/patient", "GET", null, RecordListResponse.class,
                recordListResponse -> {
                    Record record = recordListResponse.getRecords().getFirst();
                    Platform.runLater(() -> {
                        lblRecord.setText(record.getMedicalRecord());

                        lvAppointments.getItems().setAll(record.getAppointments());
                    });
                }, "Failed to fetch appointments"
        ));
    }



    public String getAppointmentText(Appointment appointment, Physio physio) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String confirmed = "Not specified";
        if (appointment.getConfirmed() != null)
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

    public void onAddAppointment(ActionEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/appointment-detail-view.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            showAlert("ERROR", e.getMessage(), 2);
            e.printStackTrace();
            return;
        }



        AppointmentDetailViewController controller = loader.getController();
        controller.setPatient(patient);

        Node source = (Node) mouseEvent.getSource();
        String title = "New Appointment| PhysioCare";
        Utils.switchView(source, root, title);
    }

    public void onChangePhotoClick(ActionEvent actionEvent) {
    }
}
