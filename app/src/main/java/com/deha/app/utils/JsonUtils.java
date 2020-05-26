package com.deha.app.utils;

import com.google.gson.Gson;

public class JsonUtils {

  private Gson gson;

  public JsonUtils() {
    gson = new Gson();
  }

  public String toJson(Object object) {
    return gson.toJson(object);
  }

  public <T> T fromJson(String json, Class<T> classOfT) {
    return gson.fromJson(json, classOfT);
  }
}
