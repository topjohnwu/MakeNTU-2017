package com.topjohnwu.plugmote;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private static final String MAP_KEY = "namemap";

    private List<String> mDevices;
    private SharedPreferences prefs;
    private Map<String, String> nameMap;
    private Map<String, Details> detailsMap;
    private Gson gson;

    public DeviceAdapter(List<String> devices, Map<String, Details> details) {
        mDevices = devices;
        detailsMap = details;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        if (prefs == null) {
            gson = new Gson();
            prefs = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
            nameMap = gson.fromJson(prefs.getString(MAP_KEY, ""), new TypeToken<HashMap<String, String>>(){}.getType());
            if (nameMap == null) {
                nameMap = new HashMap<>();
            }
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String mac = mDevices.get(position);
        Details details = detailsMap.get(mac);
        String name = nameMap.get(mac);
        if (name == null) {
            for (int i = 1; ; ++i) {
                name = holder.itemView.getContext().getString(R.string.unknown_device, i);
                if (!nameMap.containsValue(name)) {
                    nameMap.put(mac, name);
                    prefs.edit().putString(MAP_KEY, gson.toJson(nameMap)).apply();
                    break;
                }
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.mExpanded) {
                    holder.collapse();
                } else {
                    holder.expand();
                }
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.edit_text, null);
                final EditText editText = (EditText) view.findViewById(R.id.edit_text);
                alert.setView(view);
                alert.setTitle(R.string.edit_name);
                alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString();
                        if (!TextUtils.isEmpty(name)) {
                            holder.deviceName.setText(name);
                            nameMap.put(mac, name);
                            prefs.edit().putString(MAP_KEY, gson.toJson(nameMap)).apply();
                        }
                    }
                });
                alert.setNegativeButton(R.string.cancel, null);
                alert.show();
            }
        });
        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AsyncTask<Void, Void, Boolean>() {
                    private Details details;
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            details = WebUtils.getDetails(mac);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return false;
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (!result) {
                            return;
                        }
                        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                        View view = LayoutInflater.from(v.getContext()).inflate(R.layout.details_dialog, null);
                        TextView macAddr = (TextView) view.findViewById(R.id.mac);
                        TextView temperature = (TextView) view.findViewById(R.id.temperature);
                        TextView status = (TextView) view.findViewById(R.id.status);
                        macAddr.setText(mac);
                        temperature.setText(details.temp + " °C");
                        status.setText(details.status);
                        alert.setView(view);
                        alert.setTitle(R.string.more_info);
                        alert.setPositiveButton(R.string.done, null);
                        alert.show();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });
        holder.masterSwitch.setOnCheckedChangeListener(null);
        holder.masterSwitch.setChecked(details.status.equals("ON"));
        holder.masterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        WebUtils.togglePower(isChecked, mac);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        holder.deviceName.setText(name);
        holder.macAddr.setText(mac);

    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout expandLayout;
        TextView deviceName;
        TextView macAddr;
        ImageView editButton;
        ImageView moreInfo;
        Switch masterSwitch;

        private ValueAnimator mAnimator;
        private boolean mExpanded = false;
        private static int expandHeight = 0;

        public ViewHolder(View itemView) {
            super(itemView);

            expandLayout = (LinearLayout) itemView.findViewById(R.id.expand_layout);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            macAddr = (TextView) itemView.findViewById(R.id.mac_addr);
            editButton = (ImageView) itemView.findViewById(R.id.edit);
            moreInfo = (ImageView) itemView.findViewById(R.id.more_info);
            masterSwitch = (Switch) itemView.findViewById(R.id.master_switch);

            expandLayout.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {

                        @Override
                        public boolean onPreDraw() {
                            if (expandHeight == 0) {
                                final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                                final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                                expandLayout.measure(widthSpec, heightSpec);
                                expandHeight = expandLayout.getMeasuredHeight();
                            }

                            expandLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                            expandLayout.setVisibility(View.GONE);
                            mAnimator = slideAnimator(0, expandHeight);
                            return true;
                        }

                    });
        }

        private void setExpanded(boolean expanded) {
            mExpanded = expanded;
            ViewGroup.LayoutParams layoutParams = expandLayout.getLayoutParams();
            layoutParams.height = expanded ? expandHeight : 0;
            expandLayout.setLayoutParams(layoutParams);
            expandLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
        }

        private void expand() {
            expandLayout.setVisibility(View.VISIBLE);
            mAnimator.start();
            mExpanded = true;
        }

        private void collapse() {
            if (!mExpanded) return;
            int finalHeight = expandLayout.getHeight();
            ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    expandLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationStart(Animator animator) {}

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
            mAnimator.start();
            mExpanded = false;
        }

        private ValueAnimator slideAnimator(int start, int end) {

            ValueAnimator animator = ValueAnimator.ofInt(start, end);

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = expandLayout.getLayoutParams();
                    layoutParams.height = value;
                    expandLayout.setLayoutParams(layoutParams);
                }
            });
            return animator;
        }
    }
}
