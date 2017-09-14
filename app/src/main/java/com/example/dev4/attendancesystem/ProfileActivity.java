package com.example.dev4.attendancesystem;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Map;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ProfessorClass prof;
    EditText username, password;
    String strUser, strPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(3).setChecked(true);

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

        username = (EditText) findViewById(R.id.usernameEt);
        password = (EditText) findViewById(R.id.passwordEt);
        TextView profname = (TextView) findViewById(R.id.nameTx);
        profname.setText(prof.getName());
        Picasso.with(this)
                .load(LoginActivity._URL+prof.getImage())
                .resize(50, 50)
                .centerCrop()
                .into((ImageView) findViewById(R.id.profImg));
        username.setText(prof.getUser());
        password.setText(prof.getPass());
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
                finish();
                startActivity(new Intent(this, CheckAttendance.class));
                break;
            case R.id.addStud:
                finish();
                startActivity(new Intent(this, AddStudent.class));
                break;
            case R.id.profile:
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

    public void updateProf(View view) {
        strUser = username.getText().toString();
        strPass = password.getText().toString();

        new updateProf().execute();
    }

    class updateProf extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ProfileActivity.this);
            dialog.setMessage("Processing...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Snackbar.make(password, "Profile has been updated!", Snackbar.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(password, "You will be logged out.", Snackbar.LENGTH_SHORT).show();
                }
            }, 400);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                }
            },1400);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(LoginActivity._URL+"updateprof.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("profid", prof.getId());
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
