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

public class SendMessage extends Activity {

    Context context;
    SessionManager session;
    DBConnector dbConnector;
    AlertDialogManager alert;
    EditText txtReceiver;
    EditText txtMessage;
    Button btnSend;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        context = getApplicationContext();

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

                Message newMessage;

                try {
                    newMessage = new Message(null, sender, receiver, received, message);
//                    Long id = dbConnector.addMessage(newMessage);
//                    newMessage.setId(id.intValue());

                    DoSend doSend = new DoSend();
                    Message params[] = {
                        newMessage
                    };
                    doSend.execute(params);
                } catch (JSONException jsone) {
                    Log.e("SendMessage: ", jsone.getMessage());
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

    class DoSend extends AsyncTask<Message, String, String> {
        protected Socket socket;
        protected PrintWriter out;
        protected BufferedReader in;
        protected static final String   SERVER_IP   = "141.56.133.103";
        protected static final int      SERVER_PORT = 8010;

        public static final String      SERVER_GET  = "get";
        public static final String      SERVER_SET  = "set";
        public static final String      SERVER_REM  = "rem";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SendMessage.this);
            pDialog.setMessage("Sending Message...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Message... messages) {
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

            try {
                JSONObject m = messages[0].toJSON();
                m.remove(Message.TAG_ID);
                String tmp = SERVER_SET + " " + m.toString();
                char buffer[] = new char[4096];
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.write(tmp);
                out.flush();

                in.read(buffer, 0, 4096);
                socket.close();
            } catch (IOException ioe) {
                Log.e("IOE: ", ioe.getMessage());
            } catch (JSONException jsone) {
                Log.e("JSONE: ", jsone.getMessage());
            } catch (NullPointerException npe) {
                Log.e("NPE: ", npe.getMessage());
            } catch (Exception e) {
                Log.e("E: ", e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

            if (s.equals("Empfangen")) {
                alert.showAlertDialog(SendMessage.this, "", "Nachricht gesendet!", true);
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(context, "Senden fehlgeschlagen!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
