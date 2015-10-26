package me.toxz.ftp.model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Carlos on 2015/10/27.
 */
public class LocalFile {
    private File mFile;
    private boolean mIsParentFile = false;
    public static final String PARENT_FILE_NAME = "..";
    private final static LocalFile PARENT_FILE = new LocalFile();

    public static LocalFile getParentFile(File parentFile) {
        PARENT_FILE.mFile = parentFile;
        return PARENT_FILE;
    }

    public boolean isRootFile() {
        return mFile == null;
    }

    public boolean isParentFile() {
        return mIsParentFile;
    }

    private LocalFile() {
        // only for PARENT_FILE
        mFile = null;
        mIsParentFile = true;
    }

    public LocalFile(String pathname) {
        mFile = new File(pathname);
    }

    public LocalFile(File file) {
        mFile = file;
    }

    public
    @NotNull
    List<LocalFile> listLocalFiles() {
        return wrap(mFile.listFiles());
    }

    public boolean isDirectory() {
        return mFile.isDirectory();
    }


    @Override
    public String toString() {
        return mFile.getName();
    }

    public static
    @NotNull
    List<LocalFile> wrap(@Nullable File[] files) {
        List<LocalFile> localFileList = new ArrayList<>();
        if (files == null) return localFileList;
        for (File file : files) {
            localFileList.add(new LocalFile(file));
        }
        return localFileList;
    }
}
