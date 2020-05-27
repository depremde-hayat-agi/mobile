package com.deha.app;


import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.HashSet;
import java.util.Set;

public class P2PConnections {

    private Set<String> endPointIds;
    private Context context;

    public P2PConnections(Context context) {
        this.endPointIds = new HashSet<>();
        this.context = context;
    }


}
