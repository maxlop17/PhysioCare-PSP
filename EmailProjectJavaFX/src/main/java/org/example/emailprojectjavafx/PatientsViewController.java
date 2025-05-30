package org.example.emailprojectjavafx;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Patient.*;
import org.example.emailprojectjavafx.models.User.UserResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;
import static org.example.emailprojectjavafx.utils.Utils.switchView;

public class PatientsViewController implements Initializable {

    @FXML
    public PasswordField txtPassword;
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
    private TextField txtLogin;


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
                            ServiceUtils.makePetition(new GenericPetition<>(
                                    "users", t2.getId(), "GET", null, UserResponse.class,
                                    userResponse -> {
                                        Platform.runLater(() -> {
                                            txtLogin.setText(userResponse.getUser().getLogin());
                                        });
                                    }, "Failed to fetch user"
                            ));
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

        lsPatients.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    if (lsPatients.getSelectionModel().getSelectedItem() != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/patient-profile-view.fxml"));
                            Parent root = loader.load();
                            PatientProfileViewController controller = loader.getController();
                            controller.setPatient(lsPatients.getSelectionModel().getSelectedItem());
                            switchView((Node) mouseEvent.getSource(), root, "Patient | PhysioCare");
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            Utils.showAlert("Error", "Error getting the profile", 2);
                        }
                    }
                }
            }
        });
        getPatients();
    }

    private void getPatients() {
        ServiceUtils.makePetition(new GenericPetition<>(
                "patients", "", "GET", null, PatientListResponse.class,
                patientListResponse -> {
                    Platform.runLater(() ->
                            lsPatients.getItems().setAll(patientListResponse.getPatients())
                    );
                }, "Failed to fetch patients"
        ));
    }

    /*-----------------------------------------------------------------------------------------*/

    public void addPatientAction() {
        Patient selectedPatient = lsPatients.getSelectionModel().getSelectedItem();

        if (selectedPatient != null) {
            showAlert("Warning", "To add a new patient, please deselect the selected patient from the list or press the 'Clear Fields' button.", 2);
        } else {
            Patient newPatient = getValidatedDataFromForm(false);
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
            Patient updatedPatient = getValidatedDataFromForm(true);
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
        PatientRequest pr = new PatientRequest(patient, txtLogin.getText(), txtPassword.getText());
        String jsonRequest = gson.toJson(pr);

        ServiceUtils.makePetition(new GenericPetition<>(
                "patients", "", "POST", jsonRequest, PatientResponse.class,
                patientResponse -> {
                    Platform.runLater(() -> {
                        showAlert("Added patient", patientResponse.getPatient().getName() + " added", 1);
                        getPatients();
                        clearFields();
                    });
                }, "Failed to add patient"
        ));
    }

    private void modifyPatient(Patient patient) {
        UpdatePatient up = new UpdatePatient(patient, txtLogin.getText(), txtPassword.getText());
        String jsonRequest = gson.toJson(up);

        ServiceUtils.makePetition(new GenericPetition<>(
                "patients", patient.getId(), "PUT", jsonRequest, PatientResponse.class,
                patientResponse -> {
                    Platform.runLater(() -> {
                        System.out.println("Patient updated." + patientResponse.getPatient());
                        showAlert("Updated patient", patientResponse.getPatient().getName() + " updated", 1);
                        getPatients();
                        clearFields();
                    });
                }, "Failed to update patient"
        ));
    }

    private void deletePatient(Patient patient) {
        ServiceUtils.makePetition(new GenericPetition<>(
                "patients", patient.getId(), "DELETE", null, PatientResponse.class,
                patientResponse -> {
                    Platform.runLater(() -> {
                        showAlert("Deleted Patient", patientResponse.getPatient().getName() + " deleted", 1);
                        getPatients();
                        clearFields();
                    });
                }, "Failed to delete patient"
        ));
    }

    /*-----------------------------------------------------------------------------------------*/

    private void clearFields() {
        txtName.clear();
        txtSurname.clear();
        txtAddress.clear();
        txtInsuranceNumber.clear();
        txtEmail.clear();
        txtLogin.clear();
        txtPassword.clear();
        dpBirthDate.setValue(null);
        lsPatients.getSelectionModel().clearSelection();
    }

    private Patient getValidatedDataFromForm(Boolean edit) {
        String patientName = txtName.getText();
        String surname = txtSurname.getText();
        String address = txtAddress.getText();
        String insuranceNumber = txtInsuranceNumber.getText();
        String email = txtEmail.getText();
        LocalDate localDate = dpBirthDate.getValue();
        String login = txtLogin.getText();
        String password = txtPassword.getText();

        if(localDate.isAfter(LocalDate.now())){
            showAlert("Error", "The date cannot be in the future.", 2);
            return null;
        }
        if (patientName.isEmpty() || surname.isEmpty() || address.isEmpty()
                || insuranceNumber.isEmpty() || email.isEmpty() || localDate == null ||
                login.isEmpty()) {
            showAlert("Error", "Please fill all the fields.", 2);
            return null;
        }
        if(!edit && password.isEmpty()){
            showAlert("Error", "The password cannot be empty.", 2);
        }
        if(!insuranceNumber.matches("^\\w{9}$")){
            showAlert("Error", "The insurance number must have 9 characters.", 2);
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

}
























