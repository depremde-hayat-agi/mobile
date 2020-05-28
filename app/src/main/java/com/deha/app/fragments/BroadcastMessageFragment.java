package com.deha.app.fragments;


import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.deha.app.service.P2PConnections;
import com.deha.app.R;
import com.deha.app.databinding.FragmentBroadcastMessageBinding;

import pub.devrel.easypermissions.EasyPermissions;

public class BroadcastMessageFragment extends Fragment {

    public static final String TAG = "Nearby";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 11;

    private FragmentBroadcastMessageBinding binding;
    private P2PConnections p2pConnections;

    public static BroadcastMessageFragment newInstance() {
        BroadcastMessageFragment fragment = new BroadcastMessageFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION};

        EasyPermissions.requestPermissions(this, "fine_location",
                LOCATION_PERMISSION_REQUEST_CODE, perms);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_broadcast_message, container, false);
        binding.setLifecycleOwner(this);

        p2pConnections = new P2PConnections(getContext(), new P2PConnections.P2PListener() {
            @Override
            public void newMessageArrived(String message) {
                binding.textMessage.setText(binding.textMessage.getText() + " " + message);
            }
        });

        binding.buttonAdvertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p2pConnections.startAdvertising();
            }
        });

        binding.buttonDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                p2pConnections.startDiscovery();

            }
        });

        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p2pConnections.sendMessage(binding.inputMessage.getText().toString());
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
