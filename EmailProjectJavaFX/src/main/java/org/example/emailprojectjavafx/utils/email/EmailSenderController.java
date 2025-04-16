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

    public void onSend() {
        String gmail = gmailField.getText();
        String password = passwordField.getText();
        String destination = destinationField.getText();
        String subject = subjectField.getText();
        String message = messageArea.getText();

        if (gmail.isEmpty() || password.isEmpty() || destination.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            showAlert("ERROR", "You have to fill all the fields to send a message.", 2);
        } else {
            try {
                final NetHttpTransport HTTP_TRANSPORT =
                        new com.google.api.client.http.javanet.NetHttpTransport();
                Gmail service =
                        new Gmail.Builder(HTTP_TRANSPORT, EmailSender.JSON_FACTORY,
                                EmailSender.getCredentials(HTTP_TRANSPORT))
                                .setApplicationName(EmailSender.APPLICATION_NAME)
                                .build();

                CreateTableInPdf.createTableInPdf();

                // Define the email parameters
                String user = "me";
                //MimeMessage emailContent = EmailSender.createEmail(destination, gmail, subject, message);
                MimeMessage emailContent =
                        EmailSender.createEmailWithAttachment(destination,
                                gmail,
                                subject,
                                message,
                                "src/main/resources/tableExample.pdf");

                // Send the email
                EmailSender.sendMessage(service, user, emailContent);
                showAlert("SUCCESS", "Email sent.", 1);
            } catch (Exception e) {
                showAlert("ERROR", e.getMessage(), 2);
            }
        }
    }

    public void onBack(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/first-view.fxml";
        String title = "Welcome | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }
}