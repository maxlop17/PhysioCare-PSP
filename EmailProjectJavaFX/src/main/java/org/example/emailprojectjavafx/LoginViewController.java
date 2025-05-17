package org.example.emailprojectjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.TokenUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController {

    @FXML
    public VBox vbox;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;

    public void onLogin(ActionEvent actionEvent) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if(username.isEmpty() || password.isEmpty()) {
            Utils.showAlert("ERROR", "Error: all fields must be filled to continue.", 2);
        } else {
            if(ServiceUtils.login(username, password)){
                Node source = (Node) actionEvent.getSource();
                switchView(source, "/fxml/first-view.fxml");
            }
            else{
                Utils.showAlert("ERROR", "Error: login failed. User or password incorrect.", 2);
            }
        }
    }

    private void switchView(Node source, String fxmlFile){
        String title = "Welcome | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

}
