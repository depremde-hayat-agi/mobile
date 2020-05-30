package com.deha.app.utils;

import android.content.SharedPreferences;

import com.deha.app.model.UserModel;
import com.google.android.gms.maps.model.LatLng;

public class LocalStorageService {

    public static final String USER_KEY = "USER";
    public static final String LOCATION_KEY = "LOCATION";

    private SharedPreferences sharedPreferences;

    public LocalStorageService(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setUser(UserModel user){
        saveString(USER_KEY, user.toJson());
    }

    public UserModel getUser(){
        if(getString(USER_KEY).equals("")){
            return null;
        }
        return UserModel.fromJson(getString(USER_KEY));
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
