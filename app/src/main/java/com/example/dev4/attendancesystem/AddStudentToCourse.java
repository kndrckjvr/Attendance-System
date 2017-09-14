package com.example.dev4.attendancesystem;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.Map;

public class AddStudentToCourse extends AppCompatActivity {

    String studid;
    ListView lv;
    String courseid;
    boolean course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_to_course);

        if(getIntent().hasExtra("id")) {
            course = true;
            courseid = getIntent().getStringExtra("id");
        }


        lv = (ListView) findViewById(R.id.availView);
        new getStudents().execute();
    }

    public void stopAttendance(View view) {
        finish();
        startActivity(new Intent(AddStudentToCourse.this, HomeActivity.class));
    }
    @Override
    public void onBackPressed() {
        Snackbar.make(lv, "Press \"FINISH\" Button if you\'re done.", Snackbar.LENGTH_SHORT).show();
    }

    class getStudents extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        JSONObject jsonObject;
        JSONArray jsonArray;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddStudentToCourse.this);
            dialog.setMessage("Fetching Data...");
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
                URL url = new URL(LoginActivity._URL+"showstudent.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                if(course) {
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                    ContentValues cv = new ContentValues();
                    cv.put("courseid", courseid);
                    bw.write(createPostString(cv));
                    bw.flush();
                    bw.close();
                    os.close();
                }

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
                StudentClass.STUDENT_LIST.clear();
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("student");
                int i = 0;
                while(jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    String number = jsonObject.getString("studno");
                    String serial = jsonObject.getString("studseri");
                    String image = jsonObject.getString("image");
                    StudentClass stud = new StudentClass(id, name, number, serial, image);
                    StudentClass.STUDENT_LIST.add(stud);
                    i++;
                }

                lv.setAdapter(new StudentViewAdapter(AddStudentToCourse.this, R.layout.student_view_adapter, StudentClass.STUDENT_LIST));
                lv.setEmptyView(findViewById(R.id.emptyView));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        studid = StudentClass.STUDENT_LIST.get(position).getId();
                        new enrollStudent().execute();
                    }
                });
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

    class enrollStudent extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddStudentToCourse.this);
            dialog.setMessage("Processing...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            finish();
            if(course)
                startActivity(new Intent(AddStudentToCourse.this, AddStudentToCourse.class).putExtra("id", courseid));
            else
                startActivity(new Intent(AddStudentToCourse.this, AddStudentToCourse.class));
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url;
                if(!course)
                    url = new URL(LoginActivity._URL+"addstudent.php");
                else
                    url = new URL(LoginActivity._URL+"addstudenttocourse.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("id", studid);
                if(course)
                    cv.put("courseid", courseid);
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
