package com.deha.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

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

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 11;
    public static final String DEHA_NEEDS_LOC_PERM = "DeHA'nın çalışabilmesi için telefonunuzun lokasyonuna ihtiyacı var. DeHA'yı kullanmak için bu izini vermeniz gerekiyor.";
    public static String userId;

    private boolean isSettingsTapped = false;
    private ActivityMainBinding binding;
    String[] fineLocationPerm = { Manifest.permission.ACCESS_FINE_LOCATION};

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

        userId = localStorageService.getUserId();

        if (EasyPermissions.hasPermissions(this, fineLocationPerm)) {
            navigateBroadcastMessageFragment();
        } else {
            askForLocationPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSettingsTapped){
            if (EasyPermissions.hasPermissions(this, fineLocationPerm)) {
                navigateBroadcastMessageFragment();
            } else {
                askForLocationPermission();
            }

        }
    }

    public void navigateBroadcastMessageFragment() {
        binding.progress.setVisibility(View.GONE);
        FragmentUtils.replaceFragment(getSupportFragmentManager(),
                BroadcastMessageFragment.newInstance(), R.id.container, "broadcastmessage");
    }

    public void navigateToHouseLocationFragment() {
        binding.progress.setVisibility(View.GONE);
        FragmentUtils.replaceFragment(getSupportFragmentManager(),
                HouseLocationFragment.newInstance(false, null), R.id.container, "householdinfo");
    }

    public void navigateToDiscoverMapFragment() {
        binding.progress.setVisibility(View.GONE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int index = fragmentManager.getBackStackEntryCount() - 1;
        FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(index);
        String tag = backEntry.getName();
        return fragmentManager.findFragmentByTag(tag);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            navigateBroadcastMessageFragment();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                if (!EasyPermissions.hasPermissions(this, fineLocationPerm)) {
                    isSettingsTapped = true;
                    new AppSettingsDialog.Builder(this)
                            .setRationale("DeHA'nın çalışabilmesi için telefonunuzun lokasyonuna ihtiyacı var.")
                            .setPositiveButton("Ayarlara git")
                            .setNegativeButton("İptal")
                            .setTitle("Uyarı").build().show();
                }
            }
            else{
                askForLocationPermission();
            }
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        askForLocationPermission();
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Handler handler = new Handler(getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        askForLocationPermission();
                    }
                }, 1000);
            }
        }).start();
    }

    private void askForLocationPermission(){
        EasyPermissions.requestPermissions(this, DEHA_NEEDS_LOC_PERM,
                LOCATION_PERMISSION_REQUEST_CODE, fineLocationPerm);

    }
}

