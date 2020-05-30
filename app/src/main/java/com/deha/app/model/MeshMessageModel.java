package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.HashMap;

public class MeshMessageModel {

  private HashMap<String, UserModel> helpMap;
  private HashMap<String, UserModel> iAmOkayMap;
  private HashMap<String, RescueModel> rescueMap;

  public MeshMessageModel(HashMap<String, UserModel> helpMap, HashMap<String, UserModel> iAmOkayMap, HashMap<String, RescueModel> rescueMap) {
    this.helpMap = helpMap;
    this.iAmOkayMap = iAmOkayMap;
    this.rescueMap = rescueMap;
  }

  public boolean updateMaps(MeshMessageModel newMeshMessageModel){
    boolean changed = false;
    changed = changed || updateMap(newMeshMessageModel.getHelpMap(), helpMap, iAmOkayMap);
    changed = changed || updateMap(newMeshMessageModel.getiAmOkayMap(), iAmOkayMap, helpMap);
    changed = changed || updateRescueMap(rescueMap, newMeshMessageModel.rescueMap);

    return changed;
  }

  private boolean updateMap(HashMap<String, UserModel> updateInfoMap, HashMap<String, UserModel> map, HashMap<String, UserModel> otherMap){
    boolean changed = false;
    for(String key: updateInfoMap.keySet()){

      UserModel updateModel = updateInfoMap.get(key);
      UserModel mapModel = map.get(key);
      UserModel otherMapModel = otherMap.get(key);

      if(mapModel == null || mapModel.getLastTimestamp().compareTo(updateModel.getLastTimestamp()) < 0){
        changed = true;
        map.put(key, updateModel);
        mapModel = map.get(key);
      }

      if(otherMapModel != null && otherMapModel.getLastTimestamp().compareTo(mapModel.getLastTimestamp()) < 0){
        changed = true;
        map.remove(key);
      }
    }

    return changed;
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
