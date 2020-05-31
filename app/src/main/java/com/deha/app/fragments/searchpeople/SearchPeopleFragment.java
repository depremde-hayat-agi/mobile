package com.deha.app.fragments.searchpeople;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deha.app.R;
import com.deha.app.databinding.FragmentSearchPeopleBinding;
import com.deha.app.di.DI;
import com.deha.app.model.MeshMessageModel;
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
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        setHasOptionsMenu(true);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_people, container, false);
        binding.recyclerViewSearch.setHasFixedSize(true);
        adapter = new SearchPeopleAdapter(new ArrayList<>(DI.getP2pConnections().getMeshMessageModel()
                .getUserModelMap().values()));
        DI.getP2pConnections().addMessageListener(this);
        binding.recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewSearch.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onMessageUpdated(MeshMessageModel model) {
        adapter.setUserModels(new ArrayList<>(DI.getP2pConnections().getMeshMessageModel()
                .getUserModelMap().values()));
        adapter.notifyDataSetChanged();
    }
}
