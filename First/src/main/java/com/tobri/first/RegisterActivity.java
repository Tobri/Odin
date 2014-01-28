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
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    Context context;

    EditText txtSalutation;
    EditText txtFirstname;
    EditText txtLastname;
    EditText txtStreet;
    EditText txtHNumber;
    EditText txtZipcode;
    EditText txtLocation;
    EditText txtEmail;
    EditText txtUsername;
    EditText txtPassword;
    EditText txtPassword2;
    Button btnRegister;

    String salutation;
    String firstname;
    String lastname;
    String street;
    String hnumber;
    String zipcode;
    String location;
    String email;
    String username;
    String password;
    String password2;

    SessionManager session;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        context = getApplicationContext();
        session = new SessionManager(context);

        txtSalutation = (EditText) findViewById(R.id.txtSalutation);
        txtFirstname = (EditText) findViewById(R.id.txtFirstname);
        txtLastname = (EditText) findViewById(R.id.txtLastname);
        txtStreet = (EditText) findViewById(R.id.txtStreet);
        txtHNumber = (EditText) findViewById(R.id.txtHNumber);
        txtZipcode = (EditText) findViewById(R.id.txtZipcode);
        txtLocation = (EditText) findViewById(R.id.txtLocation);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPassword2 = (EditText) findViewById(R.id.txtPassword2);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salutation = txtSalutation.getText().toString();
                firstname = txtFirstname.getText().toString();
                lastname = txtLastname.getText().toString();
                street = txtStreet.getText().toString();
                hnumber = txtHNumber.getText().toString();
                zipcode = txtZipcode.getText().toString();
                location = txtLocation.getText().toString();
                email = txtEmail.getText().toString();
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();
                password2 = txtPassword2.getText().toString();


                if (email.trim().length() > 0 &&
                        firstname.trim().length() > 0 &&
                        hnumber.trim().length() > 0 &&
                        lastname.trim().length() > 0 &&
                        location.trim().length() > 0 &&
                        password.trim().length() > 0 &&
                        password2.trim().length() > 0 &&
                        salutation.trim().length() > 0 &&
                        street.trim().length() > 0 &&
                        username.trim().length() > 0 &&
                        zipcode.trim().length() > 0) {
                    if (password.equals(password2)) {
                        // Alle Felder gefüllt und Passwörter stimmen überein
                        DoRegister doRegister = new DoRegister();
                        String params[] = {
                                username,
                                new Crypter().cryptPassword(password),
                                salutation,
                                lastname,
                                firstname,
                                street,
                                hnumber,
                                zipcode,
                                location,
                                email
                        };
                        doRegister.execute(params);
                    } else {
                        Log.e("Register: ", "Passwörter stimmen nicht überein...");
                    }
                } else {
                    // Feld(er) vergessen
                    Log.e("Register: ", "Felder vergessen");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
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

    class DoRegister extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Attempting registration...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            HTTPConnector httpConnector = new HTTPConnector();
            return httpConnector.register(strings[0], strings[1], strings[2], strings[3], strings[4],
                    strings[5], strings[6], strings[7], strings[8], strings[9]);
        }
        protected void onPostExecute(Boolean registered) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (registered) {
                // Staring MainActivity
                Intent i = new Intent(context, LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                alert.showAlertDialog(RegisterActivity.this, "Register failed..", "Something went wrong...", false);
            }
        }

    }
}
