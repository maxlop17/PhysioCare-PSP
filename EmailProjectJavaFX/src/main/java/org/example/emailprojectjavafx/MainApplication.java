package org.example.emailprojectjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;
import org.example.emailprojectjavafx.utils.services.TokenUtils;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String file = "/fxml/login-view.fxml";
        String token = ServiceUtils.getToken();
        if(token != null && !token.isEmpty() && ServiceUtils.validToken(token)) {
            file = "/fxml/first-view.fxml";
        }
        FXMLLoader fxmlLoader = new FXMLLoader(LoginViewController.class.getResource(file));
        Scene scene = new Scene(fxmlLoader.load(), 750, 650);
        stage.setTitle("PhysioCare");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}