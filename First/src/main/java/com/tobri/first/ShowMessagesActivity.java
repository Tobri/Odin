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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import java.util.Date;

public class ShowMessagesActivity extends Activity implements ISender {

    SessionManager session;
    DBConnector dbc;
    ProgressDialog pDialog;

    protected TextView lblSender;
    protected ListView lvMessages;
    protected EditText txtInput;
    protected Button btnSend;

    TCPConnector tcpConnector;
    Context context;
    ISender iSender;

    String sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);

        context = this;
        iSender = this;

        // Session class instance
        session = new SessionManager(context);
        session.checkLogin();

        dbc = new DBConnector(context);
        lblSender = (TextView) findViewById(R.id.lblSender);
        lvMessages = (ListView) findViewById(R.id.lvMessages);
        txtInput = (EditText) findViewById(R.id.txtInput);
        btnSend = (Button) findViewById(R.id.btnSend);

        Intent intent = getIntent();
        sender = intent.getStringExtra("sender");

        lblSender.setText("Sender: " + sender);

        updateList(sender);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Message tmpMessage = new Message(null, null,
                            session.getUserDetails().get(SessionManager.KEY_NAME),
                            sender,
                            Long.toString(new Date().getTime()),
                            txtInput.getText().toString());
                    String params[] = {
                            TCPConnector.SERVER_SET,
                            tmpMessage.toJSON().toString()
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

    @Override
    public void callback(String result) {

        updateList(sender);
        txtInput.setText("");
        pDialog.dismiss();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
