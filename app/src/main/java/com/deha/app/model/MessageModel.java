package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.List;

public class MessageModel {
  private List<UserModel> positiveList;
  private List<String> negativeList;

  public MessageModel(List<UserModel> positiveList, List<String> negativeList) {
    this.positiveList = positiveList;
    this.negativeList = negativeList;
  }

  public List<UserModel> getPositiveList() {
    return positiveList;
  }

  public void setPositiveList(List<UserModel> positiveList) {
    this.positiveList = positiveList;
  }

  public List<String> getNegativeList() {
    return negativeList;
  }

  public void setNegativeList(List<String> negativeList) {
    this.negativeList = negativeList;
  }

  public static MessageModel fromJson(String json) {
    return DI.getJsonUtils().fromJson(json, MessageModel.class);
  }
}
