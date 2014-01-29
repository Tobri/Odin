package com.tobri.first;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by studat on 29.01.14.
 */
public class TCPConnector extends AsyncTask<String, Integer, String> {
    protected Socket                socket;
    protected PrintWriter           out;
    protected BufferedReader        in;
    protected static final String   SERVER_IP   = "141.56.133.103";
    protected static final int      SERVER_PORT = 8010;

    public static final String  SERVER_GET  = "get";
    public static final String  SERVER_SET  = "set";
    public static final String  SERVER_REM  = "rem";

    public Activity activity;
    public Context context;
    public ProgressDialog pDialog;
    public DBConnector dbc;
    public String method;

    public TCPConnector(Activity activity,Context context) {
        this.activity = activity;
        this.context = context;
        dbc = new DBConnector(context);
    }

    @Override
    protected String doInBackground(String... strings) {
        method = strings[0];
        String result = null;

        try {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            socket = new Socket(serverAddress, SERVER_PORT);
            socket.setSoTimeout(600);
            socket.setSoLinger(true, 600);
            socket.setKeepAlive(false);
            socket.setReceiveBufferSize(4096);
        } catch (UnknownHostException uhe) {
            Log.e("UHE: ", uhe.getMessage());
        } catch (IOException ioe) {
            Log.e("IOE: ", ioe.getMessage());
        } catch (NullPointerException npe) {
            Log.e("NPE: ", npe.getMessage());
        } catch (Exception e) {
            Log.e("E: ", e.getMessage());
        }

        String tmp = strings[0] + " " + strings[1];

        try {
            char buffer[] = new char[4096];
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write(tmp);
            out.flush();

            in.read(buffer, 0, 4096);
            result = new String(buffer);
            socket.close();
        } catch (IOException ioe) {
            Log.e("IOE: ", ioe.getMessage());
        } catch (NullPointerException npe) {
            Log.e("NPE: ", npe.getMessage());
        } catch (Exception e) {
            Log.e("E: ", e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Work in Progress");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Message tmpMessage;
        JSONArray messages;
        JSONObject message;

        if (s == null) {
            return;
        } else if (method.equals(SERVER_GET)){
            try {
                messages = new JSONArray(s);
                for (int i = 0; i < messages.length(); i++) {
                    message    = messages.getJSONObject(i);
                    tmpMessage = new Message(message);
                    dbc.addMessage(tmpMessage);
                    TCPConnector tcpConnector = new TCPConnector(activity, context);
                    String params[] = {
                            SERVER_REM,
                            tmpMessage.getId().toString()
                    };
                    tcpConnector.execute(params);
                }
            } catch (JSONException jsone) {
                Log.e("Main: ", jsone.getMessage());
            } catch (Exception e) {
                Log.e("Main: ", e.getMessage());
            }
        }

        pDialog.dismiss();
    }
}
