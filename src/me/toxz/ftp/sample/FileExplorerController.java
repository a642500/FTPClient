package me.toxz.ftp.sample;

import com.sun.istack.internal.Nullable;
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
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Carlos on 2015/10/23.
 */
public class FileExplorerController implements Initializable {
    private static final String TAG = "FileExplorerController";
    private Main application;
    private User user;
    private String currentRemoteDir = "";

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
                if (!changeRemoteDirTo(file)) {
                    Log.i(TAG, "double clicked, file: " + file);
                }
            }
        });


        changeRemoteDirTo(null);
    }

    private boolean changeRemoteDirTo(@Nullable FTPFile file) {
        if (file != null && !file.isDir()) {
            return false;
        } else {
            new Thread(new UpdateRemoteListTask(file)).start();
            return true;
        }
    }

    private class UpdateRemoteListTask extends Task<UpdateRemoteListTask.Result> {
        private final @Nullable FTPFile mFile;

        public UpdateRemoteListTask(@Nullable FTPFile changeTo) {
            mFile = changeTo;
        }

        @Override
        protected Result call() throws Exception {
            if (mFile != null && !application.mClient.cwd(mFile.getName())) {
                failed();
                return null;
            } else {
                String dir = application.mClient.pwd();
                String list = application.mClient.list();
                return new Result(dir, list);
            }
        }

        @Override
        protected void succeeded() {
            List<FTPFile> ftpFiles = FTPFile.formatAll(this.getValue().list, currentRemoteDir);
            if (mFile != null) {
                currentRemoteDir = getValue().dir;
                if (!mFile.isRootFile()) {
                    ftpFiles.add(FTPFile.getParentFile());
                }
            }
            Collections.sort(ftpFiles);
            remoteList.setItems(new ObservableListWrapper<>(ftpFiles));
            remotePathText.setText(currentRemoteDir);
        }

        @Override
        protected void failed() {
            super.failed();
        }

        public class Result {
            String dir;
            String list;

            public Result(String dir, String list) {
                this.dir = dir;
                this.list = list;
            }
        }
    }
}
