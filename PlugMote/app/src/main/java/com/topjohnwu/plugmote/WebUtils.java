package com.topjohnwu.plugmote;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class WebUtils {

    private static final String REQ_URL = "http://52.175.20.174:3000/";

    public static List<String> getMACList() {
        List<String> ret = new ArrayList<>();
        String res = WebService.request(REQ_URL + "list", WebService.GET);
        try {
            JSONArray array = new JSONArray(res);
            for (int i = 0; i < array.length(); i++) {
                ret.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void togglePower(boolean on, String mac) {
        WebService.request(REQ_URL + mac + "?status=" + (on ? "on" : "off"), WebService.POST);
    }

    public static Details getDetails(String mac) throws JSONException {
        return new Details(WebService.request(REQ_URL + "details/" + mac, WebService.GET));
    }

}
