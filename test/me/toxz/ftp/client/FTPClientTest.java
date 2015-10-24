package me.toxz.ftp.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        client.connect("90.130.70.73", 21);
        client.list();
    }

}
