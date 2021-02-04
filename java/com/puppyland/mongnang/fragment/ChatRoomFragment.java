package com.puppyland.mongnang.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.ChatRoomModel;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.Message;
import com.puppyland.mongnang.DTO.UserModel;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatRoomFragment extends Fragment {
    private Context mContext;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private RecyclerViewAdapter mAdapter;
    private List<String> arrayList;

    public List<String> getArrayList() {
        return arrayList;
    }

    //파이어스토어 rooms 삭제
    private FirebaseFirestore db;

    public void setArrayList(List<String> arrayList) {
        this.arrayList = arrayList;
    }

    private String userImagename;
    private List<String> usernicknamelist = new ArrayList<>();

    public ChatRoomFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        mAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);

        //파이어스토어 rooms 삭제
        db = FirebaseFirestore.getInstance();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("ChatuserListfile", Context.MODE_PRIVATE);
        String chatid = sharedPreferences1.getString("chatid", null);
        String[] strs;
        if (chatid == null) {
            List<String> arrayList2 = new ArrayList<String>();
            setArrayList(arrayList2);
            return view;
        }
        strs = chatid.split(" ");
        List<String> arrayList2 = new ArrayList<String>(Arrays.asList(strs));
        setArrayList(arrayList2);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    // =============================================================================================
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final private RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));
        private List<ChatRoomModel> roomList = new ArrayList<>();
        private Map<String, UserModel> userList = new HashMap<>();
        private String myUid;
        private StorageReference storageReference;
        private FirebaseFirestore firestore;
        private ListenerRegistration listenerRegistration;
        private ListenerRegistration listenerUsers;

        RecyclerViewAdapter() {
            firestore = FirebaseFirestore.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();
            myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


            // all users information
            listenerUsers = firestore.collection("users")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }

                            for (QueryDocumentSnapshot doc : value) {

                                userList.put(doc.getId(), doc.toObject(UserModel.class));
                            }
                            getRoomInfo();
                        }
                    });
        }

        Integer unreadTotal = 0;

        public void getRoomInfo() {
            // my chatting room information
            //  Log.v("rz",getArrayList().get(0));
            listenerRegistration = firestore.collection("rooms").whereGreaterThanOrEqualTo("users." + myUid, 0)
                    //여기서 쿼리로 방 정보 가져오는듯 여기서 wherein 해서 field 부분은 users.userid 로 하고 유저리스트프라그먼트에서 해줬던거 처럼
                    //arraylist 넣어줘서 유저로 추가되 있는 채팅방만 남아있게 해주면 될듯.
//                    a.orderBy("timestamp", Query.Direction.DESCENDING)346 vgft
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d("@@@채팅을걸었을때, 방이 생성되는부분", e.getMessage());
                                //return;
                            }


                            TreeMap<Date, ChatRoomModel> orderedRooms = new TreeMap<Date, ChatRoomModel>(Collections.reverseOrder());

                            for (final QueryDocumentSnapshot document : value) {
                                Message message = document.toObject(Message.class);
                                if (message.getMsg() != null & message.getTimestamp() == null) {
                                    continue;
                                } // FieldValue.serverTimestamp is so late

                                ChatRoomModel chatRoomModel = new ChatRoomModel();
                                chatRoomModel.setRoomID(document.getId());

                                if (message.getMsg() != null) { // there are no last message
                                    chatRoomModel.setLastDatetime(simpleDateFormat.format(message.getTimestamp()));
                                    switch (message.getMsgtype()) {
                                        case "1":
                                            chatRoomModel.setLastMsg("Image");
                                            break;
                                        case "2":
                                            chatRoomModel.setLastMsg("File");
                                            break;
                                        default:
                                            chatRoomModel.setLastMsg(message.getMsg());
                                    }
                                }
                                Map<String, Long> users = (Map<String, Long>) document.get("users");
                                chatRoomModel.setUserCount(users.size());
                                for (String key : users.keySet()) {
                                    if (myUid.equals(key)) {
                                        Integer unread = (int) (long) users.get(key);
                                        unreadTotal += unread;
                                        chatRoomModel.setUnreadCount(unread);
                                        break;
                                    }
                                }
                                if (users.size() == 2) {
                                    for (String key : users.keySet()) {
                                        if (myUid.equals(key)) continue;
                                        UserModel userModel = userList.get(key);
                                        chatRoomModel.setTitle(userModel.getUsernm());
                                        /*usernicknamelist.add(userModel.getUserid());
                                        MemberDTO memberDTO = new MemberDTO();
                                        memberDTO.setUserId(userModel.getUserid());

                                        Gson gson = new Gson();
                                        String objJson = gson.toJson(memberDTO);

                                        Call<MemberDTO> fileName1 = NetRetrofit.getInstance().getService().fileName1(objJson);
                                        fileName1.enqueue(new Callback<MemberDTO>() {
                                            @Override
                                            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                                                //별명 저장
                                                String nickedname = response.body().getNickname();
                                                chatRoomModel.setTitle(nickedname);
                                            }

                                            @Override
                                            public void onFailure(Call<MemberDTO> call, Throwable t) {
                                                Log.d("###", t.getMessage());
                                            }
                                        });*/

                                        //여기서 방이름이 뜨니까 이 이름으로 레트로핏 요청해야하지 않을까 싶음
                                        chatRoomModel.setPhoto(userModel.getUserphoto());
                                    }
                                } else {                // group chat room
                                    chatRoomModel.setTitle(document.getString("title"));
                                }
                                if (message.getTimestamp() == null)
                                    message.setTimestamp(new Date());
                                orderedRooms.put(message.getTimestamp(), chatRoomModel);
                            }
                            roomList.clear();
                            for (Map.Entry<Date, ChatRoomModel> entry : orderedRooms.entrySet()) {
                                roomList.add(entry.getValue());
                            }
                            notifyDataSetChanged();
                            setBadge(mContext, unreadTotal);
                        }
                    });
        }

        public void stopListening() {
            if (listenerRegistration != null) {
                listenerRegistration.remove();
                listenerRegistration = null;
            }
            if (listenerUsers != null) {
                listenerUsers.remove();
                listenerUsers = null;
            }

            roomList.clear();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom, parent, false);
            return new RoomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RoomViewHolder roomViewHolder = (RoomViewHolder) holder;

            final ChatRoomModel chatRoomModel = roomList.get(position);
            roomViewHolder.room_title.setText(chatRoomModel.getTitle());
            roomViewHolder.last_msg.setText(chatRoomModel.getLastMsg());

            MemberDTO dto = new MemberDTO();
            dto.setNickname(chatRoomModel.getTitle());

            Gson gson = new Gson();
            String objJson = gson.toJson(dto);

            Call<MemberDTO> ChatgetMyImg = NetRetrofit.getInstance().getService().ChatgetMyImg(objJson);
            ChatgetMyImg.enqueue(new Callback<MemberDTO>() {
                @Override
                public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                    MemberDTO dto2 = response.body();
                    String img = dto2.getMemberimage();
                    userImagename = img;
                    Picasso.get().load("http://192.168.219.100:8092/upload/" + img).error(R.drawable.monglogo2).fit().into(roomViewHolder.room_image);
                }

                @Override
                public void onFailure(Call<MemberDTO> call, Throwable t) {
                    Log.d("채팅에서 보여지는 내 사진 가져오기 실패", t.getMessage());
                }
            });

            /*MemberDTO memberDTO = new MemberDTO();
            memberDTO.setUserId(usernicknamelist.get(position));

            Gson gson = new Gson();
            String objJson = gson.toJson(memberDTO);

            Call<MemberDTO> fileName1 = NetRetrofit.getInstance().getService().fileName1(objJson);
            fileName1.enqueue(new Callback<MemberDTO>() {
                @Override
                public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                    //별명 저장
                    String nickedname = response.body().getNickname();
                    roomViewHolder.room_title.setText(nickedname);
                    Picasso.get().load("http://192.168.35.7:8092/upload/" + response.body().getMemberimage()).error(R.color.colorAccent).fit().into(roomViewHolder.room_image);
                    roomViewHolder.chatuserImagename.setText(response.body().getMemberimage());
                }

                @Override
                public void onFailure(Call<MemberDTO> call, Throwable t) {
                    Log.d("###", t.getMessage());
                }
            });*/




            /*
            if (chatRoomModel.getPhoto()==null) {
                Glide.with(getActivity()).load(R.drawable.common_google_signin_btn_icon_dark_focused)
                        .apply(requestOptions)
                        .into(roomViewHolder.room_image);
            } else{
                Glide.with(getActivity()).load(storageReference.child("userPhoto/"+chatRoomModel.getPhoto()))
                        .apply(requestOptions)
                        .into(roomViewHolder.room_image);
            }*/
            if (chatRoomModel.getUserCount() > 2) {
                roomViewHolder.room_count.setText(chatRoomModel.getUserCount().toString());
                roomViewHolder.room_count.setVisibility(View.VISIBLE);
            } else {
                roomViewHolder.room_count.setVisibility(View.INVISIBLE);
            }
            if (chatRoomModel.getUnreadCount() > 0) {
                roomViewHolder.unread_count.setText(chatRoomModel.getUnreadCount().toString());
                roomViewHolder.unread_count.setVisibility(View.VISIBLE);
            } else {
                roomViewHolder.unread_count.setVisibility(View.INVISIBLE);
            }
            //방탈출
            roomViewHolder.Tx_byebye.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db.collection("rooms").document(chatRoomModel.getRoomID())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("###", "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("###", e.getMessage());
                                }
                            });
                }
            });
            roomViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // 채팅방 목록중 하나를 눌렀을때 동작
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
                    intent.putExtra("roomID", chatRoomModel.getRoomID());
                    intent.putExtra("roomTitle", chatRoomModel.getTitle());
                    intent.putExtra("chatuserImage", userImagename);
                    intent.putExtra("chatuserNickname", roomViewHolder.room_title.getText().toString());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return roomList.size();
        }

        private class RoomViewHolder extends RecyclerView.ViewHolder {
            CircleImageView room_image;
            public TextView room_title;
            public TextView last_msg;
            public TextView room_count;
            public TextView unread_count;
            public TextView Tx_byebye;
            public TextView chatuserImagename;

            RoomViewHolder(View view) {
                super(view);
                room_image = view.findViewById(R.id.room_image);
                room_title = view.findViewById(R.id.room_title);
                last_msg = view.findViewById(R.id.last_msg);
                Tx_byebye = view.findViewById(R.id.Tx_byebye);
                room_count = view.findViewById(R.id.room_count);
                unread_count = view.findViewById(R.id.unread_count);
                chatuserImagename = view.findViewById(R.id.chatuserImagename);
            }
        }
    }

    public static void setBadge(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                return resolveInfo.activityInfo.name;
            }
        }
        return null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}