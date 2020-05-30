package com.deha.app.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.deha.app.R;
import com.deha.app.databinding.FragmentBroadcastMessageBinding;
import com.deha.app.di.DI;
import com.deha.app.service.P2PConnections;

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

        p2pConnections = DI.getP2pConnections();

        p2pConnections.startAdvertising();

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
