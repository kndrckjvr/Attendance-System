package com.example.dev4.attendancesystem;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    public static String _URL = "http://192.168.43.189/attendance/";
    EditText user, pass;
    String strUser, strPass;
    SharedPreferences shared;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = (EditText) findViewById(R.id.userEt);
        pass = (EditText) findViewById(R.id.passEt);
        if(getIntent().hasExtra("signedup")) {
            Snackbar.make(pass, "Successfully Signed up.", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void loginProfessor(View view) {
        strUser = user.getText().toString();
        strPass = pass.getText().toString();

        new searchProf().execute();
    }

    public void signUpMe(View view) {
        finish();
        startActivity(new Intent(this, SignUp.class));
    }

    class searchProf extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        JSONObject jsonObject;
        JSONArray jsonArray;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Logging In...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            parseJSON(s);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(LoginActivity._URL+"searchprof.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("user", strUser);
                cv.put("pass", strPass);
                bw.write(createPostString(cv));
                bw.flush();
                bw.close();
                os.close();

                ResponseCode = con.getResponseCode();
                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                StringBuilder sb = new StringBuilder();
                String str = "";
                while ((str=br.readLine()) != null) {
                    sb.append(str);
                }

                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void parseJSON(String strJSON) {
            try {
                ProfessorClass.CURRENT_LOGON.clear();
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("prof");
                int i = 0;
                while(jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String user = jsonObject.getString("user");
                    String pass = jsonObject.getString("pass");
                    String name = jsonObject.getString("name");
                    String email = jsonObject.getString("email");
                    String dept = jsonObject.getString("dept");
                    String img = jsonObject.getString("img");
                    ProfessorClass prof = new ProfessorClass(id, name, email, dept, img, user, pass);
                    ProfessorClass.CURRENT_LOGON.add(prof);
                    i++;
                }
                if(ProfessorClass.CURRENT_LOGON.size() > 0) {
                    finish();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class).putExtra("FROM_LOGIN", true));
                } else
                    Snackbar.make(pass, "User not found.", Snackbar.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String createPostString(ContentValues cv) throws UnsupportedEncodingException {
            StringBuilder sb = new StringBuilder();
            boolean flag = true;

            for(Map.Entry<String, Object> v: cv.valueSet()) {
                if(flag)
                    flag = false;
                else
                    sb.append("&");
                sb.append(URLEncoder.encode(v.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(v.getValue().toString(), "UTF-8"));
            }
            return sb.toString();
        }
    }
}
