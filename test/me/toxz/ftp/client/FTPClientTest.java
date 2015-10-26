package me.toxz.ftp.client;

import me.toxz.ftp.model.FTPFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

/**
 * Created by yyz on 10/21/15.
 */
public class FTPClientTest {
    private FTPClient client;

    @Before
    public void createClient() {
        client = new FTPClient();
    }


    @Test
    public void testConnect() throws IOException {

    }

    @Test
    public void testFormat() {
        FTPFile.format("drwxr-xr-x    2 105      108          4096 Oct 26 09:49 upload", "/");
    }

    @Test
    public void testFormatAll() throws IOException {
        client.connect("90.130.70.73", 21);
        String list = client.list();
        System.out.println(FTPFile.formatAll(list, "/"));
    }

}
