package com.topjohnwu.plugmote;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PlugMote";

    private List<String> devices;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                devices = WebUtils.getMACList();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                recyclerView.setAdapter(new DeviceAdapter(devices));
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
