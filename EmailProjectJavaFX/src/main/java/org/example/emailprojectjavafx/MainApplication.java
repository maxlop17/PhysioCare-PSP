package org.example.emailprojectjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.emailprojectjavafx.utils.email.AppointmentsPatientTask;
import org.example.emailprojectjavafx.utils.services.TokenUtils;


import static org.example.emailprojectjavafx.utils.Utils.showAlert;
import static org.example.emailprojectjavafx.utils.services.TokenUtils.token;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage)  {
        try {
            AppointmentsPatientTask task = new AppointmentsPatientTask();
            task.setPeriod(Duration.hours(24));     //se comprueba cada 24 horas
            task.setOnFailed(_ -> {
                Throwable error = task.getException();
                error.printStackTrace();
                showAlert("ERROR", "Error en AppointmentsPatientTask:\n" + error.getMessage(), 2);
            });
            task.start();

            //If the token exists and is valid it will show the main page
            String file = "/fxml/login-view.fxml";
            TokenUtils.getToken();
            if (token != null && !token.isEmpty() && TokenUtils.validToken()) {
                TokenUtils.setToken(token);
                TokenUtils.decodeToken();
                file = "/fxml/first-view.fxml";
            }
            FXMLLoader fxmlLoader = new FXMLLoader(LoginViewController.class.getResource(file));
            Scene scene = new Scene(fxmlLoader.load(), 750, 650);
            stage.setTitle("PhysioCare");
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

        } catch (Exception e) {
            showAlert("ERROR", "Error al iniciar la aplicación:\n\n" + e.getMessage(), 2);
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }


}