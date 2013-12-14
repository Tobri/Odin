package com.tobri.first;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

/**
 * Created by mkn on 14.12.13.
 */
public class TCPConnector implements Runnable {
    protected Socket socket;
    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected static final String   SERVER_IP   = "141.56.133.103";
    protected static final int      SERVER_PORT = 8010;
    protected static final String   SERVER_GET  = "get";
    protected static final String   SERVER_SET  = "set";
    protected static final String   SERVER_REM  = "rem";

    @Override
    public void run() {
        try {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            this.socket = new Socket(serverAddress, SERVER_PORT);
            this.outputStream = this.socket.getOutputStream();
            this.inputStream = this.socket.getInputStream();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    public JSONArray recieveMessages(String hash) {
        String tmp = SERVER_GET + hash;

        try {
            this.outputStream.write(tmp.getBytes(Charset.forName("UTF-8")));
            return new JSONArray(this.inputStream.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }

        return null;
    }
}
