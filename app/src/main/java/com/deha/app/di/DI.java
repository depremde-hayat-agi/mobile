package com.deha.app.di;

import com.deha.app.service.HttpService;
import com.deha.app.utils.JsonUtils;

public class DI {
  private static HttpService httpService;
  private static JsonUtils jsonUtils;


  public static JsonUtils getJsonUtils() {
    if (jsonUtils == null) {
      jsonUtils = new JsonUtils();
    }
    return jsonUtils;
  }

  public static HttpService getHttpService() {
    if (httpService == null) {
      httpService = new HttpService();
    }
    return httpService;
  }
}
