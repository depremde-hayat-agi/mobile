package com.deha.app;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.deha.app.databinding.FragmentBroadcastMessageBinding;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

public class BroadcastMessageFragment extends Fragment {

    private MessageListener messageListener;
    private Message message;
    public static final String TAG = "Nearby";

    private FragmentBroadcastMessageBinding binding;

    public static BroadcastMessageFragment newInstance() {
        BroadcastMessageFragment fragment = new BroadcastMessageFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_broadcast_message, container, false);
        binding.setLifecycleOwner(this);

        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(message != null){
                    Nearby.getMessagesClient(getActivity()).unpublish(message);
                }

                message = new Message(binding.inputMessage.getText().toString().getBytes());
                Nearby.getMessagesClient(getActivity()).publish(message);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Nearby.getMessagesClient(getActivity()).subscribe(messageListener);

    }

    @Override
    public void onStop() {
        if(message != null){
            Nearby.getMessagesClient(getActivity()).unpublish(message);
        }

        if(messageListener != null){
            Nearby.getMessagesClient(getActivity()).unsubscribe(messageListener);
        }

        super.onStop();
    }

}
