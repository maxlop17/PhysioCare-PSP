package org.example.emailprojectjavafx;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.emailprojectjavafx.models.GenericPetition;
import org.example.emailprojectjavafx.models.Physio.Physio;
import org.example.emailprojectjavafx.models.Physio.PhysioResponse;
import org.example.emailprojectjavafx.models.User.User;
import org.example.emailprojectjavafx.models.User.UserResponse;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;
import org.example.emailprojectjavafx.utils.services.TokenUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class UserProfileViewController implements Initializable {
    @FXML
    public ImageView imgAvatar;
    @FXML
    public Label lblUsername;
    @FXML
    public Label lblRole;
    @FXML
    public Label lblEmail;
    @FXML
    public Label lblUsernameInfo;
    @FXML
    public PasswordField txtPassword;
    private User user;
    private Physio physio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            user = TokenUtils.getUserFromToken();
        } catch (Exception e) {
            System.out.println("Error getting user from token: " + e.getMessage());
        }
        if(user.getRol().equals("physio")){
            ServiceUtils.makePetition(new GenericPetition<>(
                    "physios", user.getId(), "GET", null, PhysioResponse.class,
                    physioResponse -> physio = physioResponse.getPhysio(), "Failed to fetch physio"
            ));
            lblEmail.setText(physio.getEmail());
        }
        if(user.getAvatar() != null && !user.getAvatar().isEmpty()){
            imgAvatar.setImage(new Image(user.getAvatar()));
        } else {
            imgAvatar.setImage(new Image(String.valueOf(getClass().getResource("/images/user_placeholder.png"))));
        }
        lblUsernameInfo.setText(user.getLogin());
        lblUsername.setText(user.getLogin());
        lblRole.setText(user.getRol());
    }

    public void onLogoutClick(ActionEvent actionEvent) {
        TokenUtils.removeToken();
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/login-view.fxml";
        String title = "Login | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void onBackButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        String fxmlFile = "/fxml/first-view.fxml";
        String title = "Welcome | PhysioCare";
        Utils.switchView(source, fxmlFile, title);
    }

    public void onChangePasswordAction(ActionEvent actionEvent) {
        update(txtPassword.getText());
    }

    private void update(String info){
        try {
            Gson gson = new Gson();
            String data = gson.toJson(info);
            ServiceUtils.makePetition(new GenericPetition<>(
                    "users", user.getId(), "PUT", data, UserResponse.class,
                    userResponse -> user = userResponse.getUser(), "Failed to update user"
            ));
        }catch (Exception e) {
            System.out.println("Error updating password: " + e.getMessage());
        }
    }
}
