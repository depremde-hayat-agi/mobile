package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MeshMessageModel {

  private HashMap<String, UserModel> userModelMap;
  private HashMap<String, RescueModel> rescueMap;

  public MeshMessageModel(HashMap<String, UserModel> userModelMap, HashMap<String, RescueModel> rescueMap) {
    this.userModelMap = userModelMap;
    this.rescueMap = rescueMap;
  }

  public  List<UserModel> updateMap(MeshMessageModel newMeshMessageModel){
    HashMap<String, UserModel> newUserModelMap = newMeshMessageModel.getUserModelMap();
    List<UserModel> changedUsers = new ArrayList<>();

    for(UserModel model: newUserModelMap.values()){
      if(userModelMap.containsKey(model.getId())){
        if(model.getLastTimestamp().compareTo(userModelMap.get(model.getId()).getLastTimestamp()) > 0){
          userModelMap.put(model.getId(), model);
          changedUsers.add(model);
        }
      }
      else{
        userModelMap.put(model.getId(), model);
        changedUsers.add(model);
      }
    }
    return changedUsers;
  }



  private boolean updateRescueMap(HashMap<String, RescueModel> currentMap, HashMap<String, RescueModel> newMap){
    boolean changed = false;
    for(String key: newMap.keySet()){
      if (currentMap.containsKey(key) && currentMap.get(key).getTimestamp() < newMap.get(key).getTimestamp()) {
        currentMap.put(key, newMap.get(key));
        changed = true;
      }
    }

    return changed;
  }

  public HashMap<String, UserModel> getUserModelMap() {
    return userModelMap;
  }

  public void setUserModelMap(HashMap<String, UserModel> userModelMap) {
    this.userModelMap = userModelMap;
  }

  public HashMap<String, RescueModel> getRescueMap() {
    return rescueMap;
  }

  public void setRescueMap(HashMap<String, RescueModel> rescueMap) {
    this.rescueMap = rescueMap;
  }

  public String toJson() {
    return DI.getJsonUtils().toJson(this);
  }

  public static MeshMessageModel fromJson(String json) {
    return DI.getJsonUtils().fromJson(json, MeshMessageModel.class);
  }

}
