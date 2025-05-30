package org.example.emailprojectjavafx;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Physio.*;
import org.example.emailprojectjavafx.models.User.UserResponse;
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
    private TextField txtLogin;
    @FXML
    private PasswordField txtPassword;

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
                            txtLicenseNumber.setText("");
                            txtEmail.setText("");
                            cbSpecialization.setValue(null);
                        }
                    }
                });
        getPhysios();
    }

    private void getPhysios() {
        ServiceUtils.makePetition(new GenericPetition<>(
                "physios", "",
                "GET", null, PhysioListResponse.class,
                physioListResponse -> {
                    Platform.runLater(() ->
                            lsPhysios.getItems().setAll(physioListResponse.getPhysios())
                    );
                }, "Failed to fetch physios"
        ));
    }

    public void onAddPhysio() {
        Physio selectedPhysio = lsPhysios.getSelectionModel().getSelectedItem();

        if (selectedPhysio != null) {
            showAlert("Warning", "To add a new physio, please deselect the selected physio from the list or press the 'Clear Fields' button.", 2);
        } else {
            Physio newPhysio = getValidatedDataFromForm(false);
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
            Physio updatedPhysio = getValidatedDataFromForm(true);
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
        PhysioRequest pr = new PhysioRequest(physio, txtLogin.getText(), txtPassword.getText());
        String jsonRequest = gson.toJson(pr);
        System.out.println(jsonRequest);
        ServiceUtils.makePetition(new GenericPetition<>(
                "physios", "", "POST", jsonRequest, PhysioResponse.class,
                physioResponse -> {
                    System.out.println(physioResponse.getPhysio());
                    Platform.runLater(() -> {
                        showAlert("Physio added", "Physio added successfully", 1);
                        getPhysios();
                        clearFields();
                    });
                }, "Failed to add physio"
        ));
    }

    private void modifyPhysio(Physio physio) {
        btnUpdate.setDisable(true);
        UpdatePhysio up = new UpdatePhysio(physio, txtLogin.getText(), txtPassword.getText());
        String jsonRequest = gson.toJson(up);

        ServiceUtils.makePetition(new GenericPetition<>(
                "physios", physio.getId(),
                "PUT", jsonRequest, PhysioResponse.class,
                physioResponse -> {
                    Platform.runLater(() -> {
                        showAlert("Physio updated", physioResponse.getPhysio().getName() + " updated", 1);
                        getPhysios();
                        clearFields();
                    });
                }, "Failed to modifying physio"
        ));
    }

    private void deletePhysio(Physio physio) {
        btnDelete.setDisable(true);
        ServiceUtils.makePetition(new GenericPetition<>(
                "physios", physio.getId(),
                "DELETE", null, PhysioResponse.class,
                physioResponse -> {
                    Platform.runLater(() -> {
                        showAlert("Physio deleted", physioResponse.getPhysio().getName() + " deleted", 1);
                        getPhysios();
                        btnDelete.setDisable(false);
                        clearFields();
                    });
                }, "Failed to delete physio"
        ));
    }

    /*----------------------------------------------------------------------------------------------*/
    private void clearFields() {
        txtName.clear();
        txtSurname.clear();
        txtLicenseNumber.clear();
        txtEmail.clear();
        txtLogin.clear();
        txtPassword.clear();
        cbSpecialization.setValue(null);
        lsPhysios.getSelectionModel().clearSelection();
    }

    private Physio getValidatedDataFromForm(Boolean modify) {

        String name = txtName.getText();
        String surname = txtSurname.getText();
        String licenseNumber = txtLicenseNumber.getText();
        String email = txtEmail.getText();
        String specialty = cbSpecialization.getValue();
        String login = txtLogin.getText();
        String password = txtPassword.getText();


        if (name.isEmpty() || surname.isEmpty() || licenseNumber.isEmpty() || email.isEmpty()
                || specialty == null || specialty.isEmpty()) {
            showAlert("Error", "Please fill all the fields.", 2);
            return null;
        }

        if(!licenseNumber.matches("^[a-zA-Z0-9]{8}$")){
            showAlert("Error", "The license number must have 8 numbers and/or letters.", 2);
            return null;
        }

        if (!modify && (login.isEmpty() || password.isEmpty())) {
            showAlert("Error", "Login and password are required for new physios.", 2);
            return null;
        }


        return new Physio(name, surname, licenseNumber, specialty.toLowerCase(), email);
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
