package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.List;

public class MessageModel {
  private List<UserModel> safeList;
  private List<UserModel> helpList;

  public MessageModel(List<UserModel> safeList, List<UserModel> helpList) {
    this.safeList = safeList;
    this.helpList = helpList;
  }

  public List<UserModel> getSafeList() {
    return safeList;
  }

  public void setSafeList(List<UserModel> safeList) {
    this.safeList = safeList;
  }

  public List<UserModel> getHelpList() {
    return helpList;
  }

  public void setHelpList(List<UserModel> helpList) {
    this.helpList = helpList;
  }

  public static MessageModel fromJson(String json) {
    return DI.getJsonUtils().fromJson(json, MessageModel.class);
  }

  public String toJson() {
    return DI.getJsonUtils().toJson(this);
  }
}
