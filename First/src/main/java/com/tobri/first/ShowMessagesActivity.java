package com.tobri.first;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import java.text.DateFormat;
import java.util.Date;

public class ShowMessagesActivity extends ActionBarActivity {

    AlertDialogManager alert = new AlertDialogManager();

    SessionManager session;
    DBConnector dbc;

    protected TextView lblSender;
    protected ListView lvMessages;
    protected EditText txtInput;
    protected Button btnSend;

    String sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);

        // Session class instance
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        dbc = new DBConnector(this);
        lblSender = (TextView) findViewById(R.id.lblSender);
        lvMessages = (ListView) findViewById(R.id.lvMessages);
        txtInput = (EditText) findViewById(R.id.txtInput);
        btnSend = (Button) findViewById(R.id.btnSend);

        Intent intent = getIntent();
        sender = intent.getStringExtra("sender");

        lblSender.setText(Html.fromHtml("Sender: <b>" + sender + "</b>"));

        updateList(sender);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Message tmpMessage = new Message(null,
                            session.getUserDetails().get(SessionManager.KEY_NAME),
                            sender,
                            Long.toString(new Date().getTime()),
                            txtInput.getText().toString());
                    String params[] = {
                            TCPConnector.SERVER_SET,
                            tmpMessage.toJSON().toString()
                    };
                    TCPConnector tcpConnector = new TCPConnector();
                    if (tcpConnector.getStatus() != AsyncTask.Status.RUNNING) {
                        tcpConnector.execute(params);
                    }
                } catch (JSONException jsone) {
                    Log.e("JSONE: ", jsone.getMessage());
                }

            }
        });
    }

    protected void updateList(String sender) {
        try {
            ListAdapter listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dbc.getAllMessages(sender));
            lvMessages.setAdapter(listAdapter);
        } catch (Exception e) {
            Log.e("Exception: ", e.getMessage());
        }
    }

    private class TCPConnector extends AsyncTask<String, Integer, JSONObject> {
        protected Socket                socket;
        protected PrintWriter           out;
        protected BufferedReader        in;
        protected static final String   SERVER_IP   = "141.56.133.103";
        protected static final int      SERVER_PORT = 8010;

        public static final String      SERVER_GET  = "get";
        public static final String      SERVER_SET  = "set";
        public static final String      SERVER_REM  = "rem";

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject result = null;

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
            }

            String tmp = strings[0] + " " + strings[1];
            try {
                char buffer[] = new char[4096];
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.write(tmp);
                out.flush();

                in.read(buffer, 0, 4096);

                result = new JSONObject(strings[1]);
                socket.close();
            } catch (IOException ioe) {
                Log.e("IOE: ", ioe.getMessage());
            } catch (JSONException jsone) {
                Log.e("IOE: ", jsone.getMessage());
            } catch (NullPointerException npe) {
                Log.e("NPE: ", npe.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(JSONObject message) {
            //super.onPostExecute(messages);

            try {
                dbc.addMessage(new Message(message));
            } catch (JSONException jsone) {
                Log.e("JSONE: ", jsone.getMessage());
            }
            txtInput.setText("");
            updateList(sender);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_messages, menu);
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
        } else if (id == R.id.action_logout) {
            session.logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
