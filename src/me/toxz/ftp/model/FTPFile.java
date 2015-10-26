package me.toxz.ftp.model;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Carlos on 2015/10/26.
 */
public class FTPFile {
    private final String name;
    private final String dir;
    private final boolean isDir;
    private final long size;
    private final String permission;
    private final String modifiedDate;

    private FTPFile(String name, String dir, boolean isDir, long size, String permission, String modifiedDate) {
        this.name = name;
        this.dir = dir;
        this.isDir = isDir;
        this.size = size;
        this.permission = permission;
        this.modifiedDate = modifiedDate;
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

    @Override
    public String toString() {
        return name;
    }

    public static List<FTPFile> formatAll(String string, String dir) {
        Spliterator<String> spliterator = Splitter.on(Pattern.compile("\\r?\\n")).split(string).spliterator();
        return StreamSupport.stream(spliterator, false).filter(s1 -> !s1.isEmpty()).map(s -> format(s, dir)).collect(Collectors.toList());
    }
}
