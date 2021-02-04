package com.puppyland.mongnang;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puppyland.mongnang.contract.ChatuserListContract;
import com.puppyland.mongnang.presenter.ChatuserListPresenter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends  RecyclerView.Adapter<ListViewAdapter.ViewHolder> implements ChatuserListContract.View {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    ChatuserListContract.Presenter chatuserListPresenter;
    List<String> mImages;

    @NonNull
    @Override
    public ListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        chatuserListPresenter = new ChatuserListPresenter(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewAdapter.ViewHolder viewHolder, int position) {
        ListViewItem item = listViewItemList.get(position);
        final  String a = item.getIcon();
        final  String b = item.getDogImage();
        mImages = new ArrayList<String>();
        mImages.add(a);
        mImages.add(b);
        viewHolder.carouselView.setPageCount(2);
        viewHolder.carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Picasso.get().load("http://192.168.219.100:8092/upload/" + mImages.get(position)).fit().centerCrop().into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.v("sdduccess", "성공");
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.d("Err", e.getMessage());
                    }
                });
            }
        });
        viewHolder.UserId.setText(item.getTitle());
        viewHolder.UserNickname.setText(item.getNickname());
        if(item.getDesc().equals("")){
            viewHolder.UserMsg.setText("/" + "...");
        }else{
            viewHolder.UserMsg.setText("/" + item.getDesc());
        }
        viewHolder.dogName.setText(item.getDogName());
        viewHolder.dogAge.setText(item.getDogAge()+"세");
        if(item.getDogGender().equals("m")){
            viewHolder.dogGender.setText("수컷");
        }
        else if(item.getDogGender().equals("f")){
            viewHolder.dogGender.setText("암컷");
        }


    }

    @Override
    public int getItemCount() {
        Log.v("sssize" , String.valueOf(listViewItemList.size()));
        return listViewItemList.size();
    }

    public void setItems(ArrayList<ListViewItem> items) {
        this.listViewItemList = items;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView UserMsg , dogName , dogAge , dogGender , UserId, UserNickname;
        CarouselView carouselView;
        LinearLayout selectedLinear;

        ViewHolder(View itemView) {
            super(itemView);

            UserId = itemView.findViewById(R.id.userId);
            UserNickname = itemView.findViewById(R.id.UserNickname);
            UserMsg = itemView.findViewById(R.id.userMsg);
            dogName = itemView.findViewById(R.id.dogname);
            dogAge = itemView.findViewById(R.id.dogAge);
            dogGender = itemView.findViewById(R.id.doggender);
            selectedLinear = itemView.findViewById(R.id.selectedLinear);
            carouselView = itemView.findViewById(R.id.carouselView);

            UserNickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("###", "EEE");
                    Intent intent = new Intent(v.getContext(), SelectedUserDiaryActivity.class);
                    intent.putExtra("selectedId" ,UserId.getText().toString());
                    intent.putExtra("selectedNickname",UserNickname.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}