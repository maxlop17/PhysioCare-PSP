package org.example.emailprojectjavafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import com.google.gson.Gson;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Appointment.AppointmentListResponse;
import org.example.emailprojectjavafx.models.Appointment.AppointmentResponse;
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Record.Record;
import org.example.emailprojectjavafx.models.Physio.PhysioResponse;
import org.example.emailprojectjavafx.models.Record.RecordResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.io.IOException;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;

public class PhysioProfileViewController {


    @FXML
    private ListView<Appointment> lstAppointments;
    @FXML
    private Label lblName;
    @FXML
    private Label lblSurname;
    @FXML
    private Label lblSpecialty;
    @FXML
    private Label lblLicenseNumber;
    @FXML
    private Label lblEmail;
    private Record record = null;
    Gson gson = new Gson();
    private Physio currentPhysio;

    public void setPhysio(Physio physio) {
        this.currentPhysio = physio;
        loadPhysioInfo();
        getAppointments();
    }

    private void loadPhysioInfo() {
        lblName.setText(currentPhysio.getName());
        lblSurname.setText(currentPhysio.getSurname());
        lblSpecialty.setText(currentPhysio.getSpecialty());
        lblLicenseNumber.setText(currentPhysio.getLicenseNumber());
        lblEmail.setText(currentPhysio.getEmail());
    }

    public void onVerifyAppointments(ActionEvent actionEvent) {
        Appointment selectedAppointment = lstAppointments.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert("ERROR", "Select an appointment to continue", 2);
        } else {
            if (selectedAppointment.getConfirmed()) {
                showAlert("ERROR", "Appointment is already confirmed", 2);
            } else {
                selectedAppointment.setConfirmed(true);
                modifyAppointment(selectedAppointment);
                showAlert("APPOINTMENT CONFIRMED", "Appointment confirmed successfully", 1);
                lstAppointments.refresh();

            }
        }
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/physios-view.fxml";
        String title = "Physios | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void onMouseClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && (mouseEvent.getClickCount() == 2)) {
            Appointment selectedAppointment = lstAppointments.getSelectionModel().getSelectedItem();

            //Si no hay nada seleccionado mostramos un alert
            if (selectedAppointment == null) {
                showAlert("ERROR", "Select an appointment", 2);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/appointment-detail-view.fxml"));
                Parent root = loader.load();
                // Obtener el controlador y pasarle el objeto
                AppointmentDetailViewController controller = loader.getController();
                controller.setAppointment(selectedAppointment);

                Node source = (Node) mouseEvent.getSource();
                String title =  "Appointment | PhysioCare";
                Utils.switchView(source, root, title);
            }
        }
    }

    private void getAppointments() {
        ServiceUtils.makePetition(new GenericPetition<>(
                "records", "/appointments/" + currentPhysio.getId() + "/physio",
                "GET", null, AppointmentListResponse.class,
                appointmentListResponse -> {
                    Platform.runLater(() -> {
                        lstAppointments.getItems().setAll(appointmentListResponse.getAppointments());

                        // Estilizado por estado de confirmación
                        lstAppointments.setCellFactory(lv -> new ListCell<Appointment>() {
                            @Override
                            protected void updateItem(Appointment item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                    setStyle("");
                                } else {
                                    setText(item.getDate() + "");
                                    if (item.getConfirmed()) {
                                        setStyle("-fx-background-color: #94c973; -fx-text-fill: #ffffff;");
                                    } else {
                                        setStyle("-fx-background-color: #c85250; -fx-text-fill: #ffffff;");
                                    }
                                }
                            }
                        });
                    });
                }, "Failed to fetch appointments"
        ));
    }

    private void modifyAppointment(Appointment appointment) {
        getRecord(appointment.getId());
        ServiceUtils.makePetition(new GenericPetition<>(
                "records", record.getId() + "/appointments/" + appointment.getId(),
                "PUT", gson.toJson(appointment), AppointmentResponse.class,
                appointmentListResponse -> {
                    Platform.runLater(() ->
                            showAlert("Appointment updated", appointmentListResponse.getError(), 1));
                }, "Failed to fetch appointments"
        ));
    }

    private void getRecord(String id){
        ServiceUtils.makePetition(new GenericPetition<>(
                "records", "/appointments/" + id + "/record",
                "GET", null, RecordResponse.class,
                recordResponse -> {
                    record = recordResponse.getRecord();
                }, "Failed to fetch record"
        ));
    }

}
