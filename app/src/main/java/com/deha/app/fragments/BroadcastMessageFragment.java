package com.deha.app.fragments;


import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
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

    private FragmentBroadcastMessageBinding binding;
    private P2PConnections p2pConnections;

    public static BroadcastMessageFragment newInstance() {
        BroadcastMessageFragment fragment = new BroadcastMessageFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_broadcast_message, container, false);
        binding.setLifecycleOwner(this);

        p2pConnections = new P2PConnections(getContext(), new P2PConnections.P2PListener() {
            @Override
            public void log(String tag, String message) {
                if(tag.equals(P2PConnections.LOG_NEW_PERSON_TAG)){
                    binding.textMessage.setText(binding.textMessage.getText() + " " + message);
                }
            }
        });

        p2pConnections.startAdvertising();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                p2pConnections.startDiscovery();
            }
        }, 2000);

        binding.buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p2pConnections.addMyselfToMap(P2PConnections.BroadcastType.HELP);
            }
        });

        binding.buttonOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p2pConnections.addMyselfToMap(P2PConnections.BroadcastType.I_AM_OKAY);
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
