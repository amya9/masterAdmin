package com.example.testme2admin.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testme2admin.CategoriesActivity;
import com.example.testme2admin.R;
import com.example.testme2admin.SetsActivity;
import com.example.testme2admin.models.CategoriesModel;
import com.example.testme2admin.models.MasterModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.ViewHolder> {
    private MasterAdapter.DeleteListener deleteListener;
    private List<MasterModel> masterModelList;

    //constructor for adapter
    public MasterAdapter(List<MasterModel> masterModelList , MasterAdapter.DeleteListener deleteListener) {
        this.masterModelList = masterModelList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public MasterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.master_item , parent ,false);
        return new MasterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MasterAdapter.ViewHolder holder, int position) {
        holder.setData(masterModelList.get(position).getUrl() , masterModelList.get(position).getName() ,masterModelList.get(position).getPlace(), masterModelList.get(position).getKey() , position);

    }

    @Override
    public int getItemCount() {
        return masterModelList.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circleIV;
        private TextView master_name;
        private TextView master_place;
        private ImageButton deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleIV = itemView.findViewById(R.id.master_image);
            master_name = itemView.findViewById(R.id.master_name);
            master_place = itemView.findViewById(R.id.master_place);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }
        private void setData(String url , final String name ,final String place , final String key , final int position){
            Glide.with(itemView.getContext()).load(url).into(circleIV);
            master_name.setText(name);
            master_place.setText(place);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setsIntent = new Intent(itemView.getContext() , CategoriesActivity.class);
                    setsIntent.putExtra("master_name" , name);
                    setsIntent.putExtra("master_place" , place);
                    setsIntent.putExtra("position" , position);
                    setsIntent.putExtra("master_key" , key);
                    itemView.getContext().startActivity(setsIntent);
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onDelete(key , position);
                }
            });
        }
    }

    public interface DeleteListener {
         void onDelete(String key , int position);
    }
}
