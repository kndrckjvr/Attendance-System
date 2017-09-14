package com.example.dev4.attendancesystem;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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

public class CheckActivity extends AppCompatActivity {

    PendingIntent pendingIntent;
    NfcAdapter nfc;
    ListView lv;
    String hex = new String();
    String studid = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        lv = (ListView) findViewById(R.id.presView);
        nfc = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        new showPresent().execute();
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

            for(StudentClass stud: StudentClass.STUDENT_LIST) {
                if(stud.getSerial().equals(hex)) {
                    studid = stud.getId();
                    break;
                }
            }
            new studentPresent().execute();
        }
    }

    public void stopAttendance(View view) {
        finish();
    }

    private class showPresent extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        JSONObject jsonObject;
        JSONArray jsonArray;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CheckActivity.this);
            dialog.setMessage("Fetching Data...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            parseJSONPresent(s);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(LoginActivity._URL+"showpresreal.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
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

        void parseJSONPresent(String strJSON) {
            try {
                PresentClass.SHOW_PRESENT.clear();
                jsonObject = new JSONObject(strJSON);
                jsonArray = jsonObject.getJSONArray("present");
                int i = 0;
                while(jsonArray.length() > i) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String attenid = jsonObject.getString("attenid");
                    String studid = jsonObject.getString("studid");
                    String status = jsonObject.getString("status");
                    String courseid = jsonObject.getString("courseid");
                    PresentClass present = new PresentClass(id, attenid, studid, status, courseid);
                    PresentClass.SHOW_PRESENT.add(present);
                    i++;
                }

                lv.setAdapter(new PresentViewAdapter(CheckActivity.this, R.layout.present_view_adapter, PresentClass.SHOW_PRESENT));
                lv.setEmptyView(findViewById(R.id.emptyView));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class studentPresent extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CheckActivity.this);
            dialog.setMessage("Sending Data...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            finish();
            startActivity(new Intent(CheckActivity.this, CheckActivity.class));
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(LoginActivity._URL+"updatestud.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                ContentValues cv = new ContentValues();
                cv.put("id", studid);
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

    @Override
    public void onBackPressed() {
        Snackbar.make(lv, "Press \"FINISH\" Button if you\'re done.", Snackbar.LENGTH_SHORT).show();
    }
}
