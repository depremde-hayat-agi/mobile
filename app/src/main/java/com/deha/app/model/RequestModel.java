package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.List;

public class RequestModel {
  private String id;
  private List<UserModel> positiveList;

  public RequestModel(String id, List<UserModel> positiveList) {
    this.id = id;
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

  public String toJson() {
    return DI.getJsonUtils().toJson(this);
  }
}
