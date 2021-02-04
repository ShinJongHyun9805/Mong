package com.puppyland.mongnang.Story;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
import com.puppyland.mongnang.DTO.DiaryDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.NotificationDTO;
import com.puppyland.mongnang.DTO.StoryReplyDTO;
import com.puppyland.mongnang.DTO.WarningDTO;
import com.puppyland.mongnang.OutlineTextView.OutlineTextView;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.SelectedUserDiaryActivity;
import com.puppyland.mongnang.StoryActivity;
import com.puppyland.mongnang.contract.DiaryContract;
import com.puppyland.mongnang.contract.NotificationContract;
import com.puppyland.mongnang.presenter.DiaryPresenter;
import com.puppyland.mongnang.presenter.NotificationPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.CustomViewHolder>{
    int temp;
    StoryReplyAdapter storyReplyAdapter;
    Context mContext;
    View view1;
    private ArrayList<DiaryDTO> mList = new ArrayList<DiaryDTO>();
    private ArrayList<StoryReplyDTO> msubList = new ArrayList<StoryReplyDTO>();
    private ArrayList<StoryReplyDTO> msubList2;
    List<String> mImages;
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    StoryActivity storyActivity;
    private ArrayList<DiaryDTO> mArrayList;
    private String getdno, writeuserid, id  , loginId; // 신고관련

    public class CustomViewHolder extends RecyclerView.ViewHolder implements DiaryContract.view , NotificationContract.View{

        CircleImageView userProfileImage;
        OutlineTextView UserId;
        TextView content ,title , ddate , dno , nickname , likeyCnt ,replyCnt;
        ImageView reply;
        ImageView replygoCommitBtn;
        EditText replyCommit;
        LinearLayout linearLayout, selectedLinear;
        ImageView carouselView;
        ListView replyRecyclerView;
        ScrollView replyScroll , rootScroll;
        LottieAnimationView animationView; // 좋아요 애니메이션
        private boolean like; // 좋아요
        DiaryContract.presenter dpresenter;
        NotificationContract.Presenter npresenter;
        int likey;
        //신고이미지
        ImageView warning;
        int result; // 신고결과
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Loginfile", MODE_PRIVATE);

        public CustomViewHolder(View view) {
            super(view);
            this.userProfileImage = (CircleImageView) view.findViewById(R.id.userProfileImage);
            this.UserId = (OutlineTextView) view.findViewById(R.id.userId);
            this.nickname = (TextView) view.findViewById(R.id.userNickname);
            this.content = (TextView) view.findViewById(R.id.storycontent);
            this.title = (TextView) view.findViewById(R.id.storyTitle);
            this.ddate = (TextView) view.findViewById(R.id.storydate);
            this.dno = (TextView) view.findViewById(R.id.dno);
            this.replyCommit = (EditText) view.findViewById(R.id.replyCommit);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.replyList);
            this.reply = view.findViewById(R.id.reply);
            this.replyCnt = view.findViewById(R.id.replyCnt);
            // this.reply.setButtonColor(view.getResources().getColor(R.color.fbutton_color_a));
            this.replygoCommitBtn = view.findViewById(R.id.replygoCommitBtn);
            this.carouselView = view.findViewById(R.id.carouselView2);
            this.replyRecyclerView = view.findViewById(R.id.ReplyRecyclerview);
            this.selectedLinear = view.findViewById(R.id.selectedLinear);
            this.animationView = view.findViewById(R.id.Lottie);
            this.like = false;
            this.dpresenter = new DiaryPresenter(this);
            this.npresenter = new NotificationPresenter(this);
            this.likeyCnt = view.findViewById(R.id.likey2);
            this.likey =0 ;
            this.warning = view.findViewById(R.id.warnig);
            this.result=0;
            loginId = sharedPreferences.getString("id", "");
        }
    }
    public StoryAdapter(Activity activity){
        this.mContext = activity;
    }

    @Override
    public StoryAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.storylist_item, viewGroup, false);

        StoryAdapter.CustomViewHolder viewHolder = new StoryAdapter.CustomViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder viewholder, int position) {


        mImages = new ArrayList<String>();
        final  String a = mList.get(position).getImg();
        mImages.add(a);

        // 동그란 프로필 사진 가져오는 코드
        MemberDTO dto = new MemberDTO();
        dto.setUserId(mList.get(position).getUserid());
        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환
        Call<MemberDTO> getMyInfo = NetRetrofit.getInstance().getService().fileName1(objJson);
        getMyInfo.enqueue(new retrofit2.Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                Picasso.get().load("https://mongnyang.shop/upload/" + response.body().getMemberimage()).error(R.drawable.monglogo2).fit().into(viewholder.userProfileImage);
            }
            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {

            }
        });

        StoryCheckWarning(mList.get(position).getDno() ,viewholder.sharedPreferences.getString("id", "") ,viewholder.result , viewholder.warning);
        // 스토리 카드 내용 채우는 코드
        viewholder.UserId.setText(mList.get(position).getUserid());
        writeuserid = mList.get(position).getUserid();
        viewholder.nickname.setText(mList.get(position).getNickname());
        viewholder.selectedLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext() , SelectedUserDiaryActivity.class);
                intent.putExtra("selectedId" , viewholder.UserId.getText().toString());
                intent.putExtra("selectedNickname" , viewholder.nickname.getText().toString());
                v.getContext().startActivity(intent);
            }
        });
        Log.v("getuserid" ,mList.get(position).getUserid() );
        AssetManager assetManager;
        if(mList.get(position).getFont() == null){

        }
        Typeface typeFace = Typeface.createFromAsset(StoryActivity.context.getAssets() , mList.get(position).getFont()); // db에 해당글의 저장된 폰트를 적용하는 코드
        viewholder.content.setTypeface(typeFace);

        viewholder.content.setText(mList.get(position).getDcontent());
        viewholder.title.setText(mList.get(position).getTitle());
        viewholder.ddate.setText(mList.get(position).getCreate_date());
        viewholder.dno.setText(mList.get(position).getDno());
        getdno = mList.get(position).getDno(); //신고때매
        viewholder.likeyCnt.setText(String.valueOf(mList.get(position).getLikey()));
        viewholder.likey = mList.get(position).getLikey();
        CheckLike(mList.get(position).getDno() , viewholder);
        if(!(mList.get(position).getImg().equals(""))){
            Picasso.get().load("https://mongnyang.shop/upload/" + mList.get(position).getImg()).fit().into( viewholder.carouselView);
        }else{
            Picasso.get().load("https://mongnyang.shop/upload/" + mList.get(position).getImg()).error(R.drawable.monglogo2).fit().centerInside().into( viewholder.carouselView);
        }


        viewholder.replyRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    ((StoryActivity)StoryActivity.context).mRecyclerView.requestDisallowInterceptTouchEvent(false);

                }
                else{
                    ((StoryActivity)StoryActivity.context).mRecyclerView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        viewholder.reply.setOnClickListener(new View.OnClickListener() { // 댓글달기 버튼을 누르면 댓글 입력창이랑 댓글 리스트가 보임
            @Override
            public void onClick(View v) {

                if(viewholder.linearLayout.getVisibility() == View.VISIBLE){ // 보일때는 안보이게
                    viewholder.linearLayout.setVisibility(View.GONE);
                }
                else{
                    viewholder.linearLayout.setVisibility(View.VISIBLE); //안보일때는 보이게
                }
            }
        });
        StoryReplyDTO sdto = new StoryReplyDTO();
        sdto.setDno(viewholder.dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용

        Gson gson2 = new Gson();
        String objJson2 = gson2.toJson(sdto); // DTO 객체를 json 으로 변환

        Call<List<StoryReplyDTO>> getstoryReplyList = NetRetrofit.getInstance().getService().getstoryReplyList(objJson2);//여기 레트로핏 하나 더 만들어야함
        //인증번호 2인 애들만 다이어리 테이블에서 셀렉트 리스트 하는거
        getstoryReplyList.enqueue(new retrofit2.Callback<List<StoryReplyDTO>>() {
            @Override
            public void onResponse(Call<List<StoryReplyDTO>> call, Response<List<StoryReplyDTO>> response) {
                List<StoryReplyDTO> templist = new ArrayList<StoryReplyDTO>();
                msubList2 = new ArrayList<StoryReplyDTO>();
                templist = response.body();
                if(templist !=null){
                    for(int i=0;i<templist.size();i++){
                        msubList2.add(templist.get(i));
                    }
                }
                //리사이클러뷰 안에 리사이클러뷰 붙이는중

                storyReplyAdapter = new StoryReplyAdapter();
                viewholder.replyRecyclerView.setAdapter(storyReplyAdapter);

                try {
                    if (msubList2 != null) {
                        for (StoryReplyDTO storyReplyDTO : msubList2) { // 리스트로 담기는// 것들 하나씩 출력
                            Log.d("oeoeoe" ,storyReplyDTO.getContent());
                            storyReplyAdapter.addItem(storyReplyDTO.getDno(),storyReplyDTO.getUserid(),storyReplyDTO.getContent(),storyReplyDTO.getDate() , storyReplyDTO.getNickname());
                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(msubList2.size()>9){
                    viewholder.replyCnt.setText(String.valueOf(msubList2.size()));
                }else if(msubList2.size()<10){
                    viewholder.replyCnt.setText(String.valueOf(msubList2.size())+" ");
                }
                setListViewHeightBasedOnItems(viewholder.replyRecyclerView);
                storyReplyAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<StoryReplyDTO>> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });
        viewholder.replyCommit.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        viewholder.replyCommit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        if (viewholder.replyCommit.getText().toString().equals("")) {
                            Toast.makeText(StoryActivity.context, "내용을 작성해 주세요.", Toast.LENGTH_LONG).show();
                        } else {

                            id = viewholder.sharedPreferences.getString("id", "");
                            StoryReplyDTO dto = new StoryReplyDTO();
                            dto.setUserid(id);
                            //닉네임
                            SharedPreferences sharedPreferences2 = v.getContext().getSharedPreferences("nickname", MODE_PRIVATE);
                            final String nickname = sharedPreferences2.getString("nickname", null);
                            dto.setNickname(nickname);
                            dto.setDno(viewholder.dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용
                            String tempreplyCommit =viewholder.replyCommit.getText().toString();
                            tempreplyCommit = plzNoHacking(tempreplyCommit);
                            dto.setContent(tempreplyCommit); // 되는지 모르겠음

                            Gson gson = new Gson();
                            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                            Call<ResponseBody> storyReplyinsert = NetRetrofit.getInstance().getService().storyReplyinsert(objJson);
                            storyReplyinsert.enqueue(new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    NotificationDTO ndto = new NotificationDTO();
                                    ndto.setUserid(viewholder.UserId.getText().toString());
                                    ndto.setNo(viewholder.dno.getText().toString());
                                    ndto.setItem("스토리");
                                    ndto.setDiarywritedate(mList.get(position).getCreate_date());
                                    ndto.setDiarywritefont(mList.get(position).getFont());
                                    ndto.setDiarywritenickname(mList.get(position).getNickname());
                                    ndto.setDiarywrtieimagename(mList.get(position).getImg());
                                    ndto.setDiarywritecontent(mList.get(position).getDcontent());
                                    ndto.setDiarywritetitle(mList.get(position).getTitle());
                                    ndto.setDiarywritelikey(String.valueOf(mList.get(position).getLikey()));
                                    ndto.setDiarywriteshare(mList.get(position).getShareConfirmation());
                                    viewholder.npresenter.UpdateNotification(ndto);
                                    if(!viewholder.sharedPreferences.getString("id", "").equals(viewholder.UserId.getText().toString())){
                                        getDeviceId(viewholder.UserId.getText().toString(),2 , viewholder.dno.getText().toString());
                                    }
                                    //스토리 달리면 스토리 주인한테 푸시알람 보내는 코드
                                    setListViewHeightBasedOnItems(viewholder.replyRecyclerView);
                                    viewholder.replyCommit.setText("");// 댓글달면 창 비우기
                                    Toast.makeText(StoryActivity.context, "작성 완료.", Toast.LENGTH_SHORT).show();

                                    // 작성완료 뒤에 리스트 읽어온다
                                    StoryReplyDTO dto = new StoryReplyDTO();
                                    dto.setDno(viewholder.dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용
                                    Gson gson = new Gson();
                                    String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환
                                    Call<List<StoryReplyDTO>> getstoryReplyList = NetRetrofit.getInstance().getService().getstoryReplyList(objJson);//여기 레트로핏 하나 더 만들어야함
                                    //인증번호 2인 애들만 다이어리 테이블에서 셀렉트 리스트 하는거
                                    getstoryReplyList.enqueue(new retrofit2.Callback<List<StoryReplyDTO>>() {
                                        @Override
                                        public void onResponse(Call<List<StoryReplyDTO>> call, Response<List<StoryReplyDTO>> response) {
                                            List<StoryReplyDTO> templist = new ArrayList<StoryReplyDTO>();
                                            msubList2 = new ArrayList<StoryReplyDTO>();
                                            templist = response.body();
                                            if(templist !=null){
                                                for(int i=0;i<templist.size();i++){
                                                    msubList2.add(templist.get(i));
                                                }
                                            }
                                            //리사이클러뷰 안에 리사이클러뷰 붙이는중

                                            storyReplyAdapter = new StoryReplyAdapter();
                                            viewholder.replyRecyclerView.setAdapter(storyReplyAdapter);

                                            try {
                                                if (msubList2 != null) {
                                                    for (StoryReplyDTO storyReplyDTO : msubList2) { // 리스트로 담기는// 것들 하나씩 출력
                                                        Log.d("oeoeoe" ,storyReplyDTO.getContent());
                                                        storyReplyAdapter.addItem(storyReplyDTO.getDno(),storyReplyDTO.getUserid(),storyReplyDTO.getContent(),storyReplyDTO.getDate() , storyReplyDTO.getNickname());
                                                    }
                                                } else {

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            viewholder.replyCnt.setText(String.valueOf(msubList2.size()));
                                            setListViewHeightBasedOnItems(viewholder.replyRecyclerView);
                                            storyReplyAdapter.notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onFailure(Call<List<StoryReplyDTO>> call, Throwable t) {
                                            Log.d("###", t.getMessage());
                                        }
                                    });


                                }
                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.d("###", t.getMessage());
                                }
                            });
                            break;
                        }
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });


        viewholder.replygoCommitBtn.setOnClickListener(new View.OnClickListener() { // 댓글다는창 없에서 댓글 입력버튼을 눌렀을때
            @Override
            public void onClick(View v) {


                id = viewholder.sharedPreferences.getString("id", "");
                StoryReplyDTO dto = new StoryReplyDTO();
                dto.setUserid(id);
                //닉네임
                SharedPreferences sharedPreferences2 = v.getContext().getSharedPreferences("nickname", MODE_PRIVATE);
                final String nickname = sharedPreferences2.getString("nickname", null);
                dto.setNickname(nickname);
                dto.setDno(viewholder.dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용
                String tempreplyCommit  = viewholder.replyCommit.getText().toString();
                tempreplyCommit = plzNoHacking(tempreplyCommit);
                dto.setContent(tempreplyCommit); // 되는지 모르겠음

                Gson gson = new Gson();
                String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                Call<ResponseBody> storyReplyinsert = NetRetrofit.getInstance().getService().storyReplyinsert(objJson);
                storyReplyinsert.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        NotificationDTO ndto = new NotificationDTO();
                        ndto.setUserid(viewholder.UserId.getText().toString());
                        ndto.setNo(viewholder.dno.getText().toString());
                        ndto.setItem("스토리");
                        ndto.setDiarywritedate(mList.get(position).getCreate_date());
                        ndto.setDiarywritefont(mList.get(position).getFont());
                        ndto.setDiarywritenickname(mList.get(position).getNickname());
                        ndto.setDiarywrtieimagename(mList.get(position).getImg());
                        ndto.setDiarywritecontent(mList.get(position).getDcontent());
                        ndto.setDiarywritetitle(mList.get(position).getTitle());
                        ndto.setDiarywritelikey(String.valueOf(mList.get(position).getLikey()));
                        ndto.setDiarywriteshare(mList.get(position).getShareConfirmation());
                        viewholder.npresenter.UpdateNotification(ndto);
                        if(!viewholder.sharedPreferences.getString("id", "").equals(viewholder.UserId.getText().toString() )) {
                            getDeviceId(viewholder.UserId.getText().toString(), 2, viewholder.dno.getText().toString()); // 콜백이 많아지니까 문제가 있을수도 있다.
                            //스토리 달리면 스토리 주인한테 푸시알람 보내는 코드
                        }
                        setListViewHeightBasedOnItems(viewholder.replyRecyclerView);
                        viewholder.replyCommit.setText("");// 댓글달면 창 비우기
                        Toast.makeText(StoryActivity.context, "작성 완료.", Toast.LENGTH_SHORT).show();

                        StoryReplyDTO dto = new StoryReplyDTO();
                        dto.setDno(viewholder.dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용

                        Gson gson = new Gson();
                        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                        Call<List<StoryReplyDTO>> getstoryReplyList = NetRetrofit.getInstance().getService().getstoryReplyList(objJson);//여기 레트로핏 하나 더 만들어야함
                        //인증번호 2인 애들만 다이어리 테이블에서 셀렉트 리스트 하는거
                        getstoryReplyList.enqueue(new retrofit2.Callback<List<StoryReplyDTO>>() {
                            @Override
                            public void onResponse(Call<List<StoryReplyDTO>> call, Response<List<StoryReplyDTO>> response) {
                                List<StoryReplyDTO> templist = new ArrayList<StoryReplyDTO>();
                                msubList2 = new ArrayList<StoryReplyDTO>();
                                templist = response.body();
                                if(templist !=null){
                                    for(int i=0;i<templist.size();i++){
                                        msubList2.add(templist.get(i));
                                    }
                                }
                                //리사이클러뷰 안에 리사이클러뷰 붙이는중

                                storyReplyAdapter = new StoryReplyAdapter();
                                viewholder.replyRecyclerView.setAdapter(storyReplyAdapter);

                                try {
                                    if (msubList2 != null) {
                                        for (StoryReplyDTO storyReplyDTO : msubList2) { // 리스트로 담기는// 것들 하나씩 출력
                                            Log.d("oeoeoe" ,storyReplyDTO.getContent());
                                            storyReplyAdapter.addItem(storyReplyDTO.getDno(),storyReplyDTO.getUserid(),storyReplyDTO.getContent(),storyReplyDTO.getDate() , storyReplyDTO.getNickname());
                                        }
                                    } else {

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                setListViewHeightBasedOnItems(viewholder.replyRecyclerView);
                                storyReplyAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onFailure(Call<List<StoryReplyDTO>> call, Throwable t) {
                                Log.d("###", t.getMessage());
                            }
                        });
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("###", t.getMessage());
                    }
                });
            }
        });
        //좋아요
        viewholder.animationView.setAnimation("lf30_editor_yjixlnlv.json");
        viewholder.animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewholder.like == false) { // 좋아요 푸는 상태
                    viewholder.dpresenter.UnLike(viewholder.dno.getText().toString() , loginId);
                    viewholder.dpresenter.desUpdateLike(viewholder.dno.getText().toString());
                    viewholder.likeyCnt.setText(String.valueOf(viewholder.likey-1));
                    viewholder.likey=viewholder.likey-1;
                    viewholder.animationView.setProgress(0);

                    viewholder.like = true;
                } else { // 좋아요 하는 상태
                    SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
                    String id = sharedPreferences.getString("id", "");

                    // 푸시알람 관리
                    NotificationDTO ndto = new NotificationDTO();
                    ndto.setUserid(viewholder.UserId.getText().toString());
                    ndto.setNo(viewholder.dno.getText().toString());
                    ndto.setItem("스토리");
                    ndto.setDiarywritedate(mList.get(position).getCreate_date());
                    ndto.setDiarywritefont(mList.get(position).getFont());
                    ndto.setDiarywritenickname(mList.get(position).getNickname());
                    ndto.setDiarywrtieimagename(mList.get(position).getImg());
                    ndto.setDiarywritecontent(mList.get(position).getDcontent());
                    ndto.setDiarywritetitle(mList.get(position).getTitle());
                    ndto.setDiarywritelikey(String.valueOf(mList.get(position).getLikey()));
                    ndto.setDiarywriteshare(mList.get(position).getShareConfirmation());

                    viewholder.npresenter.UpdateNotification(ndto);

                    if(!viewholder.sharedPreferences.getString("id", "").equals(viewholder.UserId.getText().toString()) ) {
                        getDeviceId(viewholder.UserId.getText().toString(), 6, viewholder.dno.getText().toString()); // 콜백이 많아지니까 문제가 있을수도 있다.
                    }
                    viewholder.animationView.playAnimation();
                    viewholder.dpresenter.UpdateLike(viewholder.dno.getText().toString());
                    viewholder.dpresenter.InsertLike(id, viewholder.dno.getText().toString());
                    viewholder.likeyCnt.setText(String.valueOf(viewholder.likey+1));
                    viewholder.likey=viewholder.likey+1;
                    viewholder.like = false;
                }
            }
        });

        //신고
        viewholder.warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Warning(viewholder.dno.getText().toString(),viewholder.sharedPreferences.getString("id", ""),viewholder.UserId.getText().toString(), viewholder.warning);
            }
        });
    }

    private boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            //setDynamicHeight(listView);
            return true;

        } else {
            return false;
        }
    }


    private void getDeviceId(String userid ,  final int flag, final  String dno) { // 이 함수를 가져다 쓰기만하면 3개를 다 돌려막기 할 수 있음.
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

                    Call<ResponseBody> boardPushAlarm = NetRetrofit.getInstance().getService().PushAlarm(flag,objJson , dno);
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

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    private void CheckLike(String dno  , @NonNull final CustomViewHolder viewholder) {
        DiaryDTO dto = new DiaryDTO();
        dto.setDno(dno);
        Log.v("checklikeId", loginId);
        dto.setUserid(loginId);
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> diaryCheckLike = NetRetrofit.getInstance().getService().diaryCheckLike(objJson);
        diaryCheckLike.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int result = Integer.parseInt(response.body().string());
                    if (result == 1) {
                        viewholder.animationView.setProgress(1);
                        viewholder.like = false;
                    } else {
                        viewholder.animationView.setProgress(0);
                        viewholder.like = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //아이템 데이터 추가를 위한 함수.
    public void addItem(String dno, String title, String userid, String content , String imgName, String create_date , String nickname , String font , int likey, String ShareConfirmation){
        DiaryDTO item = new DiaryDTO();
        item.setDno(dno);
        item.setUserid(userid);
        item.setImg(imgName);
        item.setDcontent(content);
        item.setTitle(title);
        item.setCreate_date(create_date);
        item.setNickname(nickname);
        item.setFont(font);
        item.setLikey(likey);
        item.setShareConfirmation(ShareConfirmation);
        mList.add(item);

    }

    /*********************************************************************************************************************************************************
     * 신고
     ********************************************************************************************************************************************************/
    private void Warning(String no  ,String id , String writeuserid ,ImageView imageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //  LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.warningcheck, null);
        builder.setView(view);
        final TextView warning = (TextView) view.findViewById(R.id.btn_warning);
        final TextView cancel = (TextView) view.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();

        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WarningDTO dto = new WarningDTO();
                dto.setNo(no);
                Log.d("###getno",no);
                dto.setItem("스토리");
                dto.setWriteuserid(writeuserid);
                Log.d("###writeuserid",writeuserid);
                dto.setId(id);
                Log.d("###id",id);

                Gson gson = new Gson();
                String objJson = gson.toJson(dto);

                Call<ResponseBody> Warning = NetRetrofit.getInstance().getService().Warning(objJson);
                Warning.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(mContext, "신고 완료.", Toast.LENGTH_SHORT).show();
                        imageView.setImageResource(R.drawable.warnig);
                        imageView.setClickable(false);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(mContext, "신고 실패!< 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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


    private void StoryCheckWarning(String bno , String loginId , int result , ImageView imageView) {
        WarningDTO dto = new WarningDTO();
        dto.setNo(bno);
        dto.setItem("스토리");
        dto.setId(loginId);
        //   Log.v("checkwarnning" , bno + loginId);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> StoryCheckWarning = NetRetrofit.getInstance().getService().StoryCheckWarning(objJson);
        StoryCheckWarning.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body().string().equals("1")) {
                            imageView.setImageResource(R.drawable.warnig);
                            imageView.setClickable(false);
                        } else {
                            imageView.setImageResource(R.drawable.notwaring);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public String plzNoHacking(String string){
        string = string.replace("!", "\u0021");
        string = string.replace("?", "\u003F");
        string = string.replace("%", "\u0025");
        string = string.replace("#", "\u0023");
        string = string.replace("@", "\u0040");
        string = string.replace("^", "\u005E");
        string = string.replace("&", "\u0026");
        string = string.replace("*", "\u002A");
        string = string.replace("(", "\u0028");
        string = string.replace(")", "\u0029");
        string = string.replace("-", "\u002D");
        string = string.replace("_", "\u005F");
        string = string.replace("+", "\u002B");
        string = string.replace("=", "\u003D");
        string = string.replace(";", "\u003B");
        string = string.replace("(", "\u003A");
        string = string.replace("/", "\u002F");
        string = string.replace(".", "\u002E");
        string = string.replace("<", "\u003C");
        string = string.replace("~", "\u007E");

        return string;
    }
}