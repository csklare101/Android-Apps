package com.example.civiladvocacy;

import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CivicInformation {
    private static final String TAG = "CivicInformation";
    private static final String apikey = "AIzaSyA6CqCoZwottyKKO8AaIVpNM0ZWR2oUEk0";
    private static String urlCity = "https://www.googleapis.com/civicinfo/v2/representatives?key=" + apikey + "&address=";

    private static MainActivity mainActivity;
    private static RequestQueue queue;

    public static void downloadInformation(MainActivity mainAct, String address){
        mainActivity = mainAct;
        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(urlCity+address).buildUpon();
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> parseJSON(mainActivity, response.toString());

        Response.ErrorListener error =
                error1 -> downloadFailed(mainActivity);//Log.d(TAG, "Download Information Failed");

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        queue.add(jsonObjectRequest);
    }

    private static void downloadFailed(MainActivity ma){
        Log.d(TAG, "Download Information Failed");

        AlertDialog.Builder noInternet = new AlertDialog.Builder(ma);
        noInternet.setTitle("No Data Found");
        noInternet.setMessage("Civil officials information cannot be accesed!");
        noInternet.create().show();
    }

    private static void parseJSON(MainActivity ma, String s){
        try{
            JSONObject jObjMain = new JSONObject(s);

            JSONObject normalizedInput = jObjMain.getJSONObject("normalizedInput");
            String street = "";
            if(normalizedInput.has("line1")){
                street = normalizedInput.getString("line1");
            }

            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");

            JSONArray offices = jObjMain.getJSONArray("offices");

            HashMap<Integer, String> officalIndecies = new HashMap<>();
            String title;
            JSONArray indiciesJ;
            ArrayList<Integer> indicies = new ArrayList<>();
            for(int i = 0; i < offices.length(); i++) {
                JSONObject officesDesc = (JSONObject) offices.get(i);
                indicies.clear();
                title = officesDesc.getString("name");

                indiciesJ = (JSONArray) officesDesc.get("officialIndices");
                if (indiciesJ != null) {
                    for (int j = 0; j < indiciesJ.length(); j++) {
                        officalIndecies.put(indiciesJ.getInt(j), title);
                    }
                }
            }

            ArrayList<Official> officialArrayList = new ArrayList<>();

            JSONArray officials = jObjMain.getJSONArray("officials");
            String name = "";
            String office = "";
            String party = "";

            String fb = "";
            String twitter = "";
            String youtube = "";

            String address = "";
            String phone = "";
            String email = "";
            String website = "";
            String photoUrl = "";

            String testAd;
            String testCh;
            JSONObject test;
            for(int i = 0; i < officials.length(); i++) {
                test = (JSONObject) officials.get(i);
                Official of;

                name = test.getString("name");

                if(test.has("address")){
                    JSONArray ad = test.getJSONArray("address");
                    testAd = ad.getString(0);
                    testAd = testAd.replaceAll("[:,{}]", "");
                    testAd = testAd.replaceAll("\"", ":");
                    List<String> split = new ArrayList<String>(Arrays.asList(testAd.split(":")));
                    split.removeAll(Arrays.asList(""));
                    for (int sp = 1; sp < split.size(); sp += 2) {
                        if(!split.get(sp).equals("city") && !split.get(sp).equals("state")){
                            if (split.get(sp - 1).equals("state")) {
                                address += split.get(sp) + ", ";
                            } else if (sp + 1 == split.size()) {
                                address += split.get(sp);
                            } else {
                                address += split.get(sp) + " ";
                            }
                        }
                        else{
                            sp++;
                        }
                    }
                }
                party = test.getString("party");

                if(test.has("phones")) {
                    JSONArray ph = test.getJSONArray("phones");
                    phone = ph.getString(0);
                }
                if(test.has("urls")){
                    JSONArray ur = test.getJSONArray("urls");
                    website = ur.getString(0);
                }
                if(test.has("emails")){
                    JSONArray em = test.getJSONArray("emails");
                    email = em.getString(0);
                }
                if(test.has("photoUrl")){
                    photoUrl = test.getString("photoUrl");
                }
                if(test.has("channels")){
                    JSONArray cha = test.getJSONArray("channels");
                    for(int ch = 0; ch < cha.length(); ch++){
                        testCh = cha.getString(ch);
                        testCh = testCh.replaceAll("[:,{}]", "");
                        testCh = testCh.replaceAll("\"", ":");
                        List<String> splitCh = new ArrayList<String>(Arrays.asList(testCh.split(":")));
                        splitCh.removeAll(Arrays.asList(""));
                        System.out.println();
                        if(splitCh.get(1).equals("Twitter")){
                            twitter = splitCh.get(3);
                        }
                        else if(splitCh.get(1).equals("Facebook")){
                            fb = splitCh.get(3);
                        }
                        else if(splitCh.get(1).equals("YouTube")){
                            youtube = splitCh.get(3);
                        }
                    }
                }

                office = officalIndecies.get(i);
                of = new Official(name, office, party, fb, twitter, youtube, address, phone, email, website, photoUrl);
                officialArrayList.add(of);
                name = "";
                office = "";
                party = "";

                fb = "";
                twitter = "";
                youtube = "";

                address = "";
                phone = "";
                email = "";
                website = "";
                photoUrl = "";
            }

            String locString = "";
            if(!street.equals("")){
                locString += street + ", ";
            }
            if(!city.equals("")){
                locString += city + ", ";
            }
            if(!state.equals("")){
                locString += state;
            }
            if(!zip.equals("")){
                locString += " " + zip;
            }
            ma.setLocationText(city, locString);
            ma.addOfficials(officialArrayList);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
