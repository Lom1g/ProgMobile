package com.uqac_8inf865.sysi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotViewHolder> {

    private ArrayList<Spot> spotArrayList;

    public SpotAdapter(ArrayList<Spot> spotArrayList){
        this.spotArrayList = spotArrayList;
    }

    @NonNull
    @Override
    public SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.spots_cardview, parent, false);
        return new SpotViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SpotViewHolder holder, int position) {
        Spot currentSpot = spotArrayList.get(position);
        holder.textViewHolder.setText(currentSpot.getTitle());
        holder.imageViewHolder.setImageBitmap(currentSpot.getBitmapArrayList().get(0));
    }

    @Override
    public int getItemCount() {
        return spotArrayList.size();
    }

    public static class SpotViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageViewHolder;
        public TextView textViewHolder;

        public SpotViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewHolder = itemView.findViewById(R.id.imageViewSpot);
            textViewHolder = itemView.findViewById(R.id.textViewSpot);
        }
    }
}
