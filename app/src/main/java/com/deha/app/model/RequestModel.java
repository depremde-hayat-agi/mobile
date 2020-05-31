package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.HashMap;
import java.util.List;

public class RequestModel {
  private String id;
  private double latitude;
  private double longitude;
  private HashMap<String, UserModel> userModelMap;

  public RequestModel(String id, double latitude, double longitude, HashMap<String, UserModel> userModelMap) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.userModelMap = userModelMap;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public HashMap<String, UserModel> getUserModelMap() {
    return userModelMap;
  }

  public void setUserModelMap(HashMap<String, UserModel> userModelMap) {
    this.userModelMap = userModelMap;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String toJson() {
    return DI.getJsonUtils().toJson(this);
  }

  public static RequestModel fromJson(String json) {
    return DI.getJsonUtils().fromJson(json, RequestModel.class);
  }
}
