package com.example.stockwatch;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;

public class NameDownloader {

    private static final String TAG = "NameDownloader";
    private static RequestQueue queue;
    private static final String apiKey = "pk_8bed539248f14e9581ea6fb8ceb53d86";
    private static final String sourceURL = "https://cloud.iexapis.com/stable/ref-data/symbols?token=" + apiKey;

    public static void downloadNames(MainActivity mainActivity){
        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildUrl = Uri.parse(sourceURL).buildUpon();
        String url = buildUrl.build().toString();

        Response.Listener<JSONArray> listener =
                response -> handleResults(mainActivity, response);

        Response.ErrorListener error = error1 -> {
            Log.d(TAG, "getSourceData: ");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getSourceData: " + jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        JsonArrayRequest jsonArrayRequest  =
                new JsonArrayRequest (Request.Method.GET, url, null, listener, error);

        queue.add(jsonArrayRequest);
    }

    private static void handleResults(MainActivity ma, JSONArray jObj){
        if (jObj == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            ma.downloadFailed();
            return;
        }

        final HashMap<String,String> stockInfo = parseJSON(jObj);
        if (stockInfo == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            ma.downloadFailed();
            return;
        }
        ma.updateData(stockInfo);

    }
    public static HashMap parseJSON(JSONArray jObj){
        HashMap<String, String> stockNames = new HashMap<String, String>();
        try{
            for(int i = 0; i < jObj.length(); i++){
                JSONObject stock = (JSONObject) jObj.get(i);
                String symbol = stock.getString("symbol");

                String name = stock.getString("name");

                stockNames.put(symbol, name);
            }
            return stockNames;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
