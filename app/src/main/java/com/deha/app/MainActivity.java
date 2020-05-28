package com.deha.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import com.deha.app.databinding.ActivityMainBinding;
import com.deha.app.fragments.BroadcastMessageFragment;
import com.deha.app.fragments.DiscoverMapFragment;
import com.deha.app.fragments.HouseLocationFragment;
import com.deha.app.model.RequestModel;
import com.deha.app.model.UserModel;
import com.deha.app.utils.FragmentUtils;
import com.deha.app.utils.LocalStorageService;
import com.deha.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        LocalStorageService localStorageService = new LocalStorageService(getSharedPreferences(getPackageName(), MODE_PRIVATE));
        if (localStorageService.getUserId() == null) {
            localStorageService.setUserId(Utils.getUuid());
        }

        navigateToDiscoverMapFragment();
    }

    public void navigateBroadcastMessageFragment() {
        FragmentUtils.replaceFragment(getSupportFragmentManager(),
                BroadcastMessageFragment.newInstance(), R.id.container, "broadcastmessage");
    }

    public void navigateToHouseLocationFragment() {
        FragmentUtils.replaceFragment(getSupportFragmentManager(),
                HouseLocationFragment.newInstance(false, null), R.id.container, "householdinfo");
    }

    public void navigateToDiscoverMapFragment() {
        List<UserModel> users = new ArrayList<>();
        users.add(new UserModel("1", "akbas", 39.9209483, 32.8277882, 1));
        users.add(new UserModel("2", "akbas2", 39.9219483, 32.8277882, 1));
        users.add(new UserModel("3", "akbas3", 39.9229483, 32.8277882, 1));
        users.add(new UserModel("4", "akbas4", 39.9239483, 32.8277882, 1));
        RequestModel requestModel = new RequestModel("0", 39.9234809, 32.8197219, users);
        FragmentUtils.replaceFragment(getSupportFragmentManager(),
                DiscoverMapFragment.newInstance(requestModel.toJson()), R.id.container, "discovermap");
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int index = fragmentManager.getBackStackEntryCount() - 1;
        FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(index);
        String tag = backEntry.getName();
        return fragmentManager.findFragmentByTag(tag);
    }
}

