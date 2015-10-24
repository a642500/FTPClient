package me.toxz.ftp.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.toxz.ftp.client.FTPClient;
import me.toxz.ftp.model.User;

import java.io.IOException;

public class Main extends Application {
    private User mUser;
    private Stage mStage;
    private FTPClient mClient;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mClient = new FTPClient();


        mStage = primaryStage;
        mStage.setMinWidth(380);
        mStage.setMinHeight(500);
        gotoLogin();


        primaryStage.show();
    }

    private void gotoLogin() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        mStage.setTitle("Login");
        mStage.setScene(new Scene(root, 600, 400));
    }


    public static void main(String[] args) {
        launch(args);
    }

    boolean login(User user) {
        try {
            if (mUser.isAnonymous()) {
                mClient.connect(user.getHost(), user.getPortValue(), user.getUserName(), user.getPassword());
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
