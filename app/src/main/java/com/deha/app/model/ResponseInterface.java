package com.deha.app.model;

public interface ResponseInterface<T> {
  void onSuccess(T response);
  void onError(String error);
}
