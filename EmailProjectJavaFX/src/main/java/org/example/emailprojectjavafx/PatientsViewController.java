package org.example.emailprojectjavafx;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Appointment.AppointmentResponse;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Patient.PatientListResponse;
import org.example.emailprojectjavafx.models.Patient.PatientResponse;
import org.example.emailprojectjavafx.models.Physio.PhysioResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;

public class PatientsViewController implements Initializable {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSurname;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtInsuranceNumber;
    @FXML
    private TextField txtEmail;


    @FXML
    private ListView<Patient> lsPatients;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;


    Gson gson = new Gson();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lsPatients.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Patient>() {
                    @Override
                    public void changed(ObservableValue<? extends Patient> observableValue, Patient t1, Patient t2) {
                        if (t2 != null) {
                            txtName.setText(t2.getName());
                            txtSurname.setText(t2.getSurname());
                            txtAddress.setText(t2.getAddress());
                            txtInsuranceNumber.setText(t2.getInsuranceNumber());
                            txtEmail.setText(t2.getEmail());
                            LocalDate localDate = t2.getBirthDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            dpBirthDate.setValue(localDate);
                        } else {
                            txtName.setText("");
                            txtSurname.setText("");
                            txtAddress.setText("");
                            txtInsuranceNumber.setText("");
                            txtEmail.setText("");
                            dpBirthDate.setValue(null);
                        }
                    }
                });
        getPatients();
        getPhysioById();
        addAppointmentToRecord();
    }

    private void getPatients() {
        String url = ServiceUtils.SERVER + "/patients";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json ->
                        gson.fromJson(json, PatientListResponse.class)
                ).thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() ->
                                lsPatients.getItems().setAll(response.getPatients())
                        );
                    } else {
                        showAlert("Error", response.getErrorMessage(), 2);
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to fetch patients", 2);
                    return null;
                });
    }

    /*-----------------------------------------------------------------------------------------*/

    public void addPatientAction() {
        Patient selectedPatient = lsPatients.getSelectionModel().getSelectedItem();

        if (selectedPatient != null) {
            showAlert("Warning", "To add a new patient, please deselect the selected patient from the list or press the 'Clear Fields' button.", 2);
        } else {
            Patient newPatient = getValidatedDataFromForm();
            if (newPatient != null) {
                postPatient(newPatient);
            }
        }
    }

    public void updatePatientAction() {
        Patient selectedPatient = lsPatients.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert("Error", "Select a patient to update.", 2);
        } else {
            Patient updatedPatient = getValidatedDataFromForm();
            if (updatedPatient != null) {
                updatedPatient.setId(selectedPatient.getId());
                modifyPatient(updatedPatient);
            }
        }
    }

    public void deletePatientAction() {
        Patient selectedPatient = lsPatients.getSelectionModel().getSelectedItem();

        //Si no hay nada seleccionado mostramos un alert
        if (selectedPatient == null) {
            showAlert("ERROR", "Select a patient", 2);
        } else {
            deletePatient(selectedPatient);
        }
    }

    public void clearFieldsAction() {
        clearFields();
    }

    /*-----------------------------------------------------------------------------------------*/

    private void postPatient(Patient patient) {
        btnAdd.setDisable(true);
        String url = ServiceUtils.SERVER + "/patients";
        String jsonRequest = gson.toJson(patient);

        ServiceUtils.getResponseAsync(url, jsonRequest, "POST")
                .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            showAlert("Added patient", response.getPatient().getName() + " added", 1);
                            getPatients();
                            clearFields();
                            btnAdd.setDisable(false);
                        });
                    } else {
                        Platform.runLater(() ->
                                showAlert("Error creating patient", response.getErrorMessage(), 2)
                        );
                    }
                })
                .exceptionally(_ -> {
                    showAlert("Error", "Failed to add patient", 2);
                    return null;
                });
    }

    private void modifyPatient(Patient patient) {
        btnUpdate.setDisable(true);
        String url = ServiceUtils.SERVER + "/patients/" + patient.getId();
        String jsonRequest = gson.toJson(patient);

        ServiceUtils.getResponseAsync(url, jsonRequest, "PUT")
                .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            showAlert("Updated patient", response.getPatient().getName() + " updated", 1);
                            getPatients();
                            btnUpdate.setDisable(false);
                            clearFields();
                        });
                    } else {
                        Platform.runLater(() ->
                                showAlert("Error modifying patient", response.getErrorMessage(), 2)
                        );
                    }

                })
                .exceptionally(_ -> {
                    showAlert("Error", "Failed to update patient", 2);
                    return null;
                });
    }

    private void deletePatient(Patient patient) {
        btnDelete.setDisable(true);
        String url = ServiceUtils.SERVER + "/patients/" + patient.getId();
        String jsonRequest = "";

        ServiceUtils.getResponseAsync(url, jsonRequest, "DELETE")
                .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            showAlert("Deleted Patient", response.getPatient().getName() + " deleted", 1);
                            getPatients();
                            btnDelete.setDisable(false);
                            clearFields();
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error deleting patient", response.getErrorMessage(), 2));
                    }
                })
                .exceptionally(_ -> {
                    showAlert("Error", "Failed to delete patient", 2);
                    return null;
                });
    }

    /*-----------------------------------------------------------------------------------------*/

    private void clearFields() {
        txtName.clear();
        txtSurname.clear();
        txtAddress.clear();
        txtInsuranceNumber.clear();
        txtEmail.clear();
        dpBirthDate.setValue(null);
        lsPatients.getSelectionModel().clearSelection();
    }

    private Patient getValidatedDataFromForm() {


        String patientName = txtName.getText();
        String surname = txtSurname.getText();
        String address = txtAddress.getText();
        String insuranceNumber = txtInsuranceNumber.getText();
        String email = txtEmail.getText();
        LocalDate localDate = dpBirthDate.getValue();

        if (patientName.isEmpty() || surname.isEmpty() || address.isEmpty()
                || insuranceNumber.isEmpty() || email.isEmpty() || localDate == null) {
            showAlert("Error", "Please fill all the fields.", 2);
            return null;
        }
        Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return new Patient(patientName, surname, birthDate, address, insuranceNumber, email);
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/first-view.fxml";
        String title = "Welcome | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    /*--------------------------------------------CORRECCION----------------------------------------------------*/

    private void getPhysioById() {
        String id = "675d61387a2489bffe8ee05d";

        String url = ServiceUtils.SERVER + "/physios/" + id;
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json ->
                        gson.fromJson(json, PhysioResponse.class)
                ).thenAccept(response -> {
                    if (!response.isError()) {
                        System.out.println("Physio: " + response.getPhysio());
                    } else {
                        System.out.println("Error, no se ha encontrado el physio: " + response.getErrorMessage());
                    }
                }).exceptionally(_ -> {
                    System.out.println("Error, no se ha encontrado el physio ");
                    return null;
                });
    }

    private void addAppointmentToRecord() {
        Date date = new Date();

        Appointment appointment = new Appointment(date, "675d61387a2489bffe8ee05d", "diagnostico", "tratamiento", "observations");
        String url = ServiceUtils.SERVER + "/records/67e96e551387f9405d92eaf7/appointments";

        System.out.println("Appointment a añadir: " + appointment);
        String jsonRequest = gson.toJson(appointment);

        ServiceUtils.getResponseAsync(url, jsonRequest, "POST")
                .thenApply(json -> gson.fromJson(json, AppointmentResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        System.out.println("Appointment: " + response.getAppointment());
                    } else {
                        System.out.println("Error, no se ha podido insertar el appointment: " + response.getErrorMessage());
                    }
                })
                .exceptionally(_ -> {
                    System.out.println("Error, no se ha podido insertar el appointment");
                    return null;
                });
    }
}
























