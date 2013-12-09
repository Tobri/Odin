package com.tobri.first;

/**
 * Created by studat on 28.11.13.
 */
public class HTTPConnector {
    private static String ACTION_LOGIN   = "login";
    private static String ACTION_CHECK   = "check";
    private static String ACTION_SEND    = "send";
    private static String ACTION_RECEIVE = "receive";

    protected String url;

    public HTTPConnector(String url) {
        this.url = url;
    }

    public String login(String username, String password) {
        String currentHash = "a961217ca653dabc62091a94753c2d9401f4a5d796f375908753d20f9ae6c7364e2444245e47bf0e1b0075771419251434bd668a77034abb945875980af81036";
        return currentHash;
    }

    public boolean checkUserName(String username) {
        return true;
    }
}
