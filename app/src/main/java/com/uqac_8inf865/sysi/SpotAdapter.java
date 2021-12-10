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
    private OnItemClickListener onItemClickListener;

    public SpotAdapter(ArrayList<Spot> spotArrayList){
        this.spotArrayList = spotArrayList;
    }

    @NonNull
    @Override
    public SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.spots_cardview, parent, false);
        return new SpotViewHolder(v, onItemClickListener);
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

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public static class SpotViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageViewHolder;
        public TextView textViewHolder;

        public SpotViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            imageViewHolder = itemView.findViewById(R.id.imageViewSpot);
            textViewHolder = itemView.findViewById(R.id.textViewSpot);
            itemView.setOnClickListener(view -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClickListener(position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClickListener(int position);
    }
}
