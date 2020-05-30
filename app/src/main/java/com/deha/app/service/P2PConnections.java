package com.deha.app.service;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.deha.app.MainActivity;
import com.deha.app.model.MeshMessageModel;
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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class P2PConnections {


    public static final String SERVICE_ID = "DeHA2020";
    public static final String LOG_CONNECTION_TAG = "CONNECTION";
    public static final String LOG_NEW_PERSON_TAG = "NEW_PERSON";

    private Set<String> endPointIds;
    private Context context;
    private P2PListener p2pListener;
    private MeshMessageModel meshMessageModel;
    private HttpService httpService;
    private boolean lastListSentToServer = false;

    public P2PConnections(Context context, P2PListener p2pListener) {
        this.endPointIds = new HashSet<>();
        this.context = context;
        this.p2pListener = p2pListener;
        this.meshMessageModel = new MeshMessageModel(
                new HashMap<>(),
                new HashMap<>()
        );
        this.httpService = new HttpService();
    }

    public interface P2PListener{
        void log(String tag, String message);
    }

    private ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
            p2pListener.log(LOG_CONNECTION_TAG, "connection accepted with " + endpointId + " \n" );
            endPointIds.add(endpointId);
            sendMessageToSpecific(endpointId, meshMessageModel.toJson());
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
            p2pListener.log(LOG_CONNECTION_TAG, "onConnectionResult " + endpointId +  " " +
                    connectionResolution.getStatus() + " \n" );
        }
        @Override
        public void onDisconnected(@NonNull String endpointId) {
            endPointIds.remove(endpointId);
            p2pListener.log(LOG_CONNECTION_TAG, "connection disconnected with " + endpointId + " \n" );
        }
    };

    private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            p2pListener.log(LOG_CONNECTION_TAG, "endpoint found id: " + endpointId + " \n" );
            if(MainActivity.user.getId().compareTo(discoveredEndpointInfo.getEndpointName()) < 0){
                if(!endPointIds.contains(endpointId)) {
                    requestConnection(endpointId);
                }
                else{
                    p2pListener.log(LOG_CONNECTION_TAG, "already has connection with  " + endpointId + " \n" );
                }
            }
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            p2pListener.log(LOG_CONNECTION_TAG, "endpoint lost id: " + endpointId + " \n" );
        }
    };

    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            p2pListener.log(LOG_CONNECTION_TAG, "payload received from : " + endpointId + "\n " + new String(payload.asBytes()) + "  \n" );
            boolean changed = meshMessageModel.updateMaps(MeshMessageModel.fromJson(new String(payload.asBytes())));
            if(changed){
                performListChangedActions();
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };

    public void startAdvertising() {
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(context)
                .startAdvertising(
                        MainActivity.user.getId(), SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            p2pListener.log(LOG_CONNECTION_TAG, "advertising started \n" );
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            p2pListener.log(LOG_CONNECTION_TAG, "advertising failed \n" );
                        });
    }

    public void startDiscovery() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            p2pListener.log(LOG_CONNECTION_TAG, "discovery started \n" );
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            p2pListener.log(LOG_CONNECTION_TAG, "discovery failed \n" );
                        });
    }

    private void requestConnection(String endpointId){
        final Handler handler = new Handler(context.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                p2pListener.log(LOG_CONNECTION_TAG, "requestConnection to: " + endpointId + " \n" );
                Nearby.getConnectionsClient(context)
                        .requestConnection(MainActivity.user.getId(), endpointId, connectionLifecycleCallback)
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    p2pListener.log(LOG_CONNECTION_TAG, "requestConnection success " + endpointId + " \n " );
                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    if(!e.getMessage().contains("ALREADY_CONNECTED")){
                                        p2pListener.log(LOG_CONNECTION_TAG, "requestConnection failed to " + endpointId + " \n "
                                               + e.getMessage() + " \n");
                                    }
                                });
            }
        }, 3000);
    }

    public void sendMessage(String message){
        Payload streamPayload = Payload.fromBytes(message.getBytes());
        for(String endpointId: endPointIds){
            p2pListener.log(LOG_CONNECTION_TAG, "send payload to: " + endpointId + "\n message " + message + " \n" );
            Nearby.getConnectionsClient(context).sendPayload(endpointId, streamPayload);
        }
    }

    public void sendMessageToSpecific(String endpointId, String message){
        Payload streamPayload = Payload.fromBytes(message.getBytes());
        p2pListener.log(LOG_CONNECTION_TAG, "send payload to: " + endpointId + " message " + message + " \n" );
        Nearby.getConnectionsClient(context).sendPayload(endpointId, streamPayload);
    }

    public void addMyselfToMap(BroadcastType broadcastType){
        HashMap<String, UserModel> newHelpMep = new HashMap<>();
        HashMap<String, UserModel> newIAmOkayMap = new HashMap<>();
        MainActivity.user.setLastTimestamp(
                Long.toString(new Timestamp(System.currentTimeMillis()).getTime()));
        if(broadcastType == BroadcastType.HELP){
            newHelpMep.put(MainActivity.user.getId(), MainActivity.user);
        }
        else if(broadcastType == BroadcastType.I_AM_OKAY){
            newIAmOkayMap.put(MainActivity.user.getId(), MainActivity.user);
        }

        MeshMessageModel newMeshMessageModel = new MeshMessageModel(newHelpMep, newIAmOkayMap);
        boolean changed = meshMessageModel.updateMaps(newMeshMessageModel);
        if(changed){
            performListChangedActions();
        }
    }

    private void performListChangedActions(){
        sendMessage(meshMessageModel.toJson());
        lastListSentToServer = false;
        sendToServer();

        for(UserModel model: meshMessageModel.getHelpMap().values()){
            p2pListener.log(LOG_NEW_PERSON_TAG, "Help " + model.getName() + " \n");
        }

        for(UserModel model: meshMessageModel.getiAmOkayMap().values()){
            p2pListener.log(LOG_NEW_PERSON_TAG, "OK " + model.getName() + "\n");
        }

        p2pListener.log(LOG_NEW_PERSON_TAG, " \n");
    }

    private void sendToServer(){
        RequestModel requestModel = new RequestModel(MainActivity.user.getId(),
                                                    MainActivity.user.getLatitude(),
                                                    MainActivity.user.getLongitude(),
                                                    meshMessageModel.getHelpMap(),
                                                    meshMessageModel.getiAmOkayMap());

        httpService.sendInfo(requestModel, new ResponseInterface<ResponseModel>() {
            @Override
            public void onSuccess(ResponseModel response) {
                lastListSentToServer = true;
                for(UserModel model: meshMessageModel.getHelpMap().values()){
                    Log.d("DEHA Server", "Help " + model.getName() + " is sent to server");
                }
                for(UserModel model: meshMessageModel.getiAmOkayMap().values()){
                    Log.d("DEHA Server","OK " + model.getName() + " is sent to server");
                }

                Log.d("DEHA Server","------------------------------------------------");
            }

            @Override
            public void onError(String error) {
                lastListSentToServer = false;
            }
        });
    }

    public enum BroadcastType{
        HELP,
        I_AM_OKAY
    }
}
