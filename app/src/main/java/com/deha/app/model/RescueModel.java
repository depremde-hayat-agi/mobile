package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.Objects;

public class RescueModel {
  private String id;
  private double latitude;
  private double longitude;
  private int timestamp;

  public RescueModel(String id, double latitude, double longitude, int timestamp) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.timestamp = timestamp;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public int getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(int timestamp) {
    this.timestamp = timestamp;
  }

  public String toJson() {
    return DI.getJsonUtils().toJson(this);
  }

  public static RescueModel fromJson(String json) {
    return DI.getJsonUtils().fromJson(json, RescueModel.class);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RescueModel that = (RescueModel) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
