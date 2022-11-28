package com.example.civiladvocacy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Official implements Comparable<Official>, Serializable {
    private final String name;
    private final String office;
    private final String party;

    private final String fb;
    private final String twitter;
    private final String youtube;

    private final String address;
    private final String phone;
    private final String email;
    private final String website;

    private final String photoUrl;

    public Official(String name, String office, String party, String fb, String twitter, String youtube, String address, String phone, String email, String website, String photoUrl) {
        this.name = name;
        this.office = office;
        this.party = party;
        this.fb = fb;
        this.twitter = twitter;
        this.youtube = youtube;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getOffice() {
        return office;
    }

    public String getParty() {
        return party;
    }

    public String getFb() {
        return fb;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }


    public JSONObject toJSON() throws JSONException {
        JSONObject jobj = new JSONObject();

        jobj.put("name", name);
        jobj.put("office", office);
        jobj.put("party", party);
        jobj.put("fb", fb);
        jobj.put("twitter", twitter);
        jobj.put("youtube", youtube);
        jobj.put("address", address);
        jobj.put("phone", phone);
        jobj.put("email", email);
        jobj.put("website", website);
        jobj.put("photoUrl", photoUrl);
        return jobj;
    }

    public static Official createFromJSON(JSONObject jobj) throws JSONException{
        String name = jobj.getString("name");
        String office = jobj.getString("office");
        String party = jobj.getString("party");
        String fb = jobj.getString("fb");
        String twitter = jobj.getString("twitter");
        String youtube = jobj.getString("youtube");
        String address = jobj.getString("address");
        String phone = jobj.getString("phone");
        String email = jobj.getString("email");
        String website = jobj.getString("website");
        String photoUrl = jobj.getString("photoUrl");
        return new Official(name, office, party, fb, twitter, youtube, address, phone, email, website, photoUrl);
    }


    @Override
    public int compareTo(Official official) {
        return official.name.compareTo(this.name);
    }
}
