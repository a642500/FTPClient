package me.toxz.ftp.model;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Carlos on 2015/10/26.
 */
public class FTPFile implements Comparable<FTPFile> {
    private final String name;
    private final String dir;
    private final boolean isDir;
    private final long size;
    private final String permission;
    private final String modifiedDate;
    public static final String PARENT_FILE_NAME = "..";
    private final static FTPFile PARENT_FILE = new FTPFile(PARENT_FILE_NAME, null, true, 0, null, null);


    public static FTPFile getParentFile() {
        return PARENT_FILE;
    }

    public boolean isRootFile() {
        return name.equals(PARENT_FILE_NAME);
    }

    private FTPFile(String name, String dir, boolean isDir, long size, String permission, String modifiedDate) {
        this.name = name;
        this.dir = dir;
        this.isDir = isDir;
        this.size = size;
        this.permission = permission;
        this.modifiedDate = modifiedDate;
    }

    public boolean isParentFile() {
        return this.equals(PARENT_FILE);
    }

    public static FTPFile format(String string, String dir) {
        Spliterator<String> spliterator = Splitter.on(CharMatcher.BREAKING_WHITESPACE).split(string).spliterator();
        Iterator<String> info = StreamSupport.stream(spliterator, false).filter((s) -> !s.isEmpty()).iterator();
        String permission = info.next();
        info.next();
        info.next();
        info.next();
        long size = Long.parseLong(info.next());
        String date = info.next() + " " + info.next() + " " + info.next();
        String name = info.next();
        boolean isDir = permission.contains("d");
        FTPFile ftpFile = new FTPFile(name, dir, isDir, size, permission, date);
        System.out.println(ftpFile);
        return ftpFile;
    }

    public String getName() {
        return name;
    }

    public String getDir() {
        return dir;
    }

    public boolean isDir() {
        return isDir;
    }

    public long getSize() {
        return size;
    }

    public String getPermission() {
        return permission;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public String toString() {
        if (isParentFile()) {
            return PARENT_FILE_NAME;
        } else
            return name;
    }


    public static List<FTPFile> formatAll(String string, String dir) {
        Spliterator<String> spliterator = Splitter.on(Pattern.compile("\\r?\\n")).split(string).spliterator();
        return StreamSupport.stream(spliterator, false).filter(s1 -> !s1.isEmpty()).map(s -> format(s, dir)).collect(Collectors.toList());
    }

    @Override
    public int compareTo(FTPFile o) {
        if (isParentFile() && o.isParentFile())
            return 0;
        if (isParentFile())
            return -1;
        else if (o.isParentFile())
            return 1;
        if (isDir() && o.isDir()) {
            return o.name.length() - name.length();
        }
        if (isDir()) {
            return -1;
        } else if (o.isDir()) {
            return 1;
        } else {
            return (int) (o.size - size);
        }
    }
}
