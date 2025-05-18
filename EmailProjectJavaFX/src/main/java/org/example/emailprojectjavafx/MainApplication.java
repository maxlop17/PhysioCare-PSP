package org.example.emailprojectjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;
import org.example.emailprojectjavafx.utils.services.TokenUtils;

import java.io.IOException;

import static org.example.emailprojectjavafx.utils.services.TokenUtils.token;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //If the token exists and is valid it will show the main page
        String file = "/fxml/login-view.fxml";
        TokenUtils.getToken();
        if(token != null && !token.isEmpty() && TokenUtils.validToken()) {
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
    }


    public static void main(String[] args) {
        launch();
    }


    /*

    COSAS QUE HAY QUE MIRARa
    Al updatear un patient se updatea pero no cambia la listview ni sale ningun mensaje, hay que salir y volver a entrar
    Al updatear un physio se updatea pero no cambia la listview ni sale ningun mensaje, hay que salir y volver a entrar


    */
}