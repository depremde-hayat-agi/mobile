package com.deha.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.deha.app.databinding.ActivityMainBinding;
import com.deha.app.di.DI;
import com.deha.app.model.RequestModel;
import com.deha.app.model.ResponseInterface;
import com.deha.app.model.ResponseModel;
import com.deha.app.model.UserModel;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;
  private MessageListener messageListener;
  private Message message;
  public static final String TAG = "main_activity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    binding.setLifecycleOwner(this);

    messageListener = new MessageListener() {
      @Override
      public void onFound(Message message) {
        binding.textMessage.setText(binding.textMessage.getText() + " " + new String(message.getContent()));
      }

      @Override
      public void onLost(Message message) {
        Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
      }
    };

    binding.buttonSend.setOnClickListener(view -> {
      if (message != null) {
        Nearby.getMessagesClient(MainActivity.this).unpublish(message);
      }

      message = new Message(binding.inputMessage.getText().toString().getBytes());
      Nearby.getMessagesClient(MainActivity.this).publish(message);
    });

    binding.buttonSendRemote.setOnClickListener(view -> {
      final List<UserModel> userList = new ArrayList<>();
      userList.add(new UserModel("2", "name 1", "1234", "5678", 0));
      userList.add(new UserModel("3", "name 3", "12134", "56738", 1));
      final RequestModel requestModel = new RequestModel("123", userList);
      DI.getHttpService().sendInfo(requestModel, new ResponseInterface<ResponseModel>() {
        @Override
        public void onSuccess(ResponseModel response) {
          Log.d(TAG, "Success from server");
        }

        @Override
        public void onError(String error) {
          Log.d(TAG, "Error from server: " + error);
        }
      });
    });
  }

  @Override
  public void onStart() {
    super.onStart();
    Nearby.getMessagesClient(MainActivity.this).subscribe(messageListener);

  }

  @Override
  public void onStop() {
    if (message != null) {
      Nearby.getMessagesClient(MainActivity.this).unpublish(message);
    }

    if (messageListener != null) {
      Nearby.getMessagesClient(this).unsubscribe(messageListener);
    }

    super.onStop();
  }
}

