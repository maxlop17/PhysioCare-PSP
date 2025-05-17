package org.example.emailprojectjavafx;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Patient.PatientResponse;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Physio.PhysioResponse;
import org.example.emailprojectjavafx.models.User;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;
import org.example.emailprojectjavafx.utils.services.TokenUtils;

import java.net.URL;
import java.util.ResourceBundle;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;

public class UserProfileViewController implements Initializable {
    @FXML
    public ImageView imgAvatar;
    @FXML
    public Label lblUsername;
    @FXML
    public Label lblRole;
    @FXML
    public Label lblLastLogin;
    @FXML
    public Label lblEmail;
    private User user;
    private Patient patient;
    private Physio physio;
    private Gson gson = new Gson();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user = TokenUtils.getUserFromToken();
        lblUsername.setText(user.getUsername());
        if(user.getRole().equals("physio")){
            String urlPhysio = ServiceUtils.SERVER + "/physios/" + user.getId();
            ServiceUtils.getResponseAsync(urlPhysio, null, "GET")
                    .thenApply(json -> gson.fromJson(json, PhysioResponse.class)
                    ).thenAccept(response -> {
                        if (response.isOk()) {
                            physio = response.getPhysio();
                        } else {
                            showAlert("Error", response.getError(), 2);
                        }
                    }).exceptionally(_ -> {
                        showAlert("Error", "Failed to fetch physio", 2);
                        return null;
                    });

        } else {
            String urlPatient = ServiceUtils.SERVER + "/patients/" + user.getId();
            ServiceUtils.getResponseAsync(urlPatient, null, "GET")
                    .thenApply(json -> gson.fromJson(json, PatientResponse.class)
                    ).thenAccept(response -> {
                        if (response.isOk()) {
                            patient = response.getPatient();
                        } else {
                            showAlert("Error", response.getError(), 2);
                        }
                    }).exceptionally(_ -> {
                        showAlert("Error", "Failed to fetch patient", 2);
                        return null;
                    });

            lblEmail.setText(patient.getEmail());
            //imgAvatar.setImage();
        }

        lblUsername.setText(user.getUsername());
        lblRole.setText(user.getRole());
    }

    public void onLogoutClick(ActionEvent actionEvent) {
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/first-view.fxml";
        String title = "Welcome | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void onChangeAvatarClick(ActionEvent actionEvent) {
    }

}
