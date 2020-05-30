package com.deha.app.fragments.searchpeople;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deha.app.R;
import com.deha.app.databinding.FragmentSearchPeopleBinding;
import com.deha.app.di.DI;
import com.deha.app.model.MeshMessageModel;
import com.deha.app.model.UserAdapterModel;
import com.deha.app.model.UserModel;
import com.deha.app.service.BroadcastType;
import com.deha.app.service.P2PConnections;

import java.util.ArrayList;
import java.util.List;


public class SearchPeopleFragment extends Fragment implements P2PConnections.MessageUpdatedListener {

    private FragmentSearchPeopleBinding binding;
    private SearchPeopleAdapter adapter;

    public static SearchPeopleFragment newInstance() {
        return new SearchPeopleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_people, container, false);
        binding.recyclerViewSearch.setHasFixedSize(true);
        adapter = new SearchPeopleAdapter(getListFromMeshMessage(DI.getP2pConnections().getMeshMessageModel()));
        binding.recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewSearch.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onMessageUpdated(MeshMessageModel model) {
        adapter.setUserModels(getListFromMeshMessage(DI.getP2pConnections().getMeshMessageModel()));
    }

    private List<UserAdapterModel> getListFromMeshMessage(MeshMessageModel model){
        List<UserAdapterModel> userAdapterModels = new ArrayList<>();
        for(UserModel userModel: model.getHelpMap().values()){
            userAdapterModels.add(new UserAdapterModel(userModel, BroadcastType.HELP,
                    false));
        }

        for(UserModel userModel: model.getiAmOkayMap().values()){
            userAdapterModels.add(new UserAdapterModel(userModel, BroadcastType.I_AM_OKAY,
                    false));
        }


        return userAdapterModels;
    }

}
