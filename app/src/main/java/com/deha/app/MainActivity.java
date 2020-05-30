package com.deha.app;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.deha.app.databinding.ActivityMainBinding;
import com.deha.app.di.DI;
import com.deha.app.fragments.BroadcastMessageFragment;
import com.deha.app.fragments.CreateUserFragment;
import com.deha.app.fragments.DiscoverMapFragment;
import com.deha.app.fragments.HouseLocationFragment;
import com.deha.app.model.UserModel;
import com.deha.app.utils.FragmentUtils;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks, CreateUserFragment.CreateUserInterface {

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

    Toolbar toolbar = binding.toolbar;
    setSupportActionBar(toolbar);
    setDrawer();
    checkPermissions();
  }

  private void setDrawer() {
    DrawerLayout drawer = binding.drawerLayout;

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, binding.toolbar, R.string.nav_app_bar_open_drawer_description, R.string.navigation_drawer_close) {
      public void onDrawerClosed(View view) {
        supportInvalidateOptionsMenu();
        //drawerOpened = false;
      }

      public void onDrawerOpened(View drawerView) {
        supportInvalidateOptionsMenu();
        //drawerOpened = true;
      }
    };

    drawerToggle.setDrawerIndicatorEnabled(true);
    drawer.setDrawerListener(drawerToggle);
    drawerToggle.syncState();
  }

  private void startMesh() {
    final TextView textView = binding.logView;
    final ScrollView scrollView = binding.logScroll;
    final String[] logs = {""};
    DI.getP2pConnections().addLogListener((tag, message) -> {
      logs[0] = logs[0] + "\n" + tag + ": " + message;
      textView.setText(logs[0]);
      scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    });
  }

  private void checkPermissions() {
    if (EasyPermissions.hasPermissions(this, permissions)) {
      checkUser();
    } else {
      askForPermission();
    }
  }

  private void checkUser() {
    if (DI.getLocalStorageService().getUser() == null) {
      navigateToCreateUserFragment();
    } else {
      fetchUserAndStartMesh();
    }
  }

  private void fetchUserAndStartMesh() {
    user = DI.getLocalStorageService().getUser();
    startMesh();
    navigateToDiscoverMapFragment();
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
    FragmentUtils.replaceFragment(getSupportFragmentManager(),
        DiscoverMapFragment.newInstance(), R.id.container, "discovermap");
  }

  public void navigateToCreateUserFragment() {
    binding.progress.setVisibility(View.GONE);
    FragmentUtils.replaceFragment(getSupportFragmentManager(),
        CreateUserFragment.newInstance(), R.id.container, null);
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

  @Override
  public void onAttachFragment(Fragment fragment) {
    if (fragment instanceof CreateUserFragment) {
      CreateUserFragment createUserFragment = (CreateUserFragment) fragment;
      createUserFragment.setCreateUserInterface(this);
    }
  }

  @Override
  public void userCreated() {
    fetchUserAndStartMesh();
  }
}

