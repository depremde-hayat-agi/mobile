package com.deha.app.service;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.deha.app.App;
import com.deha.app.di.DI;
import com.deha.app.model.RequestModel;
import com.deha.app.model.ResponseInterface;
import com.deha.app.model.ResponseModel;
import com.deha.app.model.UserModel;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpService {
  private final String TAG = "http_service";

  private final String URL = "http://www.mocky.io/v2/5ecd23e83200006600236731";
  private RequestQueue queue;

  public HttpService() {
    queue = Volley.newRequestQueue(App.getContext());
  }

  public void sendInfo(RequestModel requestWithMaps, ResponseInterface<ResponseModel> responseInterface) {

    RequestModelWithLists requestModelWithLists = new RequestModelWithLists(
            requestWithMaps.getId(),
            requestWithMaps.getLatitude(),
            requestWithMaps.getLongitude(),
            new ArrayList<>(requestWithMaps.getHelpMap().values()),
            new ArrayList<>(requestWithMaps.getiAmOkayMap().values())
    );

    final String requestModelWithListsString = requestModelWithLists.toJson();
    Log.d(TAG, "Sending info:\n" + requestModelWithListsString);

    StringRequest post = new StringRequest(Request.Method.POST, URL, response -> {
      try {
        Log.d(TAG, "Response received:\n" + response);
        final ResponseModel responseModel = ResponseModel.fromJson(response);
        responseInterface.onSuccess(responseModel);
      } catch (Exception e) {
        responseInterface.onError("Error: " + e.getMessage());
        e.printStackTrace();
      }
    }, error -> {
      responseInterface.onError(error.getMessage());
      Log.d(TAG, "Response error:\n" + error.getMessage());
    }) {
      @Override
      public byte[] getBody() throws AuthFailureError {
        try {
          return requestModelWithListsString == null ? null : requestModelWithListsString.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
          Log.d(TAG, "Exception while getting body: " + uee.getMessage());
          uee.printStackTrace();
          return null;
        }
      }
    };

    queue.add(post);
  }

  public class RequestModelWithLists{
    private String id;
    private double latitude;
    private double longitude;
    private List<UserModel> helpMap;
    private List<UserModel> iAmOkayMap;

    public RequestModelWithLists(String id, double latitude, double longitude, List<UserModel> helpMap, List<UserModel> iAmOkayMap) {
      this.id = id;
      this.latitude = latitude;
      this.longitude = longitude;
      this.helpMap = helpMap;
      this.iAmOkayMap = iAmOkayMap;
    }

    public String toJson() {
      return DI.getJsonUtils().toJson(this);
    }
  }
}
