package com.deha.app.model;

import java.util.List;

public class MeshMessageModel {
  private List<UserModel> positiveList;
  private List<String> negativeList;

  public MeshMessageModel(List<UserModel> positiveList, List<String> negativeList) {
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
}
