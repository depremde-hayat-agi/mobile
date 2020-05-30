package com.deha.app.fragments.searchpeople;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deha.app.R;
import com.deha.app.model.UserAdapterModel;
import com.deha.app.model.UserModel;
import com.deha.app.service.BroadcastType;

import java.util.List;

public class SearchPeopleAdapter extends RecyclerView.Adapter<SearchPeopleAdapter.SearchItemViewHolder> {

    List<UserAdapterModel> userModels;


    public SearchPeopleAdapter( List<UserAdapterModel> userModels ){
        this.userModels = userModels;
    }

    @NonNull
    @Override
    public SearchItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new SearchItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchItemViewHolder holder, int position) {
        holder.name.setText(userModels.get(position).getName());

        if(userModels.get(position).getBroadcastType() == BroadcastType.I_AM_OKAY){
            holder.icon.setImageResource(R.drawable.ic_check_white_24dp);
            holder.icon.setBackgroundResource(R.drawable.background_search_icon_check);
        }
        else{
            holder.icon.setImageResource(R.drawable.ic_warning_white_24dp);
            holder.icon.setBackgroundResource(R.drawable.background_search_icon_help);
        }

        if(userModels.get(position).isContact()){
            holder.goToLocation.setVisibility(View.VISIBLE);
        }
        else{
            holder.goToLocation.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public static class SearchItemViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public ImageView icon;
        private TextView goToLocation;

        public SearchItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.text_name);
            icon = itemView.findViewById(R.id.image_status);
            goToLocation = itemView.findViewById(R.id.text_go_to_location);
        }
    }
}
