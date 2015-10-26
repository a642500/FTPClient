package me.toxz.ftp.sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Carlos on 2015/10/23.
 */
public class FileExplorerController implements Initializable {
    private Main application;

    @FXML TextField localPathText;
    @FXML TextField remotePathText;
    @FXML TextField currentIPText;
    @FXML Button disconnectBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setApp(Main application) {
        this.application = application;
    }
}
