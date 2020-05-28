package com.deha.app.utils;

import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

public class LocalStorageService {

    public static final String USER_ID_KEY = "USER_ID";
    public static final String LOCATION_KEY = "LOCATION";

    private SharedPreferences sharedPreferences;


    public LocalStorageService(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setUserId(String userId){
        saveString(USER_ID_KEY, userId);
    }

    public String getUserId(){
        if(getString(USER_ID_KEY).equals("")){
            return null;
        }
        return getString(USER_ID_KEY);
    }

    public void setLocation(LatLng latLng){
        saveString(LOCATION_KEY, latLng.latitude + "," + latLng.longitude);
    }

    public LatLng getLocation(){
        if(getString(LOCATION_KEY).equals("")){
            return null;
        }

        String[] latLngArr = getString(LOCATION_KEY).split(",");
        return new LatLng(Double.parseDouble(latLngArr[0]), Double.parseDouble(latLngArr[1]));
    }

    private void saveString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getString(String key){
        return sharedPreferences.getString(key, "");
    }
}
