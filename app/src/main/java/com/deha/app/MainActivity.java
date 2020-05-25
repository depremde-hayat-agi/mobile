package com.deha.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.deha.app.databinding.ActivityMainBinding;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MessageListener messageListener;
    private Message message;
    public static final String TAG = "Nearby";

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

        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(message != null){
                    Nearby.getMessagesClient(MainActivity.this).unpublish(message);
                }

                message = new Message(binding.inputMessage.getText().toString().getBytes());
                Nearby.getMessagesClient(MainActivity.this).publish(message);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Nearby.getMessagesClient(MainActivity.this).subscribe(messageListener);

    }

    @Override
    public void onStop() {
        if(message != null){
            Nearby.getMessagesClient(MainActivity.this).unpublish(message);
        }

        if(messageListener != null){
            Nearby.getMessagesClient(this).unsubscribe(messageListener);
        }

        super.onStop();
    }
}

