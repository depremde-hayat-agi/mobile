package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.List;

public class RequestModel {
  private String id;
  private double latitude;
  private double longitude;
  private List<UserModel> positiveList;

  public RequestModel(String id, double latitude, double longitude, List<UserModel> positiveList) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.positiveList = positiveList;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<UserModel> getPositiveList() {
    return positiveList;
  }

  public void setPositiveList(List<UserModel> positiveList) {
    this.positiveList = positiveList;
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
