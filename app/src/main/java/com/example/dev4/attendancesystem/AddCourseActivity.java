package com.example.dev4.attendancesystem;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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

public class AddCourseActivity extends AppCompatActivity {

    EditText name, desc, sect;
    String strName, strDesc, strSect, strProf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        name = (EditText) findViewById(R.id.nameEt);
        desc = (EditText) findViewById(R.id.descEt);
        sect = (EditText) findViewById(R.id.sectEt);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(AddCourseActivity.this, HomeActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addCourse(View view) {
        strName = name.getText().toString();
        strDesc = desc.getText().toString();
        strSect = sect.getText().toString();
        strProf = ProfessorClass.CURRENT_LOGON.get(0).getId();

        new addCourse().execute();
    }

    class addCourse extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddCourseActivity.this);
            dialog.setMessage("Adding Course...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            finish();
            startActivity(new Intent(AddCourseActivity.this, AddStudentToCourse.class));
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(LoginActivity._URL+"insertcourse.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("name", strName);
                cv.put("desc", strDesc);
                cv.put("sect", strSect);
                cv.put("prof", strProf);
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
