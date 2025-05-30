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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.emailprojectjavafx.models.Appointment.AppointmentResponse;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Record.Record;
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Physio.PhysioListResponse;
import org.example.emailprojectjavafx.models.Physio.PhysioResponse;
import org.example.emailprojectjavafx.models.Record.RecordResponse;
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
    @FXML
    public Spinner<Double> numPrice = new Spinner<>();
    @FXML
    public Label lblPhysioPatient;
    @FXML
    public GridPane gridPane;

    private Appointment appointment;
    private Gson gson = new Gson();
    private Patient patient = null;
    private Physio physio = null;
    private Record record;
    private static final String CONFIRMED = "Appointment confirmed";
    private static final String PENDING = "Pending verification";

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        fillData();
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        this.physio = null;
    }

    public void setPhysio(Physio physio) {
        this.physio = physio;
        this.patient = null;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numPrice.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000.0, 0.0, 1.0));
        if(appointment == null){
            appointment = new Appointment();
        } else {
            fillData();
        }
        getPhysios();
    }

    private void fillData(){
        if(appointment != null){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            dpDate.getEditor().setText(formatter.format(appointment.getDate()));
            txtDiagnosis.setText(appointment.getDiagnosis());
            txtTreatment.setText(appointment.getTreatment());
            txtObservations.setText(appointment.getObservations());
            if(appointment.getPrice() != null) {
                numPrice.getEditor().setText(String.valueOf(appointment.getPrice()));
            } else {
                numPrice.getEditor().setText("0");
            }
            if(appointment.getConfirmed() != null){
                lblConfirmationStatus.setText(appointment.getConfirmed() ? CONFIRMED : PENDING);
            }
            getRecordByAppointmentId();
        }
    }

    public void onToggleConfirmation(ActionEvent actionEvent) {
        lblConfirmationStatus.setText(lblConfirmationStatus.equals(CONFIRMED) ? PENDING : CONFIRMED);
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        changeWindow(actionEvent);
    }

    public void onAddAppointment(ActionEvent actionEvent) {
        if(appointment.getId() == null || appointment.getId().isEmpty()){
            String jsonRequest = gson.toJson(getValidatedDataFromForm(false));
            ServiceUtils.makePetition(new GenericPetition<>(
                    "records", record.getId() + "/appointments",
                    "POST", jsonRequest, RecordResponse.class,
                    recordResponse -> {
                        Platform.runLater(() -> {
                            showAlert("Appointment created", "Appointment updated", 1);
                        });
                        onBackButtonClick(actionEvent);
                    }, "Failed to create appointment"
            ));
        } else {
            showAlert("Error", "The appointment already exists.", 2);
        }
    }

    private Appointment getValidatedDataFromForm(Boolean update) {
        String diagnosis = txtDiagnosis.getText();
        String treatment = txtTreatment.getText();
        String observations = txtObservations.getText();
        Date date = toDate(dpDate.getEditor().getText());
        Boolean confirmed = lblConfirmationStatus.equals("Appointment confirmed");
        Physio physio = cbPhysio.getSelectionModel().getSelectedItem();
        Double price = numPrice.getValue();

        if (diagnosis.isEmpty() || treatment.isEmpty() ||
                price.isNaN() || Objects.requireNonNull(date).before(new Date()) ||
                physio == null) {
            showAlert("Error", "Please fill all the fields correctly.", 2);
            return null;
        }
        if(update && observations.isEmpty()){
            observations = appointment.getObservations();
        }
        return new Appointment(date, physio.getId(), diagnosis, treatment, observations, confirmed, price);
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
        if(appointment.getId() != null) {
            String jsonRequest = gson.toJson(getValidatedDataFromForm(true));
            ServiceUtils.makePetition(new GenericPetition<>(
                    "records", record.getId() + "/appointments/" + appointment.getId(),
                    "PUT", jsonRequest, RecordResponse.class,
                    recordResponse -> {
                        Platform.runLater(() -> {
                            showAlert("Updated appointment", "Appointment updated", 1);
                        });
                    }, "Failed to fetch appointments"
            ));
        } else {
            showAlert("ERROR", "There is no appointment to update", 2);
        }
    }

    private void getRecordByAppointmentId(){
        ServiceUtils.makePetition(new GenericPetition<>(
                "records", "appointments/" + appointment.getId() + "/record",
                "GET", null, RecordResponse.class,
                recordResponse -> {
                    record = recordResponse.getRecord();
                }, "Failed to fetch record"
        ));
    }

    public void onDeleteAppointment(ActionEvent actionEvent) {
        if(appointment.getId() != null) {
            ServiceUtils.makePetition(new GenericPetition<>(
                    "records", record.getId() + "/appointments/" + appointment.getId(),
                    "DELETE", null, RecordResponse.class,
                    recordResponse -> {
                        Platform.runLater(() -> {
                            showAlert("Deleted Appointment", "Appointment deleted successfully", 1);
                        });
                    }, "Failed to fetch appointments"
            ));
        } else {
            showAlert("ERROR", "There is no appointment to delete", 2);
        }
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
        ServiceUtils.makePetition(new GenericPetition<>(
                "physios", "", "GET", null, PhysioListResponse.class,
                physioListResponse -> {
                    Platform.runLater(() -> {
                        cbPhysio.getItems().addAll(physioListResponse.getPhysios());
                        if(appointment.getPhysio() != null){
                            for(Physio p : physioListResponse.getPhysios()){
                                if(p.getId().equals(appointment.getPhysio())){
                                    cbPhysio.getSelectionModel().select(p);
                                }
                            }
                        }
                    });
                }, "Failed to fetch physios"
        ));
    }

    private void getPatients(){
        ServiceUtils.makePetition(new GenericPetition<>(
                "physios", "", "GET", null, PhysioListResponse.class,
                physioListResponse -> {
                    Platform.runLater(() -> {
                        cbPhysio.getItems().addAll(physioListResponse.getPhysios());
                        if(appointment.getPhysio() != null){
                            for(Physio p : physioListResponse.getPhysios()){
                                if(p.getId().equals(appointment.getPhysio())){
                                    cbPhysio.getSelectionModel().select(p);
                                }
                            }
                        }
                    });
                }, "Failed to fetch physios"
        ));
    }

}
