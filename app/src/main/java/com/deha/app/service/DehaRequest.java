package com.deha.app.service;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class DehaRequest extends StringRequest {
  public DehaRequest(int method, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
    super(method, url, listener, errorListener);
  }

  public DehaRequest(String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
    super(url, listener, errorListener);
  }


}
