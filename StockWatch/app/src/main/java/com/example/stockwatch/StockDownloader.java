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

public class StockDownloader {
    private static final String TAG = "StockDownloader";


    private static final String apiKey = "pk_8bed539248f14e9581ea6fb8ceb53d86";
    private static MainActivity mainAct;
    private static RequestQueue queue;

    public static void downloadStockData(MainActivity mainActivity, String symb){
        mainAct = mainActivity;
        queue = Volley.newRequestQueue(mainAct);
        String url = "https://cloud.iexapis.com/stable/stock/" + symb + "/quote?token=" + apiKey;
        Uri.Builder buildUrl = Uri.parse(url).buildUpon();
        String urlBuild = buildUrl.build().toString();

        Response.Listener<JSONObject> listener = response -> parseJSON(mainActivity, response.toString());

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

        JsonObjectRequest jsonObjectRequest  =
                new JsonObjectRequest (Request.Method.GET, urlBuild, null, listener, error);

        queue.add(jsonObjectRequest);
    }

    public static void parseJSON(MainActivity ma, String s) {
        Stock stock;
        try {
            JSONObject jObj = new JSONObject(s);

            stock = new Stock(jObj.getString("symbol"), jObj.getString("companyName"),
                    jObj.getDouble("latestPrice"), jObj.getDouble("change"), jObj.getDouble("changePercent"));

            if (stock == null) {
                Log.d(TAG, "handleResults: Failure in data download");
                ma.downloadFailed();
                return;
            }
            ma.passStock(stock);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
