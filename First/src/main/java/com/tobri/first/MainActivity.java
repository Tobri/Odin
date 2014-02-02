package com.tobri.first;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.test.RenamingDelegatingContext;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends Activity implements ISender {
    // Database Connector
    public static DBConnector dbc;

    // Session Manager Class
    SessionManager session;

    TCPConnector tcpConnector = null;
    Context context;
    ISender iSender;
    ProgressDialog pDialog;

    // Activity elements
    ListView lvSenders;
    TextView lblName;
    Button btnReceive;
    Button btnNewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context         = this;
        iSender         = this;

        lblName         = (TextView) findViewById(R.id.lblName);
        lvSenders       = (ListView) findViewById(R.id.lvSenders);
        btnReceive      = (Button) findViewById(R.id.btnReceive);
        btnNewMessage   = (Button) findViewById(R.id.btnNewMessage);

        // Session class instance
        session = new SessionManager(context);
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        String name = user.get(SessionManager.KEY_NAME);
        // displaying user data
        lblName.setText("Name: " + name);

        dbc = new DBConnector(context);

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
                tcpConnector = new TCPConnector(iSender);
                if (tcpConnector.getStatus() != AsyncTask.Status.RUNNING) {
                    pDialog = new ProgressDialog(context);
                    pDialog.setMessage("Work in Progress");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();
                    tcpConnector.execute(params);
                }
            }
        });

        btnNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SendMessage.class);
                startActivity(i);
            }
        });
    }

    public void updateList() {
        String username = session.getUserDetails().get(SessionManager.KEY_NAME);
        final ListAdapter listAdapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, dbc.getAllSenders(username));
        lvSenders.setAdapter(listAdapter);
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

    @Override
    public void callback(String result) {
        if (result == null) {
            return;
        }

        JSONArray messages;
        JSONObject message;
        Message tmpMessage;

        try {
            messages = new JSONArray(result);
            for (int i = 0; i < messages.length(); i++) {
                message    = messages.getJSONObject(i);
                tmpMessage = new Message(message);
                dbc.addMessage(tmpMessage);
            }
        } catch (JSONException jsone) {
            Log.e("Main: ", jsone.getMessage());
        }

        pDialog.dismiss();
        updateList();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
