package org.example.emailprojectjavafx.utils.email;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.example.emailprojectjavafx.models.Appointment.Appointment;
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Patient.Patient;
import org.example.emailprojectjavafx.models.Patient.PatientListResponse;
import org.example.emailprojectjavafx.models.Record.Record;
import org.example.emailprojectjavafx.models.Record.RecordListResponse;
import org.example.emailprojectjavafx.utils.pdf.CreateTableInPdf;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.example.emailprojectjavafx.utils.Utils.showAlert;

public class EmailSender {

    public static final String APPLICATION_NAME = "FirstProjectEmail";
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/client_secret_779503636329-chhoit24b9vo1coo234d0it4vccm65q2.apps.googleusercontent.com.json";


    public static Credential getCredentials(
            final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        // Load client secrets.
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY,
                        new InputStreamReader(
                                new FileInputStream(CREDENTIALS_FILE_PATH)));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                        Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM))
                        .setDataStoreFactory(new FileDataStoreFactory(
                                new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline")
                        .build();
        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");
    }

    /**
     * Create a MimeMessage using the parameters provided.
     */
    public static MimeMessage createEmail(
            String to, String from, String subject,
            String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public static MimeMessage createEmailWithAttachment(String to,
                                                        String from,
                                                        String subject,
                                                        String bodyText,
                                                        String fileDir)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        // Crear el cuerpo del correo
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(bodyText, "utf-8");

        // Adjuntar archivo
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setDataHandler(new jakarta.activation.DataHandler(
                new jakarta.activation.FileDataSource(fileDir)));
        attachmentPart.setFileName(new java.io.File(fileDir).getName());

        // Crear estructura MIME
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        email.setContent(multipart, "multipart/mixed");

        return email;
    }


    /**
     * Create a message from an email.
     */
    public static Message createMessageWithEmail(
            MimeMessage email) throws MessagingException, java.io.IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = java.util.Base64.getUrlEncoder()
                .encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     */
    public static void sendMessage(
            Gmail service, String userId, MimeMessage emailContent)
            throws MessagingException, java.io.IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages()
                .send(userId, message).execute();
        System.out.println("Message id: " + message.getId());
        System.out.println("Email sent successfully.");
    }

    private static void onSend(Patient patient, List<Appointment> appointments) {
        String gmail = "maximlopezlopez@gmail.com";
        String subject = "Appointment Reminder";
        String message = "Hello " + patient.getName() + "! We are contacting you from PhysioCare Physiotherapy Company to remind you " +
                "that you have 2 appointments left to reach the maximum. Thank you. Kind regards, PhysioCare.";

        if (patient.getEmail().isEmpty()) {
            showAlert("ERROR", "The destination email is not provided.", 2);
        } else {
            try {
                final NetHttpTransport HTTP_TRANSPORT =
                        new com.google.api.client.http.javanet.NetHttpTransport();
                Gmail service =
                        new Gmail.Builder(HTTP_TRANSPORT, EmailSender.JSON_FACTORY,
                                EmailSender.getCredentials(HTTP_TRANSPORT))
                                .setApplicationName(EmailSender.APPLICATION_NAME)
                                .build();

                String destination = "EmailProjectJavaFX/src/main/resources/appointments/" + patient.getId() + ".pdf";
                CreateTableInPdf.createTableInPdf(appointments, destination);

                // Define the email parameters
                String user = "me";
                //MimeMessage emailContent = EmailSender.createEmail(destination, gmail, subject, message);
                MimeMessage emailContent =
                        EmailSender.createEmailWithAttachment(patient.getEmail(),
                                gmail,
                                subject,
                                message,
                                destination);

                // Send the email
                EmailSender.sendMessage(service, user, emailContent);
                showAlert("SUCCESS", "Email sent.", 1);
            } catch (Exception e) {
                showAlert("ERROR", e.getMessage(), 2);
            }
        }
    }

    public static void sendAppointmentReminderMail(){
        ServiceUtils.makePetition(new GenericPetition<>(
                "patients", "", "GET", null, PatientListResponse.class,
                patientListResponse -> {
                    patientListResponse.getPatients().forEach(p -> {
                        ServiceUtils.makePetition(new GenericPetition<>(
                                "records", p.getId() + "/patient", "GET", null, RecordListResponse.class,
                                recordListResponse -> {
                                    Record record = recordListResponse.getRecords().getFirst();
                                    if(record.getAppointments().size() >= 8){
                                        onSend(p, recordListResponse.getRecords().getFirst().getAppointments());
                                    }
                                }, "Failed to fetch records"
                        ));
                    });
                }, "Failed to fetch patients"
        ));
    }
}