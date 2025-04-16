module org.example.emailprojectjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires kernel;
    requires layout;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.auth;
    requires google.api.client;
    requires com.google.api.services.gmail;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires jakarta.mail;
    requires jdk.httpserver;
    requires com.google.gson;
    requires javafx.graphics;

    opens org.example.emailprojectjavafx to javafx.fxml;
    exports org.example.emailprojectjavafx;
    exports org.example.emailprojectjavafx.utils.pdf;
    opens org.example.emailprojectjavafx.utils.pdf to javafx.fxml;
    exports org.example.emailprojectjavafx.utils;
    opens org.example.emailprojectjavafx.utils to javafx.fxml;
    exports org.example.emailprojectjavafx.utils.email;
    opens org.example.emailprojectjavafx.utils.email to javafx.fxml;

    opens org.example.emailprojectjavafx.models.Patient;
    opens org.example.emailprojectjavafx.models.Physio;
    opens org.example.emailprojectjavafx.models.Appointment;
    opens org.example.emailprojectjavafx.models.Record;

    opens org.example.emailprojectjavafx.models.Auth to com.google.gson;
    opens org.example.emailprojectjavafx.models to com.google.gson;
    exports org.example.emailprojectjavafx.utils.services;
    opens org.example.emailprojectjavafx.utils.services to javafx.fxml;


}