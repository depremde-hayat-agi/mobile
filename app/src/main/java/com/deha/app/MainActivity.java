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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

  public static final int PERMISSION_REQUEST_CODE = 11;
  public static final String DEHA_NEEDS_LOC_PERM = "DeHA'nın çalışabilmek için konum, medya ve rehber erişimine ihtiyacı var.";
  public static UserModel user;

  private boolean isSettingsTapped = false;
  private ActivityMainBinding binding;
  String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    binding.setLifecycleOwner(this);

    LocalStorageService localStorageService = new LocalStorageService(getSharedPreferences(getPackageName(), MODE_PRIVATE));
    if (localStorageService.getUser() == null) {
        UserModel userModel = new UserModel(
                Utils.getUuid(),
                "Miraç Aknar",
                "+905056486804",
                11,
                22,
                1,
                "Hiç fena değil"
        );

        localStorageService.setUser(userModel);
    }

    user = localStorageService.getUser();

    checkPermissions();
  }

  private void checkPermissions() {
    if (EasyPermissions.hasPermissions(this, permissions)) {
      navigateToDiscoverMapFragment();
    } else {
        askForPermission();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (isSettingsTapped) {
      isSettingsTapped = false;
      checkPermissions();
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
    HashMap<String, UserModel> iAmOkayMap = new HashMap<>();
    HashMap<String, UserModel> helpMap = new HashMap<>();
    helpMap.put("1", new UserModel("1", "akbas", "+905554443322", 41.052230, 29.023993, 1, ""));
    helpMap.put("2", new UserModel("2", "akbas2", "+905554443322",41.047230, 29.021993, 1, ""));
    helpMap.put("3", new UserModel("3", "akbas3", "+905554443322",41.056230, 29.027993, 1, ""));
    helpMap.put("4", new UserModel("4", "akbas4", "+905554443322",41.051230, 29.019993, 1, ""));
    RequestModel requestModel = new RequestModel("0",39.9234809, 32.8197219, helpMap, iAmOkayMap);
    FragmentUtils.replaceFragment(getSupportFragmentManager(),
        DiscoverMapFragment.newInstance(requestModel.toJson()), R.id.container, "discovermap");
  }

  @Override
  public void onBackPressed() {
    if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
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
    checkPermissions();
  }

  @Override
  public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    if (requestCode == PERMISSION_REQUEST_CODE) {
      if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
        if (!EasyPermissions.hasPermissions(this, permissions)) {
          isSettingsTapped = true;
          new AppSettingsDialog.Builder(this)
              .setRationale("DeHA'nın çalışabilmek için konum, medya ve rehber erişimine ihtiyacı var.")
              .setPositiveButton("Ayarlara git")
              .setNegativeButton("İptal")
              .setTitle("Uyarı").build().show();
        }
      } else {
          askForPermission();
      }
    }
  }

  @Override
  public void onRationaleAccepted(int requestCode) {
      askForPermission();
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
              askForPermission();
          }
        }, 1000);
      }
    }).start();
  }

  private void askForPermission() {
    EasyPermissions.requestPermissions(this, DEHA_NEEDS_LOC_PERM,
        PERMISSION_REQUEST_CODE, permissions);

  }
}

