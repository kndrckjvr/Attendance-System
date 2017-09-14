package com.example.dev4.attendancesystem;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText name, email, dept, user, pass;
    String strName, strEmail, strDept, strUser, strPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = (EditText) findViewById(R.id.nameEt);
        email = (EditText) findViewById(R.id.emailEt);
        dept = (EditText) findViewById(R.id.deptEt);
        user = (EditText) findViewById(R.id.userEt);
        pass = (EditText) findViewById(R.id.passEt);
    }

    public void signUpProf(View view) {
        strName = name.getText().toString();
        strEmail = email.getText().toString();
        strDept = dept.getText().toString();
        strUser = user.getText().toString();
        strPass = pass.getText().toString();
        if(strName.isEmpty() || strDept.isEmpty() || strEmail.isEmpty() || strPass.isEmpty() || strUser.isEmpty()) {
            Snackbar.make(pass, "No Fields must be empty.", Snackbar.LENGTH_SHORT).show();
        } else {
            new signupProf().execute();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(SignUp.this, LoginActivity.class));
    }

    class signupProf extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SignUp.this);
            dialog.setMessage("Creating Account...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            finish();
            startActivity(new Intent(SignUp.this, LoginActivity.class).putExtra("signedup", true));
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(LoginActivity._URL+"insert_prof.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("user", strUser);
                cv.put("pass", strPass);
                cv.put("name", strName);
                cv.put("email", strEmail);
                cv.put("dept", strDept);
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
