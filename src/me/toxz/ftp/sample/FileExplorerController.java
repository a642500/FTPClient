package me.toxz.ftp.sample;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.javafx.binding.SelectBinding;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import me.toxz.ftp.model.FTPFile;
import me.toxz.ftp.model.LocalFile;
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
    private File currentLocalFile = new File(System.getProperty("user.dir"));

    @FXML Label localPathText;
    @FXML Label remotePathText;
    @FXML Label currentIPText;
    @FXML Button disconnectBtn;
    @FXML ListView<LocalFile> localList;
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
        localList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                LocalFile file = localList.getSelectionModel().getSelectedItem();
                Log.i(TAG, "double clicked: " + file);
                if (!changeLocalDirTo(file)) {
                    Log.i(TAG, "double clicked, file: " + file);
                }
            }
        });
//        localList.setCellFactory(new ListViewCellFactory());

        changeLocalDirTo(new LocalFile(currentLocalFile));
        changeRemoteDirTo(null);
    }

    private boolean changeLocalDirTo(@NotNull LocalFile file) {
        if (file.isParent()) {
            // navigation up rather than navigation down
            LocalFile parentFile = file.getStoreParentFile();
            updateFileContent(parentFile);
            return true;
        } else if (file.hasChild()) {
            // navigation down
            updateFileContent(file);
            return true;
        } else return false;// is a file
    }

    private void updateFileContent(@NotNull LocalFile file) {
        List<LocalFile> localFiles = file.listLocalFiles();
        if (!file.isRootParent()) {
            // if can navigation up
            localFiles.add(LocalFile.getParentFile(file.toFile()));
        }
        currentLocalFile = file.toFile();
        Collections.sort(localFiles);
        localList.setItems(new ObservableListWrapper<>(localFiles));
        localPathText.setText(file.getPath());
    }

    private boolean changeRemoteDirTo(@Nullable FTPFile file) {
        if (file != null && !file.hasChild()) {
            return false;
        } else {
            new Thread(new UpdateRemoteListTask(file)).start();
            return true;
        }
    }

//    private static class ListViewCellFactory implements javafx.util.Callback<ListView<LocalFile>, ListCell<LocalFile>> {
//
//        @Override
//        public ListCell<LocalFile> call(ListView<LocalFile> param) {
//            ListCell<LocalFile> cell = new ListCell<>();
//            LocalFile item = cell.
//
//
//            ContextMenu contextMenu = new ContextMenu();
//            MenuItem downloadItem = new MenuItem();
//            downloadItem.textProperty().bind(Bindings.createStringBinding(() -> "Download"));
//            downloadItem.setOnAction(event -> {
//                addToQueue(item);
//            });
//            //TODO  view detail
//            contextMenu.getItems().add(downloadItem);
//
//
//            cell.textProperty().bind(item == null ? Bindings.createStringBinding(() -> "") : cell.itemProperty().asString());
//            if (item == null || item.isParent()) {
//                cell.setContextMenu(null);
//            } else {
//                cell.setContextMenu(contextMenu);
//            }
//            return cell;
//        }
//
//        private void addToQueue(LocalFile file) {
//
//        }
//    }


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
                if (!mFile.isRootParent()) {
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
