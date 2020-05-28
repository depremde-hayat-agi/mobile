package com.deha.app.service;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.deha.app.App;
import com.deha.app.model.RequestModel;
import com.deha.app.model.ResponseInterface;
import com.deha.app.model.ResponseModel;

import java.io.UnsupportedEncodingException;

public class HttpService {
  private final String TAG = "http_service";

  private final String URL = "http://www.mocky.io/v2/5ecd23e83200006600236731";
  private RequestQueue queue;

  public HttpService() {
    queue = Volley.newRequestQueue(App.getContext());
  }

  public void sendInfo(RequestModel request, ResponseInterface<ResponseModel> responseInterface) {
    final String requestString = request.toJson();
    Log.d(TAG, "Sending info:\n" + requestString);

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
          return requestString == null ? null : requestString.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
          Log.d(TAG, "Exception while getting body: " + uee.getMessage());
          uee.printStackTrace();
          return null;
        }
      }
    };

    queue.add(post);
  }
}
