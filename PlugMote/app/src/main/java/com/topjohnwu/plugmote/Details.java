package com.topjohnwu.plugmote;

import org.json.JSONException;
import org.json.JSONObject;

public class Details {
    public String temp;
    public String status;

    public Details(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        temp = jsonObject.getString("temp");
        status = jsonObject.getString("status").toUpperCase();
    }
}
