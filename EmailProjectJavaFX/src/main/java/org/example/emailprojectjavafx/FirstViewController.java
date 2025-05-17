package org.example.emailprojectjavafx;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.email.EmailSenderController;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;
import org.example.emailprojectjavafx.utils.services.TokenUtils;

public class FirstViewController {

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
        String title = "Physios | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }
}
