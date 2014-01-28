package com.tobri.first;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by studat on 28.11.13.
 */
public class HTTPConnector {
    /* http://iwap4.informatik.htw-dresden.de:8000/ZendSkeletonApplication/public/json/index */
    /* [/:USERID][/:PWD][/:ANREDE][/:NAME][/:VORNAME][/:STRASSE][/:HSNR][/:PLZ][/:ORT][/:EMAIL] */
    private static String URL = "http://iwap4.informatik.htw-dresden.de:8000/ZendSkeletonApplication/public/json/";
    private static String ACTION_LOGIN = "index";
    private static String ACTION_REGISTER = "add";

    InputStream inputStream;

    public HTTPConnector() { }

    public String login(String username, String password) {
        String json = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String uri = URL + ACTION_LOGIN + "/" + username + "/" + password;
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            inputStream = httpEntity.getContent();
        } catch (ClientProtocolException cpe) {
            Log.e("HTTPConnector: ", cpe.getMessage());
        } catch (IOException ioe) {
            Log.e("HTTPConnector: ", ioe.getMessage());
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("HTTPConnector: ", "Error converting result " + e.getMessage());
        }

        JSONObject jObj;

        // try parse the string to a JSON object
        try {
            jObj = new JSONArray(json).getJSONObject(0);
            return jObj.getString("AKT_HASH");
        } catch (JSONException e) {
            Log.e("HTTPConnector: ", "Error parsing data " + e.getMessage());
        }


        return null;
    }

    public boolean checkUserName(String username) {
        return true;
    }

    /* [/:USERID][/:PWD][/:ANREDE][/:NAME][/:VORNAME][/:STRASSE][/:HSNR][/:PLZ][/:ORT][/:EMAIL] */
    public boolean register(String username, String password, String salutation,
                            String lastname, String firstname, String street, String hnumber,
                            String zipcode, String location, String email) {

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String uri = URL + ACTION_REGISTER +
                    "/" + username + "/" + password + "/" + salutation +
                    "/" + lastname + "/" + firstname + "/" + street + "/" + hnumber +
                    "/" + zipcode + "/" + location + "/" + email;
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            inputStream = httpEntity.getContent();
        } catch (ClientProtocolException cpe) {
            Log.e("HTTPConnector: ", cpe.getMessage());
        } catch (IOException ioe) {
            Log.e("HTTPConnector: ", ioe.getMessage());
        }

        String json = null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("HTTPConnector: ", "Error converting result " + e.getMessage());
        }

        JSONObject jObj;

        // try parse the string to a JSON object
        try {
            jObj = new JSONArray(json).getJSONObject(0);
            if (jObj != null)
                return true;
            else
                return false;
        } catch (JSONException e) {
            Log.e("HTTPConnector: ", "Error parsing data " + e.getMessage());
        }

        return false;
    }
}
