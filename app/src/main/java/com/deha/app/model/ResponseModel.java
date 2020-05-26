package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.List;

public class ResponseModel {
  private List<String> knownList;

  public ResponseModel(List<String> knownList) {
    this.knownList = knownList;
  }

  public List<String> getKnownList() {
    return knownList;
  }

  public void setKnownList(List<String> knownList) {
    this.knownList = knownList;
  }

  public static ResponseModel fromJson(String json) {
    return DI.getJsonUtils().fromJson(json, ResponseModel.class);
  }
}
