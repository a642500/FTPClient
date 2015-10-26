package me.toxz.ftp.sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import me.toxz.ftp.model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML TextField userName;
    @FXML TextField password;
    @FXML TextField host;
    @FXML TextField port;
    @FXML CheckBox anonymous;
    @FXML Button login;
    private Main application;
    private User user;
    @FXML Text notificationText;


    public void setApp(Main application) {
        this.application = application;
    }

    public void login(ActionEvent actionEvent) {
        System.out.println("login");

        if (application != null) {
            String userNameText = userName.getText();
            String passwordText = password.getText();
            String hostText = host.getText();
            int portValue = Integer.parseInt(port.getText());
            boolean isAnonymous = anonymous.isSelected();

            //TODO check valid value
            user = isAnonymous ? User.anonymousOf(hostText, portValue) : User.of(userNameText, passwordText, hostText, portValue);
            notificationText.setText("Logging, please waiting...");
            new Thread(mLoginTask).start();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private Task<Boolean> mLoginTask = new Task<Boolean>() {
        @Override
        protected Boolean call() throws Exception {
            application.mClient.connect(user.getHost(), user.getPortValue(), user.getUserName(), user.getPassword());
            this.set(true);
            return true;
        }

        @Override
        protected void failed() {
            notificationText.setText("Login failed!");
        }

        @Override
        protected void succeeded() {
            notificationText.setText("Login success!");
            application.onLoginSuccess(user);
        }
    };
}
