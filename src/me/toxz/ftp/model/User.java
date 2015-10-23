package me.toxz.ftp.model;

/**
 * Created by Carlos on 2015/10/23.
 */
public class User {
    private String userName;
    private String password;
    private String host;
    private int portValue;
    private boolean isAnonymous = false;
    public static final String ANONYMOUS = "anonymous";


    public static User of(String userName, String password, String host, int portValue) {
        return new User(userName, password, host, portValue);
    }

    public static User anonymousOf(String host, int portValue) {
        User user = new User(ANONYMOUS, ANONYMOUS, host, portValue);
        user.isAnonymous = true;
        return user;
    }

    private User(String userName, String password, String host, int portValue) {
        this.password = password;
        this.userName = userName;
        this.host = host;
        this.portValue = portValue;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPortValue(int portValue) {
        this.portValue = portValue;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPortValue() {
        return portValue;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }
}
