package com.deha.app.model;

import com.deha.app.di.DI;

import java.util.HashMap;

public class MeshMessageModel {

  private HashMap<String, UserModel> helpMap;
  private HashMap<String, UserModel> iAmOkayMap;

  public MeshMessageModel(HashMap<String, UserModel> helpMap, HashMap<String, UserModel> iAmOkayMap) {
    this.helpMap = helpMap;
    this.iAmOkayMap = iAmOkayMap;
  }

  public boolean updateMaps(MeshMessageModel newMeshMessageModel){
    boolean changed = false;
    changed = changed || updateMap(newMeshMessageModel.getHelpMap(), helpMap, iAmOkayMap);
    changed = changed || updateMap(newMeshMessageModel.getiAmOkayMap(), iAmOkayMap, helpMap);

    return changed;
  }

  private boolean updateMap(HashMap<String, UserModel> updateInfoMap, HashMap<String, UserModel> map, HashMap<String, UserModel> otherMap){
    boolean changed = false;
    for(String key: updateInfoMap.keySet()){

      UserModel updateModel = updateInfoMap.get(key);
      UserModel mapModel = map.get(key);
      UserModel otherMapModel = map.get(otherMap);

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

  public String toJson() {
    return DI.getJsonUtils().toJson(this);
  }

  public static MeshMessageModel fromJson(String json) {
    return DI.getJsonUtils().fromJson(json, MeshMessageModel.class);
  }

}
