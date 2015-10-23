package me.toxz.ftp.sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML TextField userName;
    @FXML TextField password;
    @FXML TextField host;
    @FXML TextField port;
    @FXML CheckBox anonymous;
    @FXML Button login;
    private Main application;


    public void setApp(Main application) {
        this.application = application;
    }

    public void onLogin(ActionEvent actionEvent) {

        if (application != null) {
            String userNameText = userName.getText();
            String passwordText = password.getText();
            String hostText = host.getText();
            int portValue = Integer.parseInt(port.getText());
            boolean isAnonymous = anonymous.isSelected();

            application.login();
        }
    }

}
