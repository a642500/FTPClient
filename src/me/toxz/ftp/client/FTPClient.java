package me.toxz.ftp.client;

import me.toxz.ftp.util.Log;
import sun.nio.ch.IOUtil;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * SimpleFTP is a simple package that implements a Java FTP client. With
 * SimpleFTP, you can connect to an FTP server and upload multiple files.
 * <p>
 * Copyright Paul Mutton, <a
 * href="http://www.jibble.org/">http://www.jibble.org/ </a>
 */
public class FTPClient {

    private static final String TAG = "FTPClient";

    /**
     * Create an instance of SimpleFTP.
     */
    public FTPClient() {

    }

    /**
     * Connects to the default port of an FTP server and logs in as
     * anonymous/anonymous.
     */
    public synchronized void connect(String host) throws IOException {
        connect(host, 21);
    }

    /**
     * Connects to an FTP server and logs in as anonymous/anonymous.
     */
    public synchronized void connect(String host, int port) throws IOException {
        connect(host, port, "anonymous", "anonymous");
    }

    /**
     * Connects to an FTP server and logs in with the supplied username and
     * password.
     */
    public synchronized void connect(String host, int port, String user,
                                     String pass) throws IOException {
        if (socket != null) {
            throw new IOException("SimpleFTP is already connected. Disconnect first.");
        }
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));

        String response = readLine();
        Log.i(TAG, "connect, " + response);
        if (!response.startsWith("220 ") && !response.startsWith("220")) {
            throw new IOException(
                    "SimpleFTP received an unknown response when connecting to the FTP server: "
                            + response);
        }


        Log.i(TAG, "USER login, " + user + " " + pass);
        sendLine("USER " + user);

        Log.i(TAG, "USER, " + response);
        if (!response.startsWith("331") && !response.startsWith("220")) {
            throw new IOException(
                    "SimpleFTP received an unknown response after sending the user: "
                            + response);
        }

        sendLine("PASS " + pass);

        response = readLine();
        Log.i(TAG, "PASS, " + response);
        if (!response.startsWith("230") && !response.startsWith("220") && !response.startsWith("331")) {
            throw new IOException(
                    "SimpleFTP was unable to log in with the supplied password: "
                            + response);
        }
        while (!response.startsWith("230")) {
            response = readLine();
            if (response.startsWith("530")) {
                throw new IncorrectPasswordException("Login or password incorrect!");
            }

            Log.i(TAG, response);
        }
        Log.i(TAG, "logged");


        // Now logged in.
    }

    public class IncorrectPasswordException extends IOException {
        public IncorrectPasswordException(String message) {
            super(message);
        }
    }


    /**
     * Disconnects from the FTP server.
     */
    public synchronized void disconnect() throws IOException {
        try {
            sendLine("QUIT");
        } finally {
            socket = null;
        }
    }

    /**
     * Returns the working directory of the FTP server it is connected to.
     */
    public synchronized String pwd() throws IOException {
        sendLine("PWD");
        String dir = null;
        String response = readLine();
        if (response.startsWith("257 ")) {
            int firstQuote = response.indexOf('\"');
            int secondQuote = response.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                dir = response.substring(firstQuote + 1, secondQuote);
            }
        }
        return dir;
    }


    public synchronized String list() throws IOException {
        sendLine("PASV");
        String response = readLine();
        if (!response.startsWith("227 ")) {
            throw new IOException("SimpleFTP could not request passive mode: "
                    + response);
        }


        String ip = readIP(response);
        int port = readPort(response);
        Log.i(TAG, "ip: " + ip + ", port: " + port);

        Socket dataSocket = new Socket(ip, port);

        sendLine("List");
        response = readLine();
        Log.i(TAG, response);


        BufferedInputStream input = new BufferedInputStream(dataSocket.getInputStream());
        StringWriter stringWriter = new StringWriter();
        Scanner s = new Scanner(input).useDelimiter("\\A");
        String list = s.hasNext() ? s.next() : "";

        stringWriter.flush();
        stringWriter.close();
        input.close();

        Log.i(TAG, list);
        response = readLine();

        Log.i(TAG, response);
        return list;
    }

    /**
     * Changes the working directory (like cd). Returns true if successful.
     */
    public synchronized boolean cwd(String dir) throws IOException {
        sendLine("CWD " + dir);
        String response = readLine();
        return (response.startsWith("250 "));
    }

    /**
     * Sends a file to be stored on the FTP server. Returns true if the file
     * transfer was successful. The file is sent in passive mode to avoid NAT or
     * firewall problems at the client end.
     */
    public synchronized boolean stor(File file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("SimpleFTP cannot upload a directory.");
        }

        String filename = file.getName();

        return stor(new FileInputStream(file), filename);
    }

    private String readIP(String data) throws IOException {
        String ip = null;
        int opening = data.indexOf('(');
        int closing = data.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = data.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
            } catch (Exception e) {
                throw new IOException("SimpleFTP received bad data link information: " + data);
            }
        }
        return ip;
    }

    private int readPort(String data) throws IOException {
        int port = -1;
        int opening = data.indexOf('(');
        int closing = data.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = data.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                for (int i = 0; i < 4; i++) {
                    tokenizer.nextToken();
                }
                port = Integer.parseInt(tokenizer.nextToken()) * 256 + Integer.parseInt(tokenizer.nextToken());
                Log.i(TAG, "port: " + port + ", data: " + data);
            } catch (Exception e) {
                throw new IOException("SimpleFTP received bad data link information: " + data);
            }
        }
        return port;
    }

    public synchronized boolean retr(File outputFile, String filename) throws IOException {
        sendLine("PASV");
        String response = readLine();
        if (!response.startsWith("227 ")) {
            throw new IOException("SimpleFTP could not request passive mode: "
                    + response);
        }
        String ip = readIP(response);
        int port = readPort(response);

        Socket dataSocket = new Socket(ip, port);

        sendLine("RETR " + filename);
        response = readLine();

        if (!response.startsWith("125 ") && !response.startsWith("150 ")) {
            //if (!response.startsWith("150 ")) {
            throw new IOException("SimpleFTP was not allowed to download the file: "
                    + response);
        }

        BufferedInputStream input = new BufferedInputStream(dataSocket.getInputStream());
        FileOutputStream output = new FileOutputStream(outputFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        output.close();
        input.close();

        response = readLine();
        return response.startsWith("226 ");
    }

    /**
     * Sends a file to be stored on the FTP server. Returns true if the file
     * transfer was successful. The file is sent in passive mode to avoid NAT or
     * firewall problems at the client end.
     */
    public synchronized boolean stor(InputStream inputStream, String filename)
            throws IOException {

        BufferedInputStream input = new BufferedInputStream(inputStream);

        sendLine("PASV");
        String response = readLine();
        if (!response.startsWith("227 ")) {
            throw new IOException("SimpleFTP could not request passive mode: "
                    + response);
        }

        String ip = readIP(response);
        int port = readPort(response);

        Socket dataSocket = new Socket(ip, port);

        sendLine("STOR " + filename);


        response = readLine();
        if (!response.startsWith("125 ") && !response.startsWith("150 ")) {
            //if (!response.startsWith("150 ")) {
            throw new IOException("SimpleFTP was not allowed to send the file: "
                    + response);
        }

        BufferedOutputStream output = new BufferedOutputStream(dataSocket
                .getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        output.close();
        input.close();

        response = readLine();
        return response.startsWith("226 ");
    }

    /**
     * Enter binary mode for sending binary files.
     */
    public synchronized boolean bin() throws IOException {
        sendLine("TYPE I");
        String response = readLine();
        return (response.startsWith("200 "));
    }

    /**
     * Enter ASCII mode for sending text files. This is usually the default mode.
     * Make sure you use binary mode if you are sending images or other binary
     * data, as ASCII mode is likely to corrupt them.
     */
    public synchronized boolean ascii() throws IOException {
        sendLine("TYPE A");
        String response = readLine();
        return (response.startsWith("200 "));
    }

    /**
     * Sends a raw command to the FTP server.
     */
    private void sendLine(String line) throws IOException {
        if (socket == null) {
            throw new IOException("SimpleFTP is not connected.");
        }
        try {
            writer.write(line + "\r\n");
            writer.flush();
            if (DEBUG) {
                System.out.println("> " + line);
            }
        } catch (IOException e) {
            socket = null;
            throw e;
        }
    }

    private String readLine() throws IOException {
        String line = reader.readLine();
        if (DEBUG) {
            System.out.println("< " + line);
        }
        return line;
    }

    private Socket socket = null;

    private BufferedReader reader = null;

    private BufferedWriter writer = null;

    private static boolean DEBUG = false;

}


