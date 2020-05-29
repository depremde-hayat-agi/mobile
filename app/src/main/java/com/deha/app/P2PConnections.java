package com.deha.app;


import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

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

import java.util.HashSet;
import java.util.Set;

public class P2PConnections {

    public static final String SERVICE_ID = "DeHA2020";
    private Set<String> endPointIds;
    private Context context;
    private P2PListener p2pListener;

    public P2PConnections(Context context, P2PListener p2pListener) {
        this.endPointIds = new HashSet<>();
        this.context = context;
        this.p2pListener = p2pListener;
    }

    public interface P2PListener{
        void newMessageArrived(String message);
    }

    private ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {

            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
            p2pListener.newMessageArrived("connection accepted with " + endpointId + " \n" );
            endPointIds.add(endpointId);
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
            p2pListener.newMessageArrived("onConnectionResult " + endpointId +  " " +
                    connectionResolution.getStatus() + " \n" );
        }
        @Override
        public void onDisconnected(@NonNull String endpointId) {
            endPointIds.remove(endpointId);
            p2pListener.newMessageArrived("connection disconnected with " + endpointId + " \n" );
        }
    };

    private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            p2pListener.newMessageArrived("endpoint found id: " + endpointId + " \n" );
            if(MainActivity.userId.compareTo(discoveredEndpointInfo.getEndpointName()) < 0){
                if(!endPointIds.contains(endpointId)) {
                    requestConnection(endpointId);
                }
                else{
                    p2pListener.newMessageArrived("already has connection with  " + endpointId + " \n" );
                }
            }
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            p2pListener.newMessageArrived("endpoint lost id: " + endpointId + " \n" );
        }
    };

    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            p2pListener.newMessageArrived("payload received from : " + endpointId + " " +new String(payload.asBytes()) + "  \n" );
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
                        MainActivity.userId, SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            p2pListener.newMessageArrived("advertising started \n" );
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            p2pListener.newMessageArrived("advertising failed \n" );
                        });
    }

    public void startDiscovery() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            p2pListener.newMessageArrived("discovery started \n" );
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            p2pListener.newMessageArrived("discovery failed \n" );
                        });
    }

    private void requestConnection(String endpointId){


        final Handler handler = new Handler(context.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                p2pListener.newMessageArrived("requestConnection to: " + endpointId + " \n" );
                Nearby.getConnectionsClient(context)
                        .requestConnection(MainActivity.userId, endpointId, connectionLifecycleCallback)
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    p2pListener.newMessageArrived("requestConnection success " + endpointId + " \n " );
                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    if(!e.getMessage().contains("ALREADY_CONNECTED")){
                                        p2pListener.newMessageArrived("requestConnection failed to " + endpointId + " \n "
                                                + e.getMessage() + " \n");
                                    }
                                });
            }
        }, 3000);

    }

    public void sendMessage(String message){
        Payload streamPayload = Payload.fromBytes(message.getBytes());
        for(String endpointId: endPointIds){
            p2pListener.newMessageArrived("send payload to: " + endpointId + " message " + message + " \n" );
            Nearby.getConnectionsClient(context).sendPayload(endpointId, streamPayload);
        }
    }

}
