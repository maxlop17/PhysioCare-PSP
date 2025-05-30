package org.example.emailprojectjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.email.EmailSenderController;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;
import org.example.emailprojectjavafx.utils.services.TokenUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class FirstViewController implements Initializable {

    @FXML
    public ImageView imgAvatar;

    public void openPatientsAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/patients-view.fxml";
        String title = "Patients | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }


    public void openEmailAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/email-sender-view.fxml";
        String title = "Email Sender | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void openPhysiosAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/physios-view.fxml";
        String title = "Physios | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void openMyProfileAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/my-profile-view.fxml";
        String title = "Profile | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void onLogoutAction(ActionEvent actionEvent) {
        TokenUtils.removeToken();
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/login-view.fxml";
        String title = "Login | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imgAvatar.setImage(new Image(String.valueOf(getClass().getResource("/images/logo.png"))));
    }
}
