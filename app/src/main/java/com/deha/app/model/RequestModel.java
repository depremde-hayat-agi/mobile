package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.HashMap;
import java.util.List;

public class RequestModel {
  private String id;
  private double latitude;
  private double longitude;
  private HashMap<String, UserModel> helpMap;
  private HashMap<String, UserModel> iAmOkayMap;

  public RequestModel(String id, double latitude, double longitude, HashMap<String, UserModel> helpMap, HashMap<String, UserModel> iAmOkayMap) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.helpMap = helpMap;
    this.iAmOkayMap = iAmOkayMap;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public HashMap<String, UserModel> getHelpMap() {
    return helpMap;
  }

  public void setHelpMap(HashMap<String, UserModel> helpMap) {
    this.helpMap = helpMap;
  }

  public HashMap<String, UserModel> getiAmOkayMap() {
    return iAmOkayMap;
  }

  public void setiAmOkayMap(HashMap<String, UserModel> iAmOkayMap) {
    this.iAmOkayMap = iAmOkayMap;
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
