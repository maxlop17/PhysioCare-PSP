package org.example.emailprojectjavafx.utils.email;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import jakarta.mail.internet.MimeMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.pdf.CreateTableInPdf;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;


public class EmailSenderController {
    @FXML
    private TextField gmailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField destinationField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea messageArea;


    @FXML
    public void initialize() {
        gmailField.setText("");
        passwordField.setText("");
        destinationField.setText("");
        subjectField.setText("Mensaje de prueba");
        messageArea.setText("Mensaje de prueba");
    }



    public void onBack(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/first-view.fxml";
        String title = "Welcome | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }
}