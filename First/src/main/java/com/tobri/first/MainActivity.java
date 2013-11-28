package com.tobri.first;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import java.io.*;

public class MainActivity extends ActionBarActivity {
    protected DBConnector   dbc;
    protected HTTPConnector oracleCon;
    protected HTTPConnector mongoCon;
    protected String        lockFileName = "garm.lock";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        byte            fileContent[] = new byte[512];
        String          username,
                        password;
        File            file = new File(this.lockFileName);

        if (!file.exists()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

//        try {
//            fis.read(fileContent, 0, 512);
//        } catch (IOException ioe) {
//            // ToDo: Falls Datei leer / nicht lesbar -> neuer Programmpfad
//        }
//
//        for (int i = 0; i < fileContent.length; i++) {
//            if (':' == fileContent[i]) {
//                username = fileContent.toString().substring(0, i - 1);
//                password = fileContent.toString().substring(i + 1, fileContent.length);
//                break;
//            }
//        }

        this.dbc        = new DBConnector();
        this.oracleCon  = new HTTPConnector();



        setContentView(R.layout.activity_main);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
