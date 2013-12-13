package com.tobri.first;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity {
    // Database Connector
    protected DBConnector dbc;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView lblName          = (TextView) findViewById(R.id.lblName);
        final ListView lvSenders  = (ListView) findViewById(R.id.lvSenders);

//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-512");
//            Toast.makeText(this, "SHA-512 gefunden", Toast.LENGTH_LONG).show();
//        } catch (NoSuchAlgorithmException nsae) {
//            Toast.makeText(this, "!SHA-512", Toast.LENGTH_LONG).show();
//        }



        // Session class instance
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        String name = user.get(SessionManager.KEY_NAME);
        String hash = user.get(SessionManager.KEY_HASH);
        // displaying user data
        lblName.setText(Html.fromHtml("Name: <b>" + name + "</b><br />Hash: " + hash));

        this.dbc = new DBConnector(this);

//        try {
//            dbc.addMessage(new Message(1, "Sender 1", "ich1", "20110101", "Text 1"));
//            dbc.addMessage(new Message(2, "Sender 2", "ich2", "20110102", "Text 2"));
//            dbc.addMessage(new Message(3, "Sender 1", "ich3", "20110103", "Text 3"));
//            dbc.addMessage(new Message(4, "Sender 3", "ich4", "20110104", "Text 4"));
//            dbc.addMessage(new Message(5, "Sender 1", "ich5", "20110105", "Text 5"));
//            dbc.addMessage(new Message(6, "Sender 2", "ich6", "20110106", "Text 6"));
//        } catch (JSONException e) {
//            alert.showAlertDialog(this, "Fehler 1", e.toString(), false);
//        }



        ListAdapter listAdapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, dbc.getAllSenders());
        lvSenders.setAdapter(listAdapter);

        lvSenders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(adapterView.getContext(), ShowMessagesActivity.class);
                intent.putExtra("sender", lvSenders.getAdapter().getItem(i).toString());
                adapterView.getContext().startActivity(intent);
            }
        });

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
