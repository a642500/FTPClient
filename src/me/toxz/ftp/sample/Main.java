package me.toxz.ftp.sample;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import me.toxz.ftp.client.FTPClient;
import me.toxz.ftp.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    User mUser;
    private Stage mStage;
    FTPClient mClient;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mClient = new FTPClient();


        mStage = primaryStage;
        gotoLogin();
        primaryStage.show();
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Main.class.getResource(fxml));
        GridPane pane;
        try (InputStream in = Main.class.getResourceAsStream(fxml)) {
            pane = loader.load(in);
        }
        Scene scene = new Scene(pane);
        mStage.setScene(scene);
        mStage.sizeToScene();
        return (Initializable) loader.getController();
    }

    private void gotoLogin() {
        System.out.println("gotoLogin");
        try {
            LoginController login = (LoginController) replaceSceneContent("Login.fxml");
            login.setApp(this);
            mStage.setTitle("Login");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gotoFileExplorer() {
        System.out.println("gotoFileExplorer");
        try {
            FileExplorerController controller = (FileExplorerController) replaceSceneContent("file_explorer.fxml");
            controller.setApp(this);
            controller.init(mUser);
            mStage.setTitle("File Transfer");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void onLoginSuccess(User user) {
        mUser = user;
        gotoFileExplorer();
    }


    public static void main(String[] args) {
        launch(args);
    }


}
