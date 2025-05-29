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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Appointment.AppointmentResponse;
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Patient.PatientResponse;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Physio.PhysioResponse;
import org.example.emailprojectjavafx.models.Record.Record;
import org.example.emailprojectjavafx.models.Record.RecordListResponse;
import org.example.emailprojectjavafx.models.Record.RecordRequest;
import org.example.emailprojectjavafx.models.Record.RecordResponse;
import org.example.emailprojectjavafx.models.User.UserResponse;
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
    @FXML
    public VBox vBoxContainer;
    @FXML
    public Button btnCreateRecord;
    @FXML
    public Button btnAddAppointment;
    public TextField txtRecord = new TextField();
    public Patient patient;
    public ImageView imgProfile;
    private Gson gson = new Gson();

    public void setPatient(Patient patient) {
        this.patient = patient;
        loadData();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnCreateRecord.setDisable(true);
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
            lblBirthdate.setText(new SimpleDateFormat("dd/MM/yyyy").format(patient.getBirthDate()));
            lblAddress.setText(patient.getAddress());
            lblEmail.setText(patient.getEmail());
            lblInsurance.setText(patient.getInsuranceNumber());
            ServiceUtils.makePetition(new GenericPetition<>(
                    "users", patient.getId(), "GET", null, UserResponse.class,
                    userResponse -> {
                        Platform.runLater(() -> {
                            String avatar = userResponse.getUser().getAvatar();
                            if(avatar != null && !avatar.isEmpty()){
                                imgProfile.setImage(new Image(avatar));
                            } else {
                                imgProfile.setImage(new Image(String.valueOf(getClass().getResource("/images/user_placeholder.png"))));
                            }
                        });
                    }, "Failed to fetch user"
            ));

            getAppointments();
        }
    }

    private void changeButtonsRecord(Boolean recordExists){
        if(recordExists){
            int indexRecordText = vBoxContainer.getChildren().indexOf(txtRecord);
            vBoxContainer.getChildren().remove(indexRecordText);
            vBoxContainer.getChildren().add(indexRecordText, lblRecord);
            btnCreateRecord.setDisable(true);
            btnAddAppointment.setDisable(false);
        } else {
            txtRecord.setPromptText("Insert the medical record");
            txtRecord.setStyle("-fx-font-size: 18px; -fx-text-fill: #3498db;");

            int indexRecordText = vBoxContainer.getChildren().indexOf(lblRecord);
            vBoxContainer.getChildren().remove(indexRecordText);
            vBoxContainer.getChildren().add(indexRecordText, txtRecord);
            btnAddAppointment.setDisable(true);
            btnCreateRecord.setDisable(false);
        }
    }

    private void getAppointments(){
        ServiceUtils.makePetition(new GenericPetition<>(
                "records", patient.getId()+ "/patient", "GET", null, RecordListResponse.class,
                recordListResponse -> {
                    Platform.runLater(() -> {
                        if(recordListResponse.getRecords() == null){
                            changeButtonsRecord(false);
                        } else {
                            Record record = recordListResponse.getRecords().getFirst();
                            lblRecord.setText(record.getMedicalRecord());
                            if (!record.getAppointments().isEmpty()) {
                                lvAppointments.getItems().addAll(record.getAppointments());
                            }
                        }
                    });
                }, "Failed to fetch appointments"
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

    public void onAddAppointment(MouseEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/appointment-detail-view.fxml"));
        Parent root = null;
        try{
            root = loader.load();
        }catch(IOException e){
            showAlert("ERROR", e.getMessage(), 2);
        }

        AppointmentDetailViewController controller = loader.getController();
        controller.setPatient(patient);

        Node source = (Node) mouseEvent.getSource();
        String title = "New Appointment| PhysioCare";
        Utils.switchView(source, root, title);
    }

    public void onChangePhotoClick(ActionEvent actionEvent) {
    }

    public void onCreateRecord(ActionEvent actionEvent) {
        if(!txtRecord.getText().isEmpty()){
            String jsonRequest = gson.toJson(new RecordRequest(patient.getId(), txtRecord.getText()));
            ServiceUtils.makePetition(new GenericPetition<>(
                    "records", "", "POST", jsonRequest, RecordResponse.class,
                    recordListResponse -> {
                        Platform.runLater(() -> {
                            changeButtonsRecord(true);
                            Record record = recordListResponse.getRecord();
                            lblRecord.setText(record.getMedicalRecord());
                        });
                    }, "Failed to fetch appointments"
            ));
        } else {
            Utils.showAlert("Warning", "Please, fill the medical record to create a new Record", 2);
        }
    }
}
