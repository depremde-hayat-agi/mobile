package com.deha.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.deha.app.databinding.FragmentHouseLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class HouseLocationFragment extends Fragment implements OnMapReadyCallback {

    public static final String LAT_KEY = "LAT";
    public static final String LNG_KEY = "LNG";
    public static final String HAS_SAVED_LOC_KEY = "HAS_SAVED_LOC";


    private FragmentHouseLocationBinding binding;
    private GoogleMap googleMap;
    private boolean hasSavedLoc;
    private LatLng savedLatLng;
    BottomSheetBehavior bottomSheetBehavior;
    Marker marker;

    public static HouseLocationFragment newInstance(boolean hasSavedLoc, LatLng savedLatLng) {
        HouseLocationFragment fragment = new HouseLocationFragment();
        Bundle args = new Bundle();
        if(savedLatLng != null){
            args.putBoolean(HAS_SAVED_LOC_KEY, hasSavedLoc);
            if(hasSavedLoc){
                args.putDouble(LAT_KEY, savedLatLng.latitude);
                args.putDouble(LNG_KEY, savedLatLng.longitude);
            }
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        hasSavedLoc = getArguments().getBoolean(HAS_SAVED_LOC_KEY);
        if(hasSavedLoc){
            savedLatLng = new LatLng(getArguments().getDouble(LAT_KEY), getArguments().getDouble(LNG_KEY));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_house_location, container, false);
        binding.setLifecycleOwner(this);
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetSave);

        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        return binding.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
//        if (viewModel.getSavedHouseCoordinates().getValue() != null && !showUserLocation) {
//            setMarker(viewModel.getSavedHouseCoordinates().getValue());
//        }

        if(hasSavedLoc){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(savedLatLng, 14));
        }
        else{
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(38.734802, 35.467987), 4.6f));
        }


        googleMap.setOnMapLongClickListener(this::setMarker);
    }

    private void setMarker(LatLng latLng) {
        if (marker == null) {
            marker = googleMap.addMarker(new MarkerOptions().position(latLng));
        }
        marker.setPosition(latLng);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
