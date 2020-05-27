package com.deha.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
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
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

  private ActivityMainBinding binding;
  public static final String TAG = "main_activity";
  public static final int LOCATION_REQUEST_ID = 1;
  private String endPointId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    binding.setLifecycleOwner(this);


    binding.buttonAdvertise.setOnClickListener(view -> startAdvertising());
    binding.buttonDiscover.setOnClickListener(view -> askPermission());
    binding.buttonSend.setOnClickListener(view -> sendPayload(binding.inputMessage.getText().toString()));

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
  }

  private void askPermission(){
      String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION};
      if (EasyPermissions.hasPermissions(this, perms)) {
          startDiscovery();
      } else {
          // Do not have permissions, request them now
          EasyPermissions.requestPermissions(this, "location permission",
                  LOCATION_REQUEST_ID, perms);
      }
  }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        if(requestCode == LOCATION_REQUEST_ID){
            startDiscovery();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if(requestCode == LOCATION_REQUEST_ID){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

  private void startAdvertising() {
    AdvertisingOptions advertisingOptions =
            new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
    Nearby.getConnectionsClient(this)
            .startAdvertising(
                    "mrc1", "1", new ConnectionLifecycleCallback() {
                      @Override
                      public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                          acceptConnection(endpointId);
                      }

                      @Override
                      public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
                        System.out.println();
                      }

                      @Override
                      public void onDisconnected(@NonNull String s) {
                        System.out.println();
                      }
                    }, advertisingOptions)
            .addOnSuccessListener(
                    (Void unused) -> {
                        System.out.println();
                    })
            .addOnFailureListener(
                    (Exception e) -> {
                        System.out.println();
                    });
  }

  private void acceptConnection(String endpointId){
      setEndPointId(endpointId);
      Nearby.getConnectionsClient(MainActivity.this).acceptConnection(endpointId, new PayloadCallback() {
          @Override
          public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
              binding.textMessage.setText(binding.textMessage.getText() + " " +
                      new String(payload.asBytes()));
          }
          @Override
          public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

          }
      });
  }

  private void startDiscovery() {
    DiscoveryOptions discoveryOptions =
            new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
    Nearby.getConnectionsClient(this)
            .startDiscovery("1", new EndpointDiscoveryCallback() {
              @Override
              public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                  requestConnectionAfterEndpointFound(endpointId);
              }

              @Override
              public void onEndpointLost(@NonNull String s) {
                System.out.println();
              }
            }, discoveryOptions)
            .addOnSuccessListener(
                    (Void unused) -> {
                      System.out.println();
                    })
            .addOnFailureListener(
                    (Exception e) -> {
                        System.out.println();
                    });
  }

  private void requestConnectionAfterEndpointFound(String endpointId){
      Nearby.getConnectionsClient(MainActivity.this)
              .requestConnection("mrc1", endpointId, new ConnectionLifecycleCallback() {
                  @Override
                  public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
                      acceptConnection(s);
                  }

                  @Override
                  public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
                      System.out.println();
                  }

                  @Override
                  public void onDisconnected(@NonNull String s) {
                      setEndPointId(null);
                  }
              })
              .addOnSuccessListener(
                      (Void unused) -> {
                          System.out.println();
                      })
              .addOnFailureListener(
                      (Exception e) -> {
                          System.out.println();
                      });
  }

  @Override
  public void onStop() {
    super.onStop();
  }

  private void sendPayload(String message){
      if(endPointId != null){
          Payload bytesPayload = Payload.fromBytes(message.getBytes());
          Nearby.getConnectionsClient(this).sendPayload(endPointId, bytesPayload);
      }

  }

    public void setEndPointId(String endPointId) {
        this.endPointId = endPointId;
    }
}

