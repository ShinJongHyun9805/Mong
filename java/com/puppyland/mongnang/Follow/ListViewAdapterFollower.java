package com.puppyland.mongnang.Follow;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.FollowDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.SelectedUserDiaryActivity;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

public class ListViewAdapterFollower extends BaseAdapter {

    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<FollowDTO> listViewItemList = new ArrayList<FollowDTO>();

    //ListViewAdapter의 생성자
    public ListViewAdapterFollower(){

    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();

        //listView_item Layout을 inflate하여 ConvertView 참조 획득

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.followerlistview_item, parent, false);


        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView Followernickname = (TextView) convertView.findViewById(R.id.Followernickname);
        TextView FollowerId= (TextView) convertView.findViewById(R.id.FollowerId);
        LinearLayout follwerLinear = convertView.findViewById(R.id.follwerLinear);
        CircleImageView userProfileImage = convertView.findViewById(R.id.userProfileImage);


        FollowDTO listViewItem = listViewItemList.get(position);
        Followernickname.setText(listViewItem.getNickname());
        FollowerId.setText(listViewItem.getUserid());

        MemberDTO prodto = new MemberDTO();
        prodto.setUserId(listViewItem.getUserid());
        Gson progson = new Gson();
        String proobjJson = progson.toJson(prodto); // DTO 객체를 json 으로 변환
        Call<MemberDTO> getMyInfo = NetRetrofit.getInstance().getService().fileName1(proobjJson);
        getMyInfo.enqueue(new retrofit2.Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                Picasso.get().load("http://192.168.219.100:8092/upload/" + response.body().getMemberimage()).error(R.drawable.monglogo2).fit().into(userProfileImage);
            }
            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {

            }
        });


        follwerLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SelectedUserDiaryActivity.class);
                intent.putExtra("selectedId" ,FollowerId.getText().toString());
                intent.putExtra("selectedNickname",Followernickname.getText().toString());
                v.getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    //지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    //지정된 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    //아이템 데이터 추가를 위한 함수.
    public void addItem(String nickname , String userid){
        FollowDTO item = new FollowDTO();
        item.setNickname(nickname);
        item.setUserid(userid);
        listViewItemList.add(item);
    }



}