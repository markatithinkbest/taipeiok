package com.ithinkbest.taipeiok;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class GcmRegister extends Activity implements View.OnClickListener {
    static String LOG_TAG="MARK987";
    Button btnRegId;
    EditText etRegId;
    GoogleCloudMessaging gcm;
    String regid;

    //String PROJECT_NUMBER = "102488860000";// from demo
    String PROJECT_NUMBER = "538682377549";// Project ID: taipei-ok Project Number: 538682377549


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcm_register);

        btnRegId = (Button) findViewById(R.id.btnGetRegId);
        etRegId = (EditText) findViewById(R.id.etRegId);

        btnRegId.setOnClickListener(this);
    }
    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
              //      Toast.makeText(getApplicationContext(), "One time only, to send registration ID to App server, "+regid,Toast.LENGTH_SHORT).show();
                    Log.i(LOG_TAG, msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);
    }
    @Override
    public void onClick(View v) {
        getRegId();
    } }
