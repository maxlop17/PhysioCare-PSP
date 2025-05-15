package org.example.emailprojectjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;
import org.example.emailprojectjavafx.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

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
        }
        else {
            if(ServiceUtils.login(username, password)){
                Node source = (Node) actionEvent.getSource();
                switchView(source);
            }
            else{
                Utils.showAlert("ERROR", "Error: login failed. User or password incorrect.", 2);
            }
        }
    }

    private void switchView(Node source){
        String fxmlFile = "/fxml/first-view.fxml";
        String title = "Welcome | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String token = ServiceUtils.getToken();
        if(!token.isEmpty()) {
            ServiceUtils.setToken(token);
            Utils.showAlert("Login correct", "Login already done", 1);
            switchView(vbox);
        }
    }
}
