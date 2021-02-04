package com.puppyland.mongnang.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.SelectedUserDiaryActivity;
import com.puppyland.mongnang.contract.ChatuserListContract;
import com.puppyland.mongnang.presenter.ChatuserListPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.userClickImageActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListViewAdapterNoAccept extends BaseAdapter implements ChatuserListContract.View {

    private TextView Tx_NoAcceptUserId, Tx_accept, Tx_denied , Tx_NoAcceptUserNickname;
    private CircleImageView circleImageView;
    private String userID;
    private ChatuserListContract.Presenter presenter;

    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ChatUserDTO> listViewItemList = new ArrayList<ChatUserDTO>();

    //ListViewAdapter의 생성자
    public ListViewAdapterNoAccept() {
    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        presenter = new ChatuserListPresenter(this);

        //listView_item Layout을 inflate하여 ConvertView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.noacceptchatuserlistview_item, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        circleImageView = (CircleImageView) convertView.findViewById(R.id.userProfileImage);
        Tx_NoAcceptUserId = (TextView) convertView.findViewById(R.id.Tx_NoAcceptUserId);
        Tx_accept = (TextView) convertView.findViewById(R.id.Tx_accept);
        Tx_denied = (TextView) convertView.findViewById(R.id.Tx_denied);
        Tx_NoAcceptUserNickname =(TextView)convertView.findViewById(R.id.Tx_NoAcceptUserNickname);


        ChatUserDTO listViewItem = listViewItemList.get(position);
        Tx_NoAcceptUserId.setText(listViewItem.getChatUserId());
        Tx_NoAcceptUserNickname.setText(listViewItem.getNickname());
        Picasso.get().load("http://192.168.219.100:8092/upload/" + listViewItem.getMemberimage()).error(R.drawable.monglogo2).fit().centerCrop().into(circleImageView);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(v.getContext() , userClickImageActivity.class);
                        intent1.putExtra("id" ,listViewItem.getChatUserId());
                        v.getContext().startActivity(intent1);
                    }
                });
            }
        });
        // 유저이름 누르면 그 사람 피드로 가는거
        Tx_NoAcceptUserNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SelectedUserDiaryActivity.class);
                intent.putExtra("selectedId" ,listViewItem.getChatUserId());
                intent.putExtra("selectedNickname",listViewItem.getNickname());
                v.getContext().startActivity(intent);
            }
        });
        //수락 버튼
        Tx_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //        ChatRoomFragment.arrayList2.add(listViewItem.getChatUserId());
                presenter.chatUserListCheckAcceptUpdate(listViewItem.getUserId(), listViewItem.getChatUserId(), "3");
                listViewItemList.remove(listViewItemList.get(position));
                notifyDataSetChanged();
            }
        });

//거절 버튼
        Tx_denied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatUserDTO dto = new ChatUserDTO();
                dto.setUserId(listViewItem.getUserId());

                Gson gson = new Gson();
                String objJson = gson.toJson(dto);

                Call<ResponseBody> DeleteChatlist = NetRetrofit.getInstance().getService().DeleteChatlist(objJson);
                DeleteChatlist.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                listViewItemList.remove(listViewItemList.get(position));
                getDeviceId(listViewItem.getChatUserId() , 5 ,""); //5는 채팅거절 플래그
                notifyDataSetChanged();
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
    public void addItem(String chatuserid, String userid , String memberImage , String nickname) {
    //    userID = userid;

        ChatUserDTO item = new ChatUserDTO();
        item.setChatUserId(chatuserid);
        item.setMemberimage(memberImage);
        item.setUserId(userid);
        item.setNickname(nickname);

        listViewItemList.add(item);
    }


    private void getDeviceId(String userid ,  final int flag , final  String vno) { // 이 함수를 가져다 쓰기만하면 3개를 다 돌려막기 할 수 있음.
        DeviceIdDTO deviceIdDTO = new DeviceIdDTO();
        deviceIdDTO.setId(userid);

        Gson gson = new Gson();
        String objJson = gson.toJson(deviceIdDTO);

        Call<DeviceIdDTO> getDeviceId = NetRetrofit.getInstance().getService().getDeviceId(objJson);
        getDeviceId.enqueue(new retrofit2.Callback<DeviceIdDTO>() {
            @Override
            public void onResponse(Call<DeviceIdDTO> call, Response<DeviceIdDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("@@@su", "성공");

                    String tempDeviceid = response.body().getDeviceId();

                    DeviceIdDTO deviceIdDTO = new DeviceIdDTO();
                    deviceIdDTO.setDeviceId(tempDeviceid);
                    deviceIdDTO.setId(userid);

                    Gson gson = new Gson();
                    String objJson = gson.toJson(deviceIdDTO);

                    Call<ResponseBody> boardPushAlarm = NetRetrofit.getInstance().getService().PushAlarm(flag,objJson,"0");
                    boardPushAlarm.enqueue(new retrofit2.Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.d("@@@su", "성공");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("fail", t.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<DeviceIdDTO> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }


}