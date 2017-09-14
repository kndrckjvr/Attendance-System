package com.example.dev4.attendancesystem;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
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

public class AddStudent extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    PendingIntent pendingIntent;
    ProfessorClass prof;
    String strName, strSerial, strNumber, hex = new String();
    EditText name, serial, number;
    NfcAdapter nfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);

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


        name = (EditText) findViewById(R.id.studNameEt);
        number = (EditText) findViewById(R.id.studNumberEt);
        serial = (EditText) findViewById(R.id.studSerialEt);
        serial.setEnabled(false);
        ImageView profpic = (ImageView) findViewById(R.id.studImg);

        Picasso.with(this)
                .load(LoginActivity._URL+prof.getImage())
                .resize(150, 150)
                .centerCrop()
                .into(profpic);

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfc = NfcAdapter.getDefaultAdapter(this);
        if(!nfc.isEnabled())
            Snackbar.make(serial, "NFC is disabled!", Snackbar.LENGTH_SHORT).show();
        if(getIntent().hasExtra("ADDED"))
            Snackbar.make(serial, "Student has been added.", Snackbar.LENGTH_SHORT).show();

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

    public void addStud(View view) {
        strName = name.getText().toString();
        strNumber = number.getText().toString();
        strSerial = serial.getText().toString();
        if(strSerial.isEmpty()) {
            Snackbar.make(view, "Please Tap Student's ID", Snackbar.LENGTH_SHORT).show();
        } else {
            new addStudent().execute();
        }
    }

    class addStudent extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        JSONObject jsonObject;
        JSONArray jsonArray;
        int ResponseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddStudent.this);
            dialog.setMessage("Processing...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(ResponseCode == 200) {
                finish();
                startActivity(new Intent(AddStudent.this, AddStudent.class).putExtra("ADDED",true));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(LoginActivity._URL+"addstud.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("name", strName);
                cv.put("serial", strSerial);
                cv.put("num", strNumber);
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

    public void HandleIntent(Intent intent) {
        String action = intent.getAction();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            hex = new String();
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            for(int i = 0; i < id.length; i++) {
                String x = Integer.toHexString(((int) id[i] & 0xff));
                if(x.length() == 1) {
                    x = '0' + x;
                }
                hex += x;
            }
            hex = hex.toUpperCase();
            serial.setText(hex);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfc.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfc.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        HandleIntent(intent);
    }
}
