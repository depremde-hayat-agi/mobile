package com.deha.app.service;


import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.deha.app.App;
import com.deha.app.MainActivity;
import com.deha.app.di.DI;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class P2PConnections {


    public static final String SERVICE_ID = "DeHA2020";
    public static final String LOG_CONNECTION_TAG = "CONNECTION";
    public static final String LOG_NEW_PERSON_TAG = "NEW_PERSON";

    private Set<String> endPointIds;
    private Context context;
    private List<LogListener> logListeners = new ArrayList<>();
    private MeshMessageModel meshMessageModel;
    private HttpService httpService;
    private List<MessageUpdatedListener> devicesUpdatedListeners = new ArrayList<>();
    private boolean lastListSentToServer = false;


    public P2PConnections() {
        this.endPointIds = new HashSet<>();
        this.context = App.getContext();
        this.meshMessageModel = new MeshMessageModel(
                new HashMap<>(),
                new HashMap<>()
        );
        this.httpService = DI.getHttpService();
        startAdvertising();
    }

    private ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
            log(LOG_CONNECTION_TAG, "connection accepted with " + endpointId + "(" + Build.MODEL + ")" + " \n" );
            endPointIds.add(endpointId);
            sendMessageToSpecific(endpointId, meshMessageModel.toJson());
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
            log(LOG_CONNECTION_TAG, "onConnectionResult " + endpointId + "(" + Build.MODEL + ")" +  " " +
                    connectionResolution.getStatus() + " \n" );
        }
        @Override
        public void onDisconnected(@NonNull String endpointId) {
            endPointIds.remove(endpointId);
            log(LOG_CONNECTION_TAG, "connection disconnected with " + endpointId + "(" + Build.MODEL + ")" + " \n" );
        }
    };

    private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            log(LOG_CONNECTION_TAG, "endpoint found id: " + endpointId + "(" + Build.MODEL + ")" + " \n" );
            if(MainActivity.user.getId().compareTo(discoveredEndpointInfo.getEndpointName()) < 0){
                if(!endPointIds.contains(endpointId)) {
                    requestConnection(endpointId);
                }
                else{
                    log(LOG_CONNECTION_TAG, "already has connection with  " + endpointId + "(" + Build.MODEL + ")" + " \n" );
                }
            }
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            log(LOG_CONNECTION_TAG, "endpoint lost id: " + endpointId + "(" + Build.MODEL + ")" + " \n" );
        }
    };

    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            log(LOG_CONNECTION_TAG, "payload received from : " + endpointId + "(" + Build.MODEL + ")" + "\n " + new String(payload.asBytes()) + "  \n" );
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
                            log(LOG_CONNECTION_TAG, "advertising started \n" );
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startDiscovery();
                                }
                            }, 2000);
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            log(LOG_CONNECTION_TAG, "advertising failed \n" );
                        });
    }

    public void startDiscovery() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            log(LOG_CONNECTION_TAG, "discovery started \n" );
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            log(LOG_CONNECTION_TAG, "discovery failed \n" );
                        });
    }

    private void requestConnection(String endpointId){
        final Handler handler = new Handler(context.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                log(LOG_CONNECTION_TAG, "requestConnection to: " + endpointId + "(" + Build.MODEL + ")" + " \n" );
                Nearby.getConnectionsClient(context)
                        .requestConnection(MainActivity.user.getId(), endpointId, connectionLifecycleCallback)
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    log(LOG_CONNECTION_TAG, "requestConnection success " + endpointId + "(" + Build.MODEL + ")" + "\n " );
                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    if(!e.getMessage().contains("ALREADY_CONNECTED")){
                                        log(LOG_CONNECTION_TAG, "requestConnection failed to " + endpointId + "(" + Build.MODEL + ")" + " \n "
                                               + e.getMessage() + " \n");
                                    }
                                });
            }
        }, 3000);
    }

    public void sendMessage(String message){
        Payload streamPayload = Payload.fromBytes(message.getBytes());
        for(String endpointId: endPointIds){
            log(LOG_CONNECTION_TAG, "send payload to: " + endpointId + "(" + Build.MODEL + ")" + "\n message " + message + " \n" );
            Nearby.getConnectionsClient(context).sendPayload(endpointId, streamPayload);
        }
    }

    public void sendMessageToSpecific(String endpointId, String message){
        Payload streamPayload = Payload.fromBytes(message.getBytes());
        log(LOG_CONNECTION_TAG, "send payload to: " + endpointId + "(" + Build.MODEL + ")" + " message " + message + " \n" );
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
        for (MessageUpdatedListener listener : devicesUpdatedListeners) {
            listener.onMessageUpdated(meshMessageModel);
        }
        sendMessage(meshMessageModel.toJson());
        lastListSentToServer = false;
        sendToServer();

        for(UserModel model: meshMessageModel.getHelpMap().values()){
            log(LOG_NEW_PERSON_TAG, "Help " + model.getName() + " \n");
        }

        for(UserModel model: meshMessageModel.getiAmOkayMap().values()){
            log(LOG_NEW_PERSON_TAG, "OK " + model.getName() + "\n");
        }

        log(LOG_NEW_PERSON_TAG, " \n");
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
                    log("DEHA Server", "Help " + model.getName() + " is sent to server");
                }
                for(UserModel model: meshMessageModel.getiAmOkayMap().values()){
                    log("DEHA Server","OK " + model.getName() + " is sent to server");
                }

                log("DEHA Server","------------------------------------------------");
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
    
    private void log(String tag, String message) {
        Log.d(tag, message);
        for (LogListener logListener : logListeners) {
            logListener.log(tag, message);
        }
    }

    public void addLogListener(LogListener listener) {
        logListeners.add(listener);
    }

    public void removeLogListener(LogListener listener) {
        logListeners.remove(listener);
    }

    public void addMessageListener(MessageUpdatedListener listener) {
        devicesUpdatedListeners.add(listener);
    }

    public void removeMessageListener(MessageUpdatedListener listener) {
        devicesUpdatedListeners.remove(listener);
    }

    public interface MessageUpdatedListener {
        void onMessageUpdated(MeshMessageModel model);
    }

    public interface LogListener{
        void log(String tag, String message);
    }
}
