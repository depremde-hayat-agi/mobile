package com.deha.app.model;

import com.deha.app.di.DI;
import com.deha.app.service.BroadcastType;

import java.util.Objects;

public class UserModel {
  private String id;
  private String name;
  private String mobilePhone;
  private double latitude;
  private double longitude;
  private int order;
  private String message;
  private String lastTimestamp;
  private BroadcastType broadcastType;

  public UserModel(UserModel model) {
    this.id = model.getId();
    this.name = model.getName();
    this.mobilePhone = model.getMobilePhone();
    this.latitude = model.getLatitude();
    this.longitude = model.getLongitude();
    this.order = model.getOrder();
    this.message = model.getMessage();
    this.lastTimestamp = model.getLastTimestamp();
    this.broadcastType = model.getBroadcastType();
  }


  public UserModel(String id, String name, String mobilePhone, double latitude, double longitude, int order, String message,
                   BroadcastType broadcastType) {
    this.id = id;
    this.name = name;
    this.mobilePhone = mobilePhone;
    this.latitude = latitude;
    this.longitude = longitude;
    this.order = order;
    this.message = message;
    this.broadcastType = broadcastType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobilePhone() {
    return mobilePhone;
  }

  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone = mobilePhone;
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

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getLastTimestamp() {
    return lastTimestamp;
  }

  public void setLastTimestamp(String lastTimestamp) {
    this.lastTimestamp = lastTimestamp;
  }


  public BroadcastType getBroadcastType() {
    return broadcastType;
  }

  public void setBroadcastType(BroadcastType broadcastType) {
    this.broadcastType = broadcastType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserModel userModel = (UserModel) o;
    return id.equals(userModel.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public String toJson() {
    return DI.getJsonUtils().toJson(this);
  }

  public static UserModel fromJson(String json) {
    return DI.getJsonUtils().fromJson(json, UserModel.class);
  }
}
