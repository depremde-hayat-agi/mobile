package com.deha.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import com.deha.app.databinding.ActivityMainBinding;


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

        navigateBroadcastMessageFragment();
    }

    public void navigateBroadcastMessageFragment() {
        FragmentUtils.replaceFragment(getSupportFragmentManager(),
                BroadcastMessageFragment.newInstance(), R.id.container, "householdinfo");
    }

    public void navigateToHouseLocationFragment() {
        FragmentUtils.replaceFragment(getSupportFragmentManager(),
                HouseLocationFragment.newInstance(false, null), R.id.container, "householdinfo");
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

