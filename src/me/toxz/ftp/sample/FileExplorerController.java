package me.toxz.ftp.sample;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import me.toxz.ftp.model.FTPFile;
import me.toxz.ftp.model.User;
import me.toxz.ftp.util.Log;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Carlos on 2015/10/23.
 */
public class FileExplorerController implements Initializable {
    private static final String TAG = "FileExplorerController";
    private Main application;
    private User user;
    private File currentRemoteDir = new File(System.getProperty("user.dir"));

    @FXML Label localPathText;
    @FXML Label remotePathText;
    @FXML Label currentIPText;
    @FXML Button disconnectBtn;
    @FXML ListView<FTPFile> localList;
    @FXML ListView<FTPFile> remoteList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setApp(Main application) {
        this.application = application;
    }

    void init(User u) {
        user = u;
        currentIPText.setText(user.getHost() + ": " + user.getPortValue());
        remoteList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                FTPFile file = remoteList.getSelectionModel().getSelectedItem();
                Log.i(TAG, "double clicked: " + file);
            }
        });


        updateRemoteList();
    }

    private Task<String> remoteFileListTask = new Task<String>() {
        @Override
        protected String call() throws Exception {
            return application.mClient.list();
        }

        @Override
        protected void succeeded() {
            remoteList.setItems(new ObservableListWrapper<>(FTPFile.formatAll(this.getValue(), currentRemoteDir.getAbsolutePath())));
        }

        @Override
        protected void failed() {
            super.failed();
        }
    };

    private void updateRemoteList() {
        new Thread(remoteFileListTask).start();
    }
}
