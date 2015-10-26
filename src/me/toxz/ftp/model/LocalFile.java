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
public class LocalFile extends TreeIterable<LocalFile> {
    private File mFile;
    private boolean mIsParentFile = false;
    public static final String PARENT_FILE_NAME = "..";
    private final static LocalFile PARENT_FILE = new LocalFile();

    public static LocalFile getParentFile(File parentFile) {
        PARENT_FILE.mFile = parentFile;
        return PARENT_FILE;
    }

    public LocalFile getStoreParentFile() {
        return new LocalFile(mFile.getParentFile());
    }

    public boolean isRootParent() {
        return mFile.getParentFile() == null;
    }

    @Override
    protected int hasChildCompareToHasChild(LocalFile o) {
        return mFile.getName().length() - o.mFile.getName().length();
    }

    @Override
    protected int basicCompareToBasic(LocalFile o) {
        return mFile.getName().length() - o.mFile.getName().length();
    }

    public boolean isParent() {
        return mIsParentFile;
    }

    public File toFile() {
        return mFile;
    }

    public String getPath() {
        return mFile.getPath();
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

    public boolean hasChild() {
        return isParent() || mFile.isDirectory();
    }


    @Override
    public String toString() {
        if (isParent()) {
            return "..";
        } else
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
