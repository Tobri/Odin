package com.tobri.first;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity {
    // Database Connector
    public static DBConnector dbc;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    private ProgressDialog pDialog;

    // Activity elements
    ListView lvSenders;
    TextView lblName;
    Button btnReceive;
    Button btnNewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblName = (TextView) findViewById(R.id.lblName);
        lvSenders = (ListView) findViewById(R.id.lvSenders);
        btnReceive = (Button) findViewById(R.id.btnReceive);
        btnNewMessage = (Button) findViewById(R.id.btnNewMessage);

        // Session class instance
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        String name = user.get(SessionManager.KEY_NAME);
        // hash
        String hash = user.get(SessionManager.KEY_HASH);
        // password
        String pass = user.get(SessionManager.KEY_PASS);
        // displaying user data
        lblName.setText("Name: " + name);// + "\nHash: " + hash + "\nPass: " + pass);

        dbc = new DBConnector(this);

//        try {
//            dbc.dropAll();
//            dbc.addMessage(new Message(1, "Sender 1", "ich1", "20110101", "Text 1"));
//            dbc.addMessage(new Message(2, "Sender 2", "ich2", "20110102", "Text 2"));
//            dbc.addMessage(new Message(3, "Sender 1", "ich3", "20110103", "Text 3"));
//            dbc.addMessage(new Message(4, "Sender 3", "ich4", "20110104", "Text 4"));
//            dbc.addMessage(new Message(5, "Sender 1", "ich5", "20110105", "Text 5"));
//            dbc.addMessage(new Message(6, "Sender 2", "ich6", "20110106", "Text 6"));
//        } catch (JSONException e) {
//            alert.showAlertDialog(this, "Fehler 1", e.toString(), false);
//        }

        updateList();

        lvSenders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(adapterView.getContext(), ShowMessagesActivity.class);
                intent.putExtra("sender", lvSenders.getAdapter().getItem(i).toString());
                adapterView.getContext().startActivity(intent);
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String params[] = {
                        TCPConnector.SERVER_GET,
                        session.getUserDetails().get(SessionManager.KEY_NAME),
                };
                TCPConnector tcpConnector = new TCPConnector();
                if (tcpConnector.getStatus() != AsyncTask.Status.RUNNING) {
                    tcpConnector.execute(params);
                }
            }
        });

        btnNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SendMessage.class);
                startActivity(i);
            }
        });
    }

    private void updateList() {
        String username = session.getUserDetails().get(SessionManager.KEY_NAME);
        final ListAdapter listAdapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, dbc.getAllSenders(username));
        lvSenders.setAdapter(listAdapter);
    }

    private class TCPConnector extends AsyncTask<String, Integer, JSONArray> {
        protected Socket                socket;
        protected PrintWriter           out;
        protected BufferedReader        in;
        protected static final String   SERVER_IP   = "141.56.133.103";
        protected static final int      SERVER_PORT = 8010;

        public static final String      SERVER_GET  = "get";
        public static final String      SERVER_SET  = "set";
        public static final String      SERVER_REM  = "rem";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching Messages...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            JSONArray result = null;

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
                result = new JSONArray(new String(buffer));
                Log.e("Nachricht: ", result.getJSONObject(0).toString());
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
        protected void onPostExecute(JSONArray messages) {
            super.onPostExecute(messages);
            Message tmpMessage;
            JSONObject message;

            if (messages == null) return;

            try {
                for (int i = 0; i < messages.length(); i++) {
                    message    = messages.getJSONObject(i);
                    tmpMessage = new Message(message);
                    Log.e("Sender (" + i + "): ", tmpMessage.toString());
                    dbc.addMessage(tmpMessage);
                }
            } catch (JSONException jsone) {
                Log.e("Main: ", jsone.getMessage());
            } catch (Exception e) {
                Log.e("Main: ", e.getMessage());
            }

            pDialog.dismiss();
            updateList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
