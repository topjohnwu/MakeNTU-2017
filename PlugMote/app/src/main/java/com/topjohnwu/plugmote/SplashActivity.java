package com.topjohnwu.plugmote;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context self = this;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    MainActivity.mDevices = WebUtils.getMACList();
                    MainActivity.detailsMap = new HashMap<>();
                    for (String mac : MainActivity.mDevices) {
                        MainActivity.detailsMap.put(mac, WebUtils.getDetails(mac));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                Intent intent = new Intent(self, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
