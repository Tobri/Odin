package com.tobri.first;

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends ActionBarActivity {
    protected String lockFileName = "tobri.lock";
    protected DBConnector dbc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FileInputStream fis;
        byte            fileContent[] = new byte[512];
        String          username,
                        password;

        try {
            if ((fis = openFileInput(this.lockFileName)) != null) {
                fis.read(fileContent, 0, 512);
                for (int i = 0; i < fileContent.length; i++) {
                    if (':' == fileContent[i]) {
                        username = fileContent.toString().substring(0, i - 1);
                        password = fileContent.toString().substring(i + 1, fileContent.length);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            //TODO
        }
        this.dbc = new DBConnector();
        if (this.dbc.UserDefined()) {
            // Nachrichtenübersicht anzeigen
        } else {
            // Loginbildschirm anzeigen
        }
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
