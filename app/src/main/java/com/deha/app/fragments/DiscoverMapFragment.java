package com.deha.app.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.deha.app.R;
import com.deha.app.databinding.FragmentDiscoverMapBinding;
import com.deha.app.model.RequestModel;
import com.deha.app.model.UserModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import java.util.ArrayList;
import java.util.List;


public class DiscoverMapFragment extends Fragment implements OnMapReadyCallback {

  public static final String MAP_DATA_KEY = "HAS_SAVED_LOC";


  private FragmentDiscoverMapBinding binding;
  private GoogleMap googleMap;
  private RequestModel data;

  public static DiscoverMapFragment newInstance(String data) {
    DiscoverMapFragment fragment = new DiscoverMapFragment();
    Bundle args = new Bundle();
    if (data != null) {
      args.putString(MAP_DATA_KEY, data);
    }

    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    final String dataString = getArguments().getString(MAP_DATA_KEY);
    data = RequestModel.fromJson(dataString);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover_map, container, false);
    binding.setLifecycleOwner(this);

    SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.discover_map);
    mapFrag.getMapAsync(this);
    setActions();
    return binding.getRoot();
  }

  private void showTooltip() {
    final ToolTipsManager toolTipsManager = new ToolTipsManager();
    ToolTip.Builder builder = new ToolTip.Builder(getContext(), binding.buttonHelp, binding.parent, "Yardıma mı ihtiyacınız var?", ToolTip.POSITION_ABOVE);
    builder.setGravity(ToolTip.GRAVITY_CENTER);
    builder.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
    toolTipsManager.show(builder.build());
  }

  private void setActions() {
    binding.buttonHelp.setOnClickListener(v -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setMessage("Yardım isteği göndermek ister misiniz?")
          .setPositiveButton("Evet", (dialog, id) -> {
          })
          .setNegativeButton("Hayır", (dialog, id) -> {
          });
      // Create the AlertDialog object and return it
      builder.create().show();

    });
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    this.googleMap = googleMap;
//        if (viewModel.getSavedHouseCoordinates().getValue() != null && !showUserLocation) {
//            setMarker(viewModel.getSavedHouseCoordinates().getValue());
//        }

    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.734802, 35.467987), 4.6f));

    setMarkers();
    showTooltip();
  }

  private void setMarkers() {
    // Uyarı sadece i am okeyler var
    List<Marker> markers = new ArrayList<>();
    if (data != null && data.getiAmOkayMap() != null) {
      for (UserModel user : data.getiAmOkayMap().values()) {
        Marker marker = googleMap.addMarker(new MarkerOptions()
            .position(new LatLng(user.getLatitude(), user.getLongitude()))
            .title(user.getName())
            .snippet("Yardım ediiinnnn"));
//        googleMap.setMyLocationEnabled(true);
        markers.add(marker);
      }

      googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(data.getLatitude(), data.getLongitude()), 14));
    }
//    if (marker == null) {
//      marker = googleMap.addMarker(new MarkerOptions().position(latLng));
//    }
//    marker.setPosition(latLng);
//    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
  }
}
