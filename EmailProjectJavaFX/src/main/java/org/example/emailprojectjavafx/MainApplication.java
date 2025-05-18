package org.example.emailprojectjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginViewController.class.getResource("/fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 650);
        stage.setTitle("PhysioCare");
        stage.setScene(scene);
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