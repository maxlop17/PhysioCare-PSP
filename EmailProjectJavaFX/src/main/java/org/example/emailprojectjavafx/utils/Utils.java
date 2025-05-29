package org.example.emailprojectjavafx.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Utils {
    public static void showAlert(String title, String content, int type) {
        Alert alert = null;
        if (type == 1) {
            alert = new Alert(Alert.AlertType.INFORMATION);
        } else if (type == 2) {
            alert = new Alert(Alert.AlertType.ERROR);
        }
        assert alert != null;
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void switchView(Node source, String fxmlFile, String title) {
        Stage stage = (Stage) source.getScene().getWindow();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Utils.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo cargar la vista: " + e.getMessage(), 2);
        }
    }
    public static void switchView(Node source, Parent root, String title) {
        Stage stage = (Stage) source.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle(title);
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }


}
