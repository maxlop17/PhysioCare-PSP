package org.example.emailprojectjavafx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.Appointment.AppointmentResponse;
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Patient.PatientListResponse;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Physio.PhysioListResponse;
import org.example.emailprojectjavafx.models.Record.Record;
import org.example.emailprojectjavafx.models.Record.RecordListResponse;
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
import java.util.concurrent.atomic.AtomicReference;

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
    private VBox statusContainer;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
    @FXML
    private TextField txtPrice;


    private Appointment appointment;
    Gson gson = new Gson();
    private Patient patient = null;
    private Physio physio = null;

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
        if (appointment == null) {
            appointment = new Appointment();
            fillData();
        }
    }


    private void fillData() {
        if (appointment != null) {
            btnAdd.setVisible(false);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            if (appointment.getDate() != null) {
                dpDate.getEditor().setText(formatter.format(appointment.getDate()));
            } else {
                dpDate.getEditor().setText("");
            }

            txtDiagnosis.setText(appointment.getDiagnosis());
            txtTreatment.setText(appointment.getTreatment());
            txtObservations.setText(appointment.getObservations());
            if (appointment.getPrice() != null) {
                txtPrice.setText(String.valueOf(appointment.getPrice()));
            } else {
                txtPrice.setText("0");
            }


            if (appointment.getId() == null) {
                statusContainer.setVisible(false);
                btnUpdate.setVisible(false);
                btnDelete.setVisible(false);
            } else {
                statusContainer.setVisible(true);
                btnUpdate.setVisible(true);
                btnDelete.setVisible(true);
                if (appointment.getConfirmed() != null) {
                    lblConfirmationStatus.setText(appointment.getConfirmed() ? "Appointment confirmed" : "Pending verification");
                }
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
        String patientId = patient.getId();
        AtomicReference<String> url = new AtomicReference<>(patientId + "/patient");

        ServiceUtils.makePetition(new GenericPetition<>(
                "records", url.get(), "GET", null, RecordListResponse.class,
                recordResponse -> {
                    Record record = recordResponse.getRecords().getFirst();
                    if (record != null) {
                        Platform.runLater(() -> {
                            Appointment newAppointment = getValidatedDataFromForm();
                            if (newAppointment != null) {

                                JsonObject wrapper = new JsonObject();
                                wrapper.add("appointments", gson.toJsonTree(newAppointment));
                                String jsonRequest = gson.toJson(wrapper);
                                url.set(record.getId() + "/appointments");
                                System.out.println("JSON enviado para añadir appointment: " + jsonRequest);

                                ServiceUtils.makePetition(new GenericPetition<>(
                                        "records", url.get(), "POST", jsonRequest, AppointmentResponse.class,
                                        appointmentResponse -> {
                                            Record updatedRecord = appointmentResponse.getResultado();
                                            if (updatedRecord != null && updatedRecord.getAppointments() != null && !updatedRecord.getAppointments().isEmpty()) {

                                                Appointment addedAppointment = updatedRecord.getAppointments().get(updatedRecord.getAppointments().size() - 1);
                                                Platform.runLater(() -> {
                                                    clearFields();
                                                    showAlert("Appointment added!", "Appointment added", 1);
                                                });
                                            } else {
                                                Platform.runLater(() -> {
                                                    showAlert("Error", "Failed to retrieve added appointment", 2);
                                                });
                                            }
                                        }, "Failed to add appointment"
                                ));
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            showAlert("Error", "Record not found", 2);
                        });
                    }
                }, "Failed to fetch record"
        ));
    }


    private Appointment getValidatedDataFromForm() {
        String diagnosis = txtDiagnosis.getText();
        String treatment = txtTreatment.getText();
        String observations = txtObservations.getText();
        Date date = toDate(dpDate.getEditor().getText());
        Boolean confirmed = lblConfirmationStatus.equals("Appointment confirmed");
        Physio physio = cbPhysio.getSelectionModel().getSelectedItem();
        Integer price = txtPrice.getText().isEmpty() ? 0 : Integer.parseInt(txtPrice.getText());

        if (diagnosis.isEmpty() || treatment.isEmpty() || observations.isEmpty() ||
                Objects.requireNonNull(date).before(new Date())) {
            showAlert("Error", "Please fill all the fields correctly.", 2);
            return null;
        }
        return new Appointment(date, physio.getId(), diagnosis, treatment, observations, confirmed, price);
    }

    /**
     * Method that gets a date in a string format and transforms it to date
     *
     * @param date the date that needs to be transformed
     * @return the date in Date format
     */
    private Date toDate(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            return formatter.parse(date);
        } catch (ParseException e) {
            showAlert("Error", e.getMessage(), 2);
        }
        return null;
    }

    public void onUpdateAppointment(ActionEvent actionEvent) {
        //1. obtener el record al que pertenece este appointment
        ServiceUtils.makePetition(new GenericPetition<>(
                "records", "/appointments/" + appointment.getId() + "/record", "GET", null, RecordResponse.class,
                recordResponse -> {
                    Platform.runLater(() -> {
                        System.out.println("Record: " + recordResponse.getRecord().getId());
                    });
                }, "Failed to fetch record"
        ));
    }


    public void onDeleteAppointment(ActionEvent actionEvent) {
        if (appointment.getId() != null) {
            String url = appointment.getId() + "/record";
            ServiceUtils.makePetition(new GenericPetition<>(
                    "records", url, "GET", null, RecordResponse.class,
                    recordResponse -> {
                        Record record = recordResponse.getRecord();
                        Platform.runLater(() -> {
                            if (record != null) {
                                System.out.println("Record: " + record.getId());
                            }
                        });
                    },
                    "Failed to fetch record"
            ));
        } else {
            //showAlert("ERROR", "There is no appointment or patient to delete", 2);
            System.out.println("ERROR: There is no appointment or patient to delete");
        }
    }


    private void changeWindow(ActionEvent actionEvent) {
        try {
            Parent root;
            String title;
            Node source = (Node) actionEvent.getSource();
            System.out.println();
            if (patient != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/patient-profile-view.fxml"));
                root = loader.load();
                PatientProfileViewController controller = loader.getController();
                controller.setPatient(patient);
                title = "Patient | PhysioCare ";
            } else if (physio != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/physio-profile-view.fxml"));
                root = loader.load();
                PhysioProfileViewController controller = loader.getController();
                controller.setPhysio(physio);
                title = "Physio | PhysioCare";
            } else {
                Utils.showAlert("Error", "No profile context available", 2);
                return;
            }
            Utils.switchView(source, root, title);
        } catch (IOException e) {
            Utils.showAlert("Error", "Error getting the profile", 2);
        }
    }

    private void getPhysios() {
        String url = ServiceUtils.SERVER + "/physios";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json ->
                        gson.fromJson(json, PhysioListResponse.class)
                ).thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            cbPhysio.getItems().addAll(response.getPhysios());
                            if (appointment.getPhysio() != null) {
                                for (Physio p : response.getPhysios()) {
                                    if (p.getId().equals(appointment.getPhysio())) {
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

    private void clearFields() {
        txtDiagnosis.clear();
        txtTreatment.clear();
        txtObservations.clear();
        dpDate.getEditor().clear();
        cbPhysio.getSelectionModel().clearSelection();
        txtPrice.clear();

    }

}
