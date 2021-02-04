package com.puppyland.mongnang.Pick;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puppyland.mongnang.DTO.PickDTO;
import com.puppyland.mongnang.PickDetailActivity;
import com.puppyland.mongnang.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Pickadapter extends RecyclerView.Adapter<Pickadapter.CustomViewHolder> {

    List<PickDTO> pickDTOList;

    public Pickadapter(List<PickDTO> pickDTOList) {
        this.pickDTOList = pickDTOList;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;
        TextView name;
        TextView content;
        TextView pstart;
        LinearLayout pickitemLinear;
        TextView pstar;
        RatingBar ratingBar;

        public CustomViewHolder(View view) {
            super(view);
            this.picture = view.findViewById(R.id.picture);
            this.name = view.findViewById(R.id.name);
            this.content = view.findViewById(R.id.content);
            this.pstart = view.findViewById(R.id.pstar);
            this.pickitemLinear = view.findViewById(R.id.pickitemLinear);
            this.pstar = view.findViewById(R.id.pstar);
            this.ratingBar = view.findViewById(R.id.ratingBar);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.picklist_item, viewGroup, false);

        Pickadapter.CustomViewHolder viewHolder = new Pickadapter.CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        PickDTO pickDTO = pickDTOList.get(position);
        Picasso.get().load("http://192.168.219.100:8092/upload/" + pickDTO.getPic1()).error(R.drawable.monglogo2).into(viewholder.picture);
        viewholder.name.setText(pickDTO.getTitle());
        viewholder.content.setText(pickDTO.getFcontent());
        viewholder.pstar.setText(" (" + Float.toString(pickDTO.getPstar()) + ")" + " " + pickDTO.getPcount() + "명");
        viewholder.ratingBar.setRating(pickDTO.getPstar());

        viewholder.pickitemLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PickDetailActivity.class);
                intent.putExtra("pickDTO", pickDTO);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != pickDTOList ? pickDTOList.size() : 0);
    }

}