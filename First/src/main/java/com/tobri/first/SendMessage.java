package com.tobri.first;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.Date;

public class SendMessage extends Activity implements ISender {

    Context             context;
    ISender             iSender;
    SessionManager      session;
    DBConnector         dbConnector;
    AlertDialogManager  alert;
    EditText            txtReceiver;
    EditText            txtMessage;
    Button              btnSend;
    TCPConnector        tcpConnector;
    ProgressDialog      pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        context = this;
        iSender = this;

        session = new SessionManager(context);
        dbConnector = new DBConnector(context);
        alert = new AlertDialogManager();

        txtReceiver = (EditText) findViewById(R.id.txtReceiver);
        txtMessage  = (EditText) findViewById(R.id.txtMessage);
        btnSend     = (Button) findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiver = txtReceiver.getText().toString();
                String message = txtMessage.getText().toString();
                String sender = session.getUserDetails().get(SessionManager.KEY_NAME);
                String received = Long.toString(new Date().getTime());
                String tmpParam = null;

                try {
                    tmpParam = new Message(null, null, sender, receiver, received, message).toJSON().toString();
                } catch (JSONException jsone) {
                    Log.e("JSONE: ", jsone.getMessage());
                } catch (NullPointerException npe) {
                    Log.e("NPE: ", npe.getMessage());
                } catch (Exception e) {
                    Log.e("E: ", e.getMessage());
                }

                tcpConnector = new TCPConnector(iSender);
                String params[] = {
                    TCPConnector.SERVER_SET,
                    tmpParam
                };
                if (tcpConnector.getStatus() != AsyncTask.Status.RUNNING) {
                    tcpConnector.execute(params);
                    pDialog = new ProgressDialog(context);
                    pDialog.setMessage("Work in Progress");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callback(String result) {
        if (result == null) {
            return;
        }

        pDialog.dismiss();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
