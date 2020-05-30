package com.deha.app.di;

import com.deha.app.service.HttpService;
import com.deha.app.service.P2PConnections;
import com.deha.app.utils.JsonUtils;

public class DI {
  private static HttpService httpService;
  private static JsonUtils jsonUtils;
  private static P2PConnections p2pConnections;


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

  public static P2PConnections getP2pConnections() {
    if (p2pConnections == null) {
      p2pConnections = new P2PConnections();
    }
    return p2pConnections;
  }
}
