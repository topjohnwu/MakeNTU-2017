package com.topjohnwu.plugmote;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PlugMote";

    private List<String> mDevices;
    private Map<String, Details> detailsMap;
    private RecyclerView recyclerView;
    private TextView empty_rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        empty_rv = (TextView) findViewById(R.id.empty_rv);

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    mDevices = WebUtils.getMACList();
                    detailsMap = new HashMap<>();
                    for (String mac : mDevices) {
                        detailsMap.put(mac, WebUtils.getDetails(mac));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (mDevices.isEmpty()) {
                    empty_rv.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty_rv.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new DeviceAdapter(mDevices, detailsMap));
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
