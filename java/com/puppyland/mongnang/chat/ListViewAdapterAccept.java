package com.puppyland.mongnang.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.R;
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

public class ListViewAdapterAccept extends BaseAdapter implements  ChatuserListContract.View{

    private LinearLayout chatuserLinear;
    private TextView Tx_AcceptUserId, Tx_chatting , Tx_NoAcceptUserNickname , Tx_chattingDelete;
    private CircleImageView circleImageView;
    ChatuserListContract.Presenter chatuserListPresenter;
    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ChatUserDTO> listViewItemList = new ArrayList<ChatUserDTO>();
    private Context context;
    //ListViewAdapter의 생성자
    public ListViewAdapterAccept() {
        chatuserListPresenter = new ChatuserListPresenter(this);
    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        context = parent.getContext();


        //listView_item Layout을 inflate하여 ConvertView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.acceptchatuserlistview_item, parent, false);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("Loginfile", 0x0000);
        String loginId = sharedPreferences.getString("id", null);

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        chatuserLinear =(LinearLayout) convertView.findViewById(R.id.chatuserLinear);
        circleImageView = (CircleImageView) convertView.findViewById(R.id.userProfileImage);
        Tx_AcceptUserId = (TextView) convertView.findViewById(R.id.Tx_AcceptUserId);
        Tx_chatting = (TextView) convertView.findViewById(R.id.Tx_chatting);
        Tx_chattingDelete = (TextView) convertView.findViewById(R.id.Tx_chattingDelete);
        Tx_NoAcceptUserNickname = (TextView) convertView.findViewById(R.id.Tx_NoAcceptUserNickname);

        ChatUserDTO listViewItem = listViewItemList.get(position);


        if(listViewItem.getAcceptNumber().equals("5")){ //5인 애들은 삭제를 요청한 유저라는 정보를 띄워줘야해서 따로 구분해서 만듬
            Tx_NoAcceptUserNickname.setText("채팅을 삭제한 유저입니다.");
            Tx_chatting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "채팅을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            Tx_chattingDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "목록에서 삭제했습니다. 새로고침 해주세요.", Toast.LENGTH_SHORT).show();
                    //여기서 이제 삭제 당한 사람도 삭제당했다는걸 인지했으니까 db에서 기록 날려버려야함.
                    ChatUserDTO dto = new ChatUserDTO();
                    dto.setUserId(loginId);

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

                }
            });
        }
        /*else if(listViewItem.getAcceptNumber().equals("7")){
            Tx_NoAcceptUserNickname.setText("나를 차단한 유저입니다.");
            Tx_chatting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "채팅을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            Tx_chattingDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "목록에서 삭제했습니다. 새로고침 해주세요.", Toast.LENGTH_SHORT).show();
                    //여기서 이제 삭제 당한 사람도 삭제당했다는걸 인지했으니까 db에서 기록 날려버려야함.
                    ChatUserDTO dto = new ChatUserDTO();
                    dto.setUserId(loginId);//누르면 차단 당한 사람의 채팅db기록은 지워짐
                    dto.setChatUserId(listViewItem.getUserId());
                    // 차단한 사람은 7로 남아있어야함
                    Gson gson = new Gson();
                    String objJson = gson.toJson(dto);
                    Call<ResponseBody> BlockDeleteChat = NetRetrofit.getInstance().getService().BlockDeleteChat(objJson);
                    BlockDeleteChat.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                }
            });
        }*/
        else if (listViewItem.getAcceptNumber().equals("3")){ // 수락한 사람들
            Tx_AcceptUserId.setText(listViewItem.getChatUserId());
            Tx_NoAcceptUserNickname.setText(listViewItem.getNickname());
            Picasso.get().load("http://192.168.219.100:8092/upload/" + listViewItem.getMemberimage()).error(R.drawable.monglogo2).fit().centerCrop().into(circleImageView);
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(v.getContext() , userClickImageActivity.class);
                    intent1.putExtra("id" ,listViewItem.getChatUserId());
                    v.getContext().startActivity(intent1);
                }
            });
            //채팅 버튼
            Tx_chatting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore.getInstance().collection("users").whereEqualTo("userid", listViewItem.getChatUserId())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("uiddd", String.valueOf(document.get("uid")));
                                            Intent intent = new Intent(context, ChatActivity.class);
                                            intent.putExtra("toUid", String.valueOf(document.get("uid")));
                                            context.startActivity(intent);
                                        }
                                    }
                                }
                            });
                }
            });
            //삭제버튼
            Tx_chattingDelete.setOnClickListener(new View.OnClickListener() { //삭제하면 상대쪽에서도 반응이 있어야함.
                //삭제하면 flag를 5 로 바꿔서 삭제가 채팅창 들어왔을때 쭉 체크할때 넘버가 5인 사람은 상대방이 삭제했다는 문구를 보여줘야할듯
                @Override
                public void onClick(View v) {
                    //삭제하고 db에는 5로 플래그 변경해야함 // 그리고 삭제했다는 문구를 확인하고 누르면 그때서야 db에서 55로 변경해줌
                    // 요청을 보낸 사람은 6으로 받은 사람은5로 5인 사람이 삭제됐다는걸 확인하면 6으로 바뀌면서 둘다 출력안된다.
                    //6으로 바꾸는 코드는 스프링 xml에 있음
                    //6일때는 화면에 노출도 안되고 5일때는 노출은 되는데 클릭해서 채팅을 못하게
                    chatuserListPresenter.chatUserListDelete(loginId , listViewItem.getChatUserId(), "5");
                    Toast.makeText(v.getContext(), "목록에서 삭제했습니다. 새로고침 해주세요.", Toast.LENGTH_SHORT).show();
                  //  listViewItemList.remove(position); // 그 포지션의 데이터를 삭제
                }
            });
            chatuserLinear.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    blockCheck(loginId , Tx_AcceptUserId.getText().toString() , "7"); //7 이 차단하는 넘버

                    return true;
                }
            });
        }
        return convertView;
    }


    //롱클릭 차단 다이얼로그
    public void blockCheck(String userid , String chatuserid , String accpetNumber){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.blockcheck, null);
        builder.setView(view);
        final TextView block = (TextView) view.findViewById(R.id.btn_block);
        final TextView cancel = (TextView) view.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatUserDTO chatUserDTO = new ChatUserDTO();
                chatUserDTO.setUserId(userid);
                chatUserDTO.setChatUserId(chatuserid);
                chatUserDTO.setAcceptNumber(accpetNumber);

                Gson gson = new Gson();
                String objJson = gson.toJson(chatUserDTO); // DTO 객체를 json 으로 변환

                Call<ChatUserDTO> chatUserListCheckAcceptUpdate = NetRetrofit.getInstance().getService().chatUserListCheckAcceptUpdate(objJson);
                chatUserListCheckAcceptUpdate.enqueue(new Callback<ChatUserDTO>() {
                    @Override
                    public void onResponse(Call<ChatUserDTO> call, Response<ChatUserDTO> response) {
                        if(response.isSuccessful()){
                            Log.v("woxeodk", "성공");
                        }
                    }
                    @Override
                    public void onFailure(Call<ChatUserDTO> call, Throwable t) {
                        Log.d("fail", t.getMessage());
                    }
                });

                //차단당한 애는 삭제시켜야함
                ChatUserDTO chatUserDTO2 = new ChatUserDTO();
                chatUserDTO2.setUserId(chatuserid); // 여기 뭔가 이상함 되긴되는데 왜 되는건지 모르겠음
                chatUserDTO2.setChatUserId(userid);
                Gson gson2 = new Gson();
                String objJson2 = gson2.toJson(chatUserDTO); // DTO 객체를 json 으로 변환

                Call<ResponseBody> BlockDeleteChat = NetRetrofit.getInstance().getService().BlockDeleteChat(objJson2);
                BlockDeleteChat.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(v.getContext(), "차단 했습니다. 새로고침 해주세요.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("fail", t.getMessage());
                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
    public void addItem(String chatuserid , String memberimage , String nickname , String acceptNumber , String userid) {

        ChatUserDTO item = new ChatUserDTO();
        item.setChatUserId(chatuserid);
        item.setMemberimage(memberimage);
        item.setNickname(nickname);
        item.setAcceptNumber(acceptNumber);
        item.setUserId(userid);
        listViewItemList.add(item);
    }


}