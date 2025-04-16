package org.example.emailprojectjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;
import org.example.emailprojectjavafx.utils.Utils;

public class LoginViewController {


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
                String fxmlFile = "/fxml/first-view.fxml";
                String title = "Welcome | PhysioCare";
                Utils.switchView(source, fxmlFile, title);
            }
            else{
                Utils.showAlert("ERROR", "Error: login failed. User or password incorrect.", 2);
            }
        }
    }
}
