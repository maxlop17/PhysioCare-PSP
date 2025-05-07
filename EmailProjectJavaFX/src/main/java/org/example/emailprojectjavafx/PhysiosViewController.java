package org.example.emailprojectjavafx;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Physio.PhysioListResponse;
import org.example.emailprojectjavafx.models.Physio.PhysioResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;

public class PhysiosViewController implements Initializable {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSurname;
    @FXML
    private TextField txtLicenseNumber;
    @FXML
    private TextField txtEmail;


    @FXML
    private ListView<Physio> lsPhysios;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private ComboBox<String> cbSpecialization;

    Gson gson = new Gson();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbSpecialization.getItems().addAll("Sports", "Neurological", "Pediatric", "Geriatric", "Oncological");

        lsPhysios.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Physio>() {
                    @Override
                    public void changed(ObservableValue<? extends Physio> observableValue, Physio t1, Physio t2) {
                        if (t2 != null) {
                            txtName.setText(t2.getName());
                            txtSurname.setText(t2.getSurname());
                            txtLicenseNumber.setText(t2.getLicenseNumber());
                            txtEmail.setText(t2.getEmail());
                            cbSpecialization.setValue(t2.getSpecialty());

                        } else {
                            txtName.setText("");
                            txtSurname.setText("");
                            txtLicenseNumber.setText("");
                            txtEmail.setText("");
                            cbSpecialization.setValue(null);
                        }
                    }
                });
        getPhysios();
    }

    private void getPhysios() {
        String url = ServiceUtils.SERVER + "/physios";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json ->
                        gson.fromJson(json, PhysioListResponse.class)
                ).thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() ->
                                lsPhysios.getItems().setAll(response.getPhysios())
                        );
                    } else {
                        showAlert("Error", response.getError(), 2);
                    }
                }).exceptionally(_ -> {
                    showAlert("Error", "Failed to fetch physios", 2);
                    return null;
                });
    }

    public void onAddPhysio() {
        Physio selectedPhysio = lsPhysios.getSelectionModel().getSelectedItem();

        if (selectedPhysio != null) {
            showAlert("Warning", "To add a new physio, please deselect the selected physio from the list or press the 'Clear Fields' button.", 2);
        } else {
            Physio newPhysio = getValidatedDataFromForm();
            if (newPhysio != null) {
                postPhysio(newPhysio);
            }
        }
    }

    public void onUpdatePhysio() {
        Physio selectedPhysio = lsPhysios.getSelectionModel().getSelectedItem();
        if (selectedPhysio == null) {
            showAlert("Error", "Select a physio to update.", 2);
        } else {
            Physio updatedPhysio = getValidatedDataFromForm();
            if (updatedPhysio != null) {
                updatedPhysio.setId(selectedPhysio.getId());
                modifyPhysio(updatedPhysio);
            }
        }
    }

    public void onDeletePhysio() {
        Physio selectedPhysio = lsPhysios.getSelectionModel().getSelectedItem();

        //Si no hay nada seleccionado mostramos un alert
        if (selectedPhysio == null) {
            showAlert("ERROR", "Select a physio", 2);
        } else {
            deletePhysio(selectedPhysio);
        }
    }

    public void clearFieldsAction() {
        clearFields();
    }

    /*----------------------------------------------------------------------------------------------*/

    private void postPhysio(Physio physio) {
        btnAdd.setDisable(true);
        String url = ServiceUtils.SERVER + "/physios";
        String jsonRequest = gson.toJson(physio);

        ServiceUtils.getResponseAsync(url, jsonRequest, "POST")
                .thenApply(json -> gson.fromJson(json, PhysioResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            showAlert("Physio added", response.getPhysio().getName() + " added", 1);
                            getPhysios();
                            clearFields();
                            btnAdd.setDisable(false);
                        });
                    } else {
                        Platform.runLater(() ->
                                showAlert("Error creating physio", response.getError(), 2)
                        );
                    }
                })
                .exceptionally(_ -> {
                    showAlert("Error", "Failed to add physio", 2);
                    return null;
                });
    }

    private void modifyPhysio(Physio physio) {
        btnUpdate.setDisable(true);
        String url = ServiceUtils.SERVER + "/physios/" + physio.getId();
        String jsonRequest = gson.toJson(physio);

        ServiceUtils.getResponseAsync(url, jsonRequest, "PUT")
                .thenApply(json -> gson.fromJson(json, PhysioResponse.class))
                .thenAccept(response -> {
                    if (!response.isOk()) {
                        Platform.runLater(() -> {
                            showAlert("Physio updated", response.getPhysio().getName() + " updated", 1);
                            getPhysios();
                            btnUpdate.setDisable(false);
                            clearFields();
                        });
                    } else {
                        Platform.runLater(() ->
                                showAlert("Error modifying physio", response.getError(), 2)
                        );
                    }

                })
                .exceptionally(_ -> {
                    showAlert("Error", "Failed to update physio", 2);
                    return null;
                });
    }

    private void deletePhysio(Physio physio) {
        btnDelete.setDisable(true);
        String url = ServiceUtils.SERVER + "/physios/" + physio.getId();
        String jsonRequest = "";

        ServiceUtils.getResponseAsync(url, jsonRequest, "DELETE")
                .thenApply(json -> gson.fromJson(json, PhysioResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            showAlert("Physio deleted", response.getPhysio().getName() + " deleted", 1);
                            getPhysios();
                            btnDelete.setDisable(false);
                            clearFields();
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error deleting physio", response.getError(), 2));
                    }
                })
                .exceptionally(_ -> {
                    showAlert("Error", "Failed to delete physio", 2);
                    return null;
                });
    }

    /*----------------------------------------------------------------------------------------------*/
    private void clearFields() {
        txtName.clear();
        txtSurname.clear();
        txtLicenseNumber.clear();
        txtEmail.clear();
        cbSpecialization.setValue(null);
        lsPhysios.getSelectionModel().clearSelection();
    }

    private Physio getValidatedDataFromForm() {

        String name = txtName.getText();
        String surname = txtSurname.getText();
        String licenseNumber = txtLicenseNumber.getText();
        String email = txtEmail.getText();
        String specialty = cbSpecialization.getValue();

        if (name.isEmpty() || surname.isEmpty() || licenseNumber.isEmpty() || email.isEmpty() || specialty.isEmpty()) {
            showAlert("Error", "Please fill all the fields.", 2);
            return null;
        }
        return new Physio(name, surname, licenseNumber, specialty, email);
    }


    public void onBackButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/first-view.fxml";
        String title = "Welcome | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void onMouseClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && (mouseEvent.getClickCount() == 2)) {
            Physio selectedPhysio = lsPhysios.getSelectionModel().getSelectedItem();

            //Si no hay nada seleccionado mostramos un alert
            if (selectedPhysio == null) {
                showAlert("ERROR", "Select a physio", 2);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/physio-profile-view.fxml"));
                Parent root = loader.load();
                // Obtener el controlador y pasarle el objeto
                PhysioProfileViewController controller = loader.getController();
                controller.setPhysio(selectedPhysio);

                Node source = (Node) mouseEvent.getSource();
                String title = selectedPhysio.getName() + " | PhysioCare";
                Utils.switchView(source, root, title);
            }
        }
    }
}
