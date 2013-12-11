package com.tobri.first;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ShowMessages extends ActionBarActivity {

    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);

        DBConnector dbc = new DBConnector(this);
        TextView lblSender    = (TextView) findViewById(R.id.lblSender);
        ListView lvMessages = (ListView) findViewById(R.id.lvMessages);

        Intent intent = getIntent();
        String sender = intent.getStringExtra("sender");

        lblSender.setText(Html.fromHtml("Sender: <b>" + sender + "</b>"));

        try {
            ListAdapter listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dbc.getAllMessages(sender));
            lvMessages.setAdapter(listAdapter);
        } catch (Exception e) {
            alert.showAlertDialog(this, "Fehler 2", e.toString(), false);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
