package com.example.dev4.attendancesystem;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

public class AttendanceList extends AppCompatActivity {

    public static int index;
    private CourseClass course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_list);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if(getIntent().hasExtra("index"))
            index = getIntent().getIntExtra("index", 0);
        course = CourseClass.COURSE_LIST.get(index);

        TextView name = (TextView) findViewById(R.id.studNameTx);
        TextView desc = (TextView) findViewById(R.id.descTx);
        TextView sect = (TextView) findViewById(R.id.sectTx);

        name.setText(course.getName());
        desc.setText(course.getDesc());
        sect.setText(course.getSection());

        new getAttendance().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attlist_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(AttendanceList.this, HomeActivity.class));
                break;
            case R.id.stud:
                finish();
                startActivity(new Intent(AttendanceList.this, StudentList.class));
                break;
            case R.id.addStud:
                finish();
                startActivity(new Intent(AttendanceList.this, AddStudentToCourse.class).putExtra("id", course.getId()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class getAttendance extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        JSONObject jsonObject;
        JSONArray jsonArray;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AttendanceList.this);
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
                URL url = new URL(LoginActivity._URL+"showattendance.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("courseid", course.getId());
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
                AttendanceClass.SHOW_ATTENDANCE.clear();
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("attendance");
                int i = 0;
                while(jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String courseid = jsonObject.getString("courseid");
                    String date = jsonObject.getString("date");
                    AttendanceClass atten = new AttendanceClass(id, courseid, date);
                    AttendanceClass.SHOW_ATTENDANCE.add(atten);
                    i++;
                }

                ListView lv = (ListView) findViewById(R.id.attenView);
                lv.setAdapter(new AttendanceViewAdapter(AttendanceList.this,
                        R.layout.atten_view_adapter, AttendanceClass.SHOW_ATTENDANCE));
                lv.setEmptyView(findViewById(R.id.emptyView));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(new Intent(AttendanceList.this, PresentList.class)
                                .putExtra("index", position));
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
}
