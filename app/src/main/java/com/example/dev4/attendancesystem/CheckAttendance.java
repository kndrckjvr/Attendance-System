package com.example.dev4.attendancesystem;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class CheckAttendance extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ProfessorClass prof;
    String courseid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        prof = ProfessorClass.CURRENT_LOGON.get(0);

        View v = navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) v.findViewById(R.id.profPic);
        TextView profName = (TextView) v.findViewById(R.id.profName);
        TextView profEmail = (TextView) v.findViewById(R.id.profEmail);

        profName.setText(prof.getName());
        profEmail.setText(prof.getEmail());
        Picasso.with(this)
                .load(LoginActivity._URL+prof.getImage())
                .resize(50, 50)
                .centerCrop()
                .into(imageView);
        new checkAttendance().execute();
        new getStudents().execute();
        final NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        final ListView lv = (ListView) findViewById(R.id.courseView);
        final AlertDialog dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(CheckAttendance.this);
        builder.setMessage("Today is: " + new SimpleDateFormat("MMMM d, y").format(new Date()))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!nfc.isEnabled()) {
                            Snackbar.make(lv, "NFC is Disabled", Snackbar.LENGTH_SHORT).show();
                        } else {
                            new newAttendance().execute();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog = builder.create();
        lv.setAdapter(new CourseViewAdapter(this, R.layout.course_view_adapter, CourseClass.COURSE_LIST));
        lv.setEmptyView(findViewById(R.id.emptyView));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                courseid = CourseClass.COURSE_LIST.get(position).getId();
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, LoginActivity.class  ));
    }

    class getStudents extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        JSONObject jsonObject;
        JSONArray jsonArray;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CheckAttendance.this);
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
                URL url = new URL(LoginActivity._URL+"showstud.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

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
                jsonArray = jsonObject.getJSONArray("stud");
                int i = 0;
                while(jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    String number = jsonObject.getString("number");
                    String serial = jsonObject.getString("serial");
                    String image = jsonObject.getString("image");
                    StudentClass stud = new StudentClass(id, name, number, serial, image);
                    StudentClass.STUDENT_LIST.add(stud);
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class checkAttendance extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        JSONObject jsonObject;
        JSONArray jsonArray;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CheckAttendance.this);
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
                URL url = new URL(LoginActivity._URL+"showcourse.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("id", prof.getId());
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
                CourseClass.COURSE_LIST.clear();
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("course");
                int i = 0;
                while(jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    String desc = jsonObject.getString("desc");
                    String profid = jsonObject.getString("profid");
                    String section = jsonObject.getString("section");
                    CourseClass course = new CourseClass(id, name, desc, section, profid);
                    CourseClass.COURSE_LIST.add(course);
                    i++;
                }
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

    class newAttendance extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CheckAttendance.this);
            dialog.setMessage("Creating Attendance...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            startActivity(new Intent(CheckAttendance.this, CheckActivity.class));
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(LoginActivity._URL+"newattendance.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                StringBuilder sb = new StringBuilder();
                String str = "";
                while ((str=br.readLine()) != null) {
                    sb.append(str);
                }
                sb.append(courseid);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.checkAtt:
                break;
            case R.id.addStud:
                finish();
                startActivity(new Intent(this, AddStudent.class));
                break;
            case R.id.profile:
                finish();
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.logout:
                CourseClass.COURSE_LIST.clear();
                ProfessorClass.CURRENT_LOGON.clear();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
