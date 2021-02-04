package com.puppyland.mongnang;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.util.IOUtils;
import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
import com.puppyland.mongnang.DTO.DiaryDTO;
import com.puppyland.mongnang.DTO.FunctionCountDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.NotificationDTO;
import com.puppyland.mongnang.DTO.StoryReplyDTO;
import com.puppyland.mongnang.Story.StoryReplyAdapter;
import com.puppyland.mongnang.contract.DiaryContract;
import com.puppyland.mongnang.contract.FunctionCountContract;
import com.puppyland.mongnang.contract.NotificationContract;
import com.puppyland.mongnang.presenter.DiaryPresenter;
import com.puppyland.mongnang.presenter.FunctionCountPresenter;
import com.puppyland.mongnang.presenter.NotificationPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.widget.FButton;
import com.squareup.picasso.Picasso;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectedUserDiaryVIewActivity extends AppCompatActivity implements DiaryContract.view , FunctionCountContract.View, NotificationContract.View{

    StoryReplyAdapter storyReplyAdapter;
    Context mContext;
    CardView cv;
    private ArrayList<StoryReplyDTO> msubList2;
    FButton replygoCommitBtn;

    TextView date , dno , nickname , replyCnt , likeyCnt ,UserId;
    String imageName , font , share;
    int likey;
    EditText replyCommit , Et_comment , content ,title;
    ImageView carouselView;
    ListView listView;
    List<String> mImages;
    ImageView menu3dot , inputArrow;
    DiaryContract.presenter dpresenter;
    String tempuri;
    LottieAnimationView animationView; // 좋아요 애니메이션
    CircleImageView userProfileImage;
    private boolean like; // 좋아요
    LinearLayout imageLL;


    //사진 가져오기 및 입력
    private static final int MY_PERMISSION_STORAGE = 1111;
    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "https://mongnyang.shop/upload.jsp";  //<<서버주소
    private Bitmap image_bitmap_copy = null;
    private Bitmap image_bitmap = null;
    private String ImageName;
    private String fileName = null;
    String dirayfont;
    boolean shareflag = false;
    String loginId;
    //pdf 관련
    //pdf 테스트중
    private static final String TAG = "webnautes" ;
    private File root;
    private AssetManager assetManager;
    private PDFont font2;
    FunctionCountContract.Presenter fpresenter;
    NotificationContract.Presenter npresenter;
    Switch shareSwitch; // 공유스위치
    @Override
    protected void onStart() {
        super.onStart();
        setup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_user_diary_view);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //아이디
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile",MODE_PRIVATE); // receiver 에선 context로 접근
        loginId = sharedPreferences.getString("id",null); // 지금 로그인한 사람 id

        //닉네임
        SharedPreferences sharedPreferences2 = getSharedPreferences("nickname", MODE_PRIVATE);
        final String nicknedname = sharedPreferences2.getString("nickname", "");
        dpresenter = new DiaryPresenter(this);
        fpresenter = new FunctionCountPresenter(this);
        npresenter = new NotificationPresenter(this);
        cv = findViewById(R.id.cv);
        dno = findViewById(R.id.dno);
        date = findViewById(R.id.storydate);
        content = findViewById(R.id.storycontent);
        title = findViewById(R.id.storyTitle);
        UserId = findViewById(R.id.userId);
        nickname = findViewById(R.id.nickname);
        listView = findViewById(R.id.listview);
        carouselView = findViewById(R.id.carouselView2);
        replyCommit = findViewById(R.id.replyCommit);
        menu3dot = findViewById(R.id.menu3dot);
        replyCnt = findViewById(R.id.replyCnt);
        likeyCnt = findViewById(R.id.likey);
        inputArrow= findViewById(R.id.inputArrow);
        shareSwitch = findViewById(R.id.shareSwitch);
        userProfileImage= findViewById(R.id.userProfileImage);
        imageLL = findViewById(R.id.imageLL);

        Intent intent = getIntent(); /*데이터 수신*/
        UserId.setText(intent.getExtras().getString("user"));
        nickname.setText(intent.getExtras().getString("nickname"));
        dno.setText(intent.getExtras().getString("dno"));
        date.setText(intent.getExtras().getString("date"));
        title.setText(intent.getExtras().getString("title"));
        imageName = intent.getExtras().getString("imageName");
        content.setText(intent.getExtras().getString("content"));
        font = intent.getExtras().getString("font");
        likey = intent.getExtras().getInt("likey");
        share = intent.getExtras().getString("share");



        DiaryDTO dto = new DiaryDTO();
        dto.setDno(dno.getText().toString());

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<DiaryDTO> diarydetailInfo = NetRetrofit.getInstance().getService().diarydetailInfo(objJson);
        diarydetailInfo.enqueue(new Callback<DiaryDTO>() {
            @Override
            public void onResponse(Call<DiaryDTO> call, Response<DiaryDTO> response) {
                DiaryDTO dto2 = response.body();
                likeyCnt.setText(String.valueOf(dto2.getLikey()));
            }

            @Override
            public void onFailure(Call<DiaryDTO> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });

        getProfileImage(UserId.getText().toString());

        if(imageName.equals("")){
            imageLL.setVisibility(View.GONE);
        }else{
            Picasso.get().load("https://mongnyang.shop/upload/" +imageName).error(R.drawable.monglogo2).fit().into(carouselView);
        }
        if(font==null){
            Typeface typeFace = Typeface.createFromAsset(getAssets(), "ibmplexsanskrsemibold.ttf"); // 폰트적용
        }else{
            Typeface typeFace = Typeface.createFromAsset(getAssets(), font); // 폰트적용
            content.setTypeface(typeFace);
        }
        if(share.equals("2")){// 공유했을때
            shareSwitch.setChecked(true);
            shareflag = true;
        }else if(share.equals("1")){ // 공유 아닐때
            shareSwitch.setChecked(false);
            shareflag = false;
        }else if(!(share.equals("1")) ||!(share.equals("2")))
        {
            shareSwitch.setChecked(false);
            shareflag = false;
        }



        shareSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareflag==false){ //공유 안되어있을때 누르면 공유하는 동작
                    SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                    String loginId = sharedPreferences.getString("id", null);
                    String title2 =  title.getText().toString();
                    String content2 = content.getText().toString();
                    //   bpresenter.ShareBoard(bno , loginId ,title, content,fileName); // 스토리 공유했을때 넘어가는 값들
                    ShareBoard(dno.getText().toString() , loginId ,title2, content2,imageName);
                    shareflag =true;
                }
                else{ //반대
                    SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                    String loginId = sharedPreferences.getString("id", null);
                    String title2 = title.getText().toString();
                    String content2 = content.getText().toString();
                    dpresenter.ShareBoardoff(dno.getText().toString()  , loginId ,title2, content2,imageName); // 스토리 공유를 풀었을때
                    shareflag =false;
                }
            }
        });



        if((UserId.getText().toString().equals(loginId))){ // 로그인 한 사람이랑 글쓴이가 같을때만 사진 클릭해서 변경되도록
            carouselView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CropViewActivity.imageFile = null;
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                    carouselView.setVisibility(View.VISIBLE);
                }
            });
        }


        if(!(UserId.getText().toString().equals(loginId))){ // 같지 않으면
            menu3dot.setVisibility(View.GONE);
            shareSwitch.setVisibility(View.GONE);
            content.setEnabled(false);
            title.setEnabled(false);
        }
        menu3dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.menu_diary, p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("삭제")) {
                            dpresenter.DeleteBoard(dno.getText().toString());
                            Toast.makeText(SelectedUserDiaryVIewActivity.this, "삭제 완료.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if(item.getTitle().equals("저장")){
                            String title2 = title.getText().toString();
                            String content2 = content.getText().toString();
                            if ((title2 == null || title2.getBytes().length == 0 || title2.getBytes().length < 5) || (content2 == null || content2.getBytes().length == 0 || content2.getBytes().length <= 10)) {
                                Toast.makeText(getApplicationContext(), "5글자 이상으로 적어주세요.", Toast.LENGTH_SHORT).show();
                            }else if(content2.getBytes().length > 500) {
                                Toast.makeText(getApplicationContext(), "500자 이하로 작성해 주세요:(", Toast.LENGTH_SHORT).show();
                            }else{
                                if(ImageName == null){
                                    //사진 업로드
                                    DoFileUpload(serverURL, img_path);
                                    dpresenter.UpdateBoard(dno.getText().toString(), title2, content2, imageName);
                                    Toast.makeText(SelectedUserDiaryVIewActivity.this, "수정 완료.", Toast.LENGTH_LONG).show();
                                    finish();
                                }else{
                                    //사진 업로드
                                    DoFileUpload(serverURL, img_path);
                                    dpresenter.UpdateBoard(dno.getText().toString(), title2, content2, ImageName);
                                    Toast.makeText(SelectedUserDiaryVIewActivity.this, "수정 완료.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        }
                        else if(item.getTitle().equals("PDF 다운로드")){
                            //TODO: PDF 남은 갯수 체크
                            CheckCountPDF();
                        }else if(item.getTitle().equals("사진첨부")) {

                        }
                        return false;
                    }
                });
                p.show();
            }
        });

        CheckLike(dno.getText().toString());

        //댓글 입력버튼
        inputArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etcontent = Et_comment.getText().toString();
                etcontent= plzNoHacking(etcontent);
                if (etcontent.equals("")) {
                    Toast.makeText(SelectedUserDiaryVIewActivity.this, "내용을 작성해 주세요.", Toast.LENGTH_LONG).show();
                } else {
                    //댓글달기
                    SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
                    String id = sharedPreferences.getString("id", "");
                    StoryReplyDTO dto = new StoryReplyDTO();
                    dto.setUserid(id);
                    dto.setNickname(nicknedname);
                    dto.setDno(dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용
                    dto.setContent(etcontent); // 되는지 모르겠음


                    Gson gson = new Gson();
                    String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                    Call<ResponseBody> storyReplyinsert = NetRetrofit.getInstance().getService().storyReplyinsert(objJson);
                    storyReplyinsert.enqueue(new retrofit2.Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            NotificationDTO ndto = new NotificationDTO();
                            ndto.setUserid(UserId.getText().toString());
                            ndto.setNo(dno.getText().toString());
                            ndto.setItem("스토리");
                            ndto.setDiarywritedate(date.getText().toString());
                            ndto.setDiarywritefont(font);
                            ndto.setDiarywritenickname(nickname.getText().toString());
                            ndto.setDiarywrtieimagename(imageName);
                            ndto.setDiarywritecontent(content.getText().toString());
                            ndto.setDiarywritetitle(title.getText().toString());
                            ndto.setDiarywritelikey(String.valueOf(likey));
                            ndto.setDiarywriteshare(share);
                            npresenter.UpdateNotification(ndto);
                            if(!loginId.equals(UserId.getText().toString())){
                                getDeviceId(UserId.getText().toString(),2 ,dno.getText().toString());
                                //스토리 달리면 스토리 주인한테 푸시알람 보내는 코드
                            }
                            StoryReplyDTO dto = new StoryReplyDTO();
                            dto.setDno(dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용

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
                                    listView.setAdapter(storyReplyAdapter);
                                    try {
                                        if (msubList2 != null) {
                                            for (StoryReplyDTO storyReplyDTO : msubList2) { // 리스트로 담기는// 것들 하나씩 출력
                                                Log.d("oeoeoe" ,storyReplyDTO.getContent());
                                                storyReplyAdapter.addItem(storyReplyDTO.getDno(),storyReplyDTO.getUserid(),storyReplyDTO.getContent(),storyReplyDTO.getDate(), storyReplyDTO.getNickname());
                                            }
                                        } else {

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    replyCnt.setText("댓글"+"("+msubList2.size()+")");
                                    setListViewHeightBasedOnItems(listView);
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
                    Toast.makeText(SelectedUserDiaryVIewActivity.this, "작성 완료", Toast.LENGTH_LONG).show();
                    Et_comment.setText(null);
                }

            }
        });

        Et_comment = findViewById(R.id.Et_comment);
        Et_comment.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        Et_comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        String etcontent = Et_comment.getText().toString();
                        etcontent = plzNoHacking(etcontent);
                        if (etcontent.equals("")) {
                            Toast.makeText(SelectedUserDiaryVIewActivity.this, "내용을 작성해 주세요.", Toast.LENGTH_LONG).show();
                        } else {
                            //댓글달기
                            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
                            String id = sharedPreferences.getString("id", "");
                            StoryReplyDTO dto = new StoryReplyDTO();
                            dto.setUserid(id);
                            dto.setNickname(nicknedname);
                            dto.setDno(dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용
                            dto.setContent(etcontent); // 되는지 모르겠음


                            Gson gson = new Gson();
                            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                            Call<ResponseBody> storyReplyinsert = NetRetrofit.getInstance().getService().storyReplyinsert(objJson);
                            storyReplyinsert.enqueue(new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    NotificationDTO ndto = new NotificationDTO();
                                    ndto.setUserid(UserId.getText().toString());
                                    ndto.setNo(dno.getText().toString());
                                    ndto.setItem("스토리");
                                    ndto.setDiarywritedate(date.getText().toString());
                                    ndto.setDiarywritefont(font);
                                    ndto.setDiarywritenickname(nickname.getText().toString());
                                    ndto.setDiarywrtieimagename(imageName);
                                    ndto.setDiarywritecontent(content.getText().toString());
                                    ndto.setDiarywritetitle(title.getText().toString());
                                    ndto.setDiarywritelikey(String.valueOf(likey));
                                    ndto.setDiarywriteshare(share);
                                    npresenter.UpdateNotification(ndto);
                                    if(!loginId.equals(UserId.getText().toString())) {
                                        getDeviceId(UserId.getText().toString(), 2, dno.getText().toString()); // 콜백이 많아지니까 문제가 있을수도 있다.
                                        //스토리 달리면 스토리 주인한테 푸시알람 보내는 코드
                                    }
                                    StoryReplyDTO dto = new StoryReplyDTO();
                                    dto.setDno(dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용

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
                                            listView.setAdapter(storyReplyAdapter);
                                            try {
                                                if (msubList2 != null) {
                                                    for (StoryReplyDTO storyReplyDTO : msubList2) { // 리스트로 담기는// 것들 하나씩 출력
                                                        Log.d("oeoeoe" ,storyReplyDTO.getContent());
                                                        storyReplyAdapter.addItem(storyReplyDTO.getDno(),storyReplyDTO.getUserid(),storyReplyDTO.getContent(),storyReplyDTO.getDate(), storyReplyDTO.getNickname());
                                                    }
                                                } else {

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            replyCnt.setText("댓글"+"("+msubList2.size()+")");
                                            setListViewHeightBasedOnItems(listView);
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
                            Toast.makeText(SelectedUserDiaryVIewActivity.this, "작성 완료", Toast.LENGTH_LONG).show();
                            Et_comment.setText(null);
                            break;
                        }
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });

        //좋아요
        animationView = findViewById(R.id.Lottie);
        animationView.setAnimation("lf30_editor_yjixlnlv.json");
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like == false) {
                    dpresenter.UnLike(dno.getText().toString() , loginId);
                    dpresenter.desUpdateLike(dno.getText().toString());
                    likeyCnt.setText(String.valueOf(likey-1));
                    likey=likey-1;
                    animationView.setProgress(0);

                    like = true;
                } else {
                    animationView.playAnimation();
                    // 푸시알람 관리
                    NotificationDTO ndto = new NotificationDTO();
                    ndto.setUserid(UserId.getText().toString());
                    ndto.setNo(dno.getText().toString());
                    ndto.setItem("스토리");
                    ndto.setDiarywritedate(date.getText().toString());
                    ndto.setDiarywritefont(font);
                    ndto.setDiarywritenickname(nickname.getText().toString());
                    ndto.setDiarywrtieimagename(imageName);
                    ndto.setDiarywritecontent(content.getText().toString());
                    ndto.setDiarywritetitle(title.getText().toString());
                    ndto.setDiarywritelikey(String.valueOf(likey));
                    ndto.setDiarywriteshare(share);
                    npresenter.UpdateNotification(ndto);
                    if(!loginId.equals(UserId.getText().toString())) {
                        getDeviceId(UserId.getText().toString(), 6, dno.getText().toString()); // 콜백이 많아지니까 문제가 있을수도 있다.
                    }
                    dpresenter.UpdateLike(dno.getText().toString());
                    dpresenter.InsertLike(loginId, dno.getText().toString());
                    likeyCnt.setText(String.valueOf(likey+1));
                    likey=likey+1;

                    like = false;
                }
            }
        });


        //댓글 달기
        Et_comment = findViewById(R.id.Et_comment);
        Et_comment.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        Et_comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        String etcontent = Et_comment.getText().toString();

                        if (etcontent.equals("")) {
                            Toast.makeText(SelectedUserDiaryVIewActivity.this, "내용을 작성해 주세요.", Toast.LENGTH_LONG).show();
                        } else {
                            //댓글달기
                            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
                            String id = sharedPreferences.getString("id", "");
                            StoryReplyDTO dto = new StoryReplyDTO();
                            dto.setUserid(id);
                            dto.setNickname(nicknedname);
                            dto.setDno(dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용
                            dto.setContent(etcontent); // 되는지 모르겠음


                            Gson gson = new Gson();
                            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                            Call<ResponseBody> storyReplyinsert = NetRetrofit.getInstance().getService().storyReplyinsert(objJson);
                            storyReplyinsert.enqueue(new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    NotificationDTO ndto = new NotificationDTO();
                                    ndto.setUserid(UserId.getText().toString());
                                    ndto.setNo(dno.getText().toString());
                                    ndto.setItem("스토리");
                                    ndto.setDiarywritedate(date.getText().toString());
                                    ndto.setDiarywritefont(font);
                                    ndto.setDiarywritenickname(nickname.getText().toString());
                                    ndto.setDiarywrtieimagename(imageName);
                                    ndto.setDiarywritecontent(content.getText().toString());
                                    ndto.setDiarywritetitle(title.getText().toString());
                                    ndto.setDiarywritelikey(String.valueOf(likey));
                                    ndto.setDiarywriteshare(share);
                                    npresenter.UpdateNotification(ndto);
                                    if(!loginId.equals(UserId.getText().toString())) {
                                        getDeviceId(UserId.getText().toString(), 2, dno.getText().toString()); // 콜백이 많아지니까 문제가 있을수도 있다.
                                        //스토리 달리면 스토리 주인한테 푸시알람 보내는 코드
                                    }
                                    StoryReplyDTO dto = new StoryReplyDTO();
                                    dto.setDno(dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용

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
                                            listView.setAdapter(storyReplyAdapter);
                                            try {
                                                if (msubList2 != null) {
                                                    for (StoryReplyDTO storyReplyDTO : msubList2) { // 리스트로 담기는// 것들 하나씩 출력
                                                        Log.d("oeoeoe" ,storyReplyDTO.getContent());
                                                        storyReplyAdapter.addItem(storyReplyDTO.getDno(),storyReplyDTO.getUserid(),storyReplyDTO.getContent(),storyReplyDTO.getDate(), storyReplyDTO.getNickname());
                                                    }
                                                } else {

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            replyCnt.setText("댓글"+"("+msubList2.size()+")");
                                            setListViewHeightBasedOnItems(listView);
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
                            Toast.makeText(SelectedUserDiaryVIewActivity.this, "작성 완료", Toast.LENGTH_LONG).show();
                            Et_comment.setText(null);
                            break;
                        }
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        StoryReplyDTO dto = new StoryReplyDTO();
        dto.setDno(dno.getText().toString()); // 해당 글의 dno 를 보내서 거기에 달린 댓글을 다 가져오기위해 사용

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
                listView.setAdapter(storyReplyAdapter);

                try {
                    if (msubList2 != null) {
                        for (StoryReplyDTO storyReplyDTO : msubList2) { // 리스트로 담기는// 것들 하나씩 출력
                            Log.d("oeoeoe" ,storyReplyDTO.getContent());
                            storyReplyAdapter.addItem(storyReplyDTO.getDno(),storyReplyDTO.getUserid(),storyReplyDTO.getContent(),storyReplyDTO.getDate(), storyReplyDTO.getNickname());
                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                replyCnt.setText("댓글"+"("+msubList2.size()+")");
                setListViewHeightBasedOnItems(listView);
                storyReplyAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<StoryReplyDTO>> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });
    }

    private  void getProfileImage(String selectedId){
        // 동그란 프로필 사진 가져오는 코드
        MemberDTO prodto = new MemberDTO();
        prodto.setUserId(selectedId);
        Gson progson = new Gson();
        String proobjJson = progson.toJson(prodto); // DTO 객체를 json 으로 변환
        Call<MemberDTO> getMyInfo = NetRetrofit.getInstance().getService().fileName1(proobjJson);
        getMyInfo.enqueue(new retrofit2.Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                Picasso.get().load("https://mongnyang.shop/upload/" + response.body().getMemberimage()).error(R.drawable.monglogo2).fit().into(userProfileImage);
            }
            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {

            }
        });
    }


    private void CheckLike(String dno) {
        DiaryDTO dto = new DiaryDTO();
        dto.setDno(dno);
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
                        animationView.setProgress(1);
                        like = false;
                    } else {
                        animationView.setProgress(0);
                        like = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SelectedUserDiaryVIewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getDeviceId(String userid ,  final int flag , final  String dno) { // 이 함수를 가져다 쓰기만하면 3개를 다 돌려막기 할 수 있음.
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

    public void ShareBoard(String dno , String userid, String title , String content, String imgName){

        DiaryDTO dto = new DiaryDTO();
        dto.setDno(dno);
        dto.setUserid(userid);
        dto.setTitle(title);
        dto.setDcontent(content);
        dto.setImg(imgName);
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> shareUpdateStory = NetRetrofit.getInstance().getService().shareUpdateStory(objJson);
        shareUpdateStory.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("@@@su", "성공");
                    Intent intent = new Intent(SelectedUserDiaryVIewActivity.this, shareCompleteActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getBaseContext(), "resultCode : " + data, Toast.LENGTH_SHORT).show();
        if (requestCode == REQ_CODE_SELECT_IMAGE
                && resultCode == Activity.RESULT_OK) {
            Uri galleryPictureUri = data.getData();
            String uri = galleryPictureUri.toString();

            Log.d("###1", String.valueOf(galleryPictureUri));

            // Intent intent = new Intent(getApplicationContext(),CropViewActivity.class);
            //  intent.putExtra("img",uri);

            Intent intent = new Intent(getApplicationContext(),CropViewActivity.class);
            intent.putExtra("img",uri);
            startActivityForResult(intent , 1123);

            //updateButtons();
        }
        if(requestCode == 1123 && resultCode ==1124){
            tempuri = data.getStringExtra("contenturi");
            Log.v("xoxoxoxo" , tempuri);
            Picasso.get().load(tempuri).error(R.drawable.monglogo2).fit().centerCrop().into(carouselView);
            Bitmap bitmap = BitmapFactory.decodeFile(tempuri);
            carouselView.setImageBitmap(bitmap);
            img_path =getFilePathFromURI(getApplicationContext(),Uri.parse(tempuri));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//end of onActivityResult()

    public  String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        ImageName = fileName;
        Log.v("wgxogxwqqq" , fileName);
        return fileName;
    }
    public  String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        File rootDataDir = context.getFilesDir();
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(rootDataDir + File.separator + fileName);
            copy(context, contentUri, copyFile);
            Log.v("result" ,copyFile.getAbsolutePath() );
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 여기서부터가 jsp 이미지를 보내는 코드
    public void DoFileUpload(String apiUrl, String absolutePath) {
        HttpFileUpload(apiUrl, "", absolutePath);
    }

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    //서버에 선택한 이미지를 전송하기 위한 함수 이미지의 URI정보를 매개로 하여 byte 단위로 서버에 전달
    public void HttpFileUpload(String urlString, String params, String fileName) {
        try {
            FileInputStream mFileInputStream = new FileInputStream(fileName);
            //하드 디스크상에 존재하는 파일로부터 바이트 단위의 입력을 받는 클래스이다.
            URL connectUrl = new URL(urlString);
            Log.d("Stream", "mFileInputStream  is " + mFileInputStream);

            // HttpURLConnection 통신
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            Log.d("uri", String.valueOf(conn));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            Log.d("Read", "image byte is " + bytesRead);

            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("Written", "File is written");
            mFileInputStream.close();
            dos.flush();
            // finish upload...

            // get response
            InputStream is = conn.getInputStream();

            StringBuffer b = new StringBuffer();
            for (int ch = 0; (ch = is.read()) != -1; ) {
                b.append((char) ch);
            }
            is.close();
            Log.e("zxcv", b.toString());


        } catch (Exception e) {
            Log.d("Message", "exception " + e.getMessage());
        }
    }


//pdf 관련

    private void setup() {

        PDFBoxResourceLoader.init(getApplicationContext());
        root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        assetManager = getAssets();

        if (ContextCompat.checkSelfPermission(SelectedUserDiaryVIewActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(SelectedUserDiaryVIewActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    public String createPdf() {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        try{
            font2 = PDType0Font.load(document, assetManager.open(font));
        }
        catch (IOException e){
            Log.e(TAG, "폰트를 읽을 수 없습니다.", e);
        }

        PDPageContentStream contentStream;

        try {
            contentStream = new PDPageContentStream( document, page, true, true);
            Drawable drawable;
            Bitmap bitmap = null;
            int image_width;
            int image_height;

            int A4_width = (int) PDRectangle.A4.getWidth();
            int A4_height = (int) PDRectangle.A4.getHeight();

            Drawable drawable1 = getResources().getDrawable(R.drawable.pdfdogbackground);
            Bitmap bitmap1 = ((BitmapDrawable)drawable1).getBitmap();
            Bitmap resized1 = Bitmap.createScaledBitmap(bitmap1, A4_width, A4_height, true);
            PDImageXObject pdImage1 = LosslessFactory.createFromImage(document, resized1);

            float x_pos = page.getCropBox().getWidth();
            float y_pos = page.getCropBox().getHeight();


            contentStream.drawImage(pdImage1, -20f, -10f, (int)page.getMediaBox().getWidth()+80, (int)page.getMediaBox().getHeight()+40);
            //  contentStream.drawImage(pdImage,70, 70, image_w, image_h);

            String title2 = title.getText().toString();


            int text_width = 300;
            int text_left = 120; // 글자 시작하는 위치
            int fontSize = 20;
            float leading = 1.2f * fontSize; // 이건 위아래 간격인듯

            String textN = "제목: "+title2+"\n\n";



            String con = textN+content.getText().toString();
            String combinecontent = "";
            int k=0;
            for(int i =0 ;i<con.length() ;i++){
                combinecontent += con.charAt(i);
                if( i > textN.length()){
                    k = k+1;
                    if(k%22 ==0 &&k!=0 ){
                        combinecontent+= "\n";
                    }
                }
            }


            List<String> lines = new ArrayList<String>();
            int lastSpace = -1;
            for (String text : combinecontent.split("\n"))
            {
                while (text.length() > 0) {
                    int spaceIndex = text.indexOf(' ', lastSpace + 1);
                    if (spaceIndex < 0)
                        spaceIndex = text.length();
                    String subString = text.substring(0, spaceIndex);
                    float size = fontSize * font2.getStringWidth(subString) / 1000;
                    if (size > text_width) {
                        if (lastSpace < 0)
                            lastSpace = spaceIndex;
                        subString = text.substring(0, lastSpace);
                        lines.add(subString);
                        text = text.substring(lastSpace).trim();
                        lastSpace = -1;
                    } else if (spaceIndex == text.length()) {
                        lines.add(text);
                        text = "";
                    } else {
                        lastSpace = spaceIndex;
                    }
                }
            }

            contentStream.beginText();
            contentStream.setFont(font2, fontSize);
            contentStream.newLineAtOffset(text_left, 630);//원래 20
            contentStream.setNonStrokingColor(60,60,60);
            for (String line: lines)
            {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -leading);
            }
            contentStream.endText();
            contentStream.close();

            String path = root.getAbsolutePath()  +"/"+date.getText().toString()+".pdf";

            document.save(path);
            document.close();
            fpresenter.functionCount_update_changepdf(loginId, 1);
            return path;

        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while creating PDF", e);
        }

        return "error";
    }

    public String createPdf2() {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        try{
            font2 = PDType0Font.load(document, assetManager.open("ibmplexsanskrsemibold.ttf"));
        }
        catch (IOException e){
            Log.e(TAG, "폰트를 읽을 수 없습니다.", e);
        }

        PDPageContentStream contentStream;

        try {
            contentStream = new PDPageContentStream( document, page, true, true);

            Drawable drawable;
            Bitmap bitmap = null;
            int image_width;
            int image_height;
            if(!(imageName.equals(""))){
                drawable = carouselView.getDrawable();
                bitmap = ((BitmapDrawable)drawable).getBitmap();
                image_width = bitmap.getWidth();
                image_height = bitmap.getHeight();
            }else{
                drawable = null;
                image_width = 0;
                //  image_height = bitmap.getHeight();
            }

            int A4_width = (int) PDRectangle.A4.getWidth();
            int A4_height = (int) PDRectangle.A4.getHeight();

            float scale = (float) (A4_width/(float)image_width*0.7);
            int image_w;
            int image_h;
            if(bitmap.getWidth() ==0){
                image_w = 0;
                image_h = 0;
            }else{
                image_w = (int) (bitmap.getWidth() * scale);
                image_h = (int) (bitmap.getHeight() * scale);
            }

            Bitmap resized = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
            PDImageXObject pdImage = LosslessFactory.createFromImage(document, resized);

            Drawable drawable1 = getResources().getDrawable(R.drawable.pdfdogbackground);
            Bitmap bitmap1 = ((BitmapDrawable)drawable1).getBitmap();
            Bitmap resized1 = Bitmap.createScaledBitmap(bitmap1, A4_width, A4_height, true);
            PDImageXObject pdImage1 = LosslessFactory.createFromImage(document, resized1);

            float x_pos = page.getCropBox().getWidth();
            float y_pos = page.getCropBox().getHeight();

            float x_adjusted = (float) (( x_pos - image_w ) * 0.5 + page.getCropBox().getLowerLeftX());
            float y_adjusted = (float) ((y_pos - image_h) * 0.9 + page.getCropBox().getLowerLeftY());

            contentStream.drawImage(pdImage1, -20f, -10f, (int)page.getMediaBox().getWidth()+80, (int)page.getMediaBox().getHeight()+40);
            contentStream.drawImage(pdImage,108f, 350f, image_w-21, image_h-10);

            int text_width = 470;
            int text_left = 250; // 글자 시작하는 위치

            String textN =date.getText().toString();
            String textN2 ="by"+nickname.getText().toString();
            int fontSize = 20;
            float leading = 1.2f * fontSize; // 이건 위아래 간격인듯
            contentStream.beginText();
            contentStream.setFont(font2, fontSize);
            contentStream.newLineAtOffset(text_left, y_adjusted-160);//원래 20
            contentStream.showText(textN);
            contentStream.endText();
            contentStream.beginText();
            contentStream.newLineAtOffset(text_left, y_adjusted-180);//원래 20
            contentStream.showText(textN2);
            contentStream.endText();
            contentStream.close();

            Log.v("pdfrootpathdd" , root.getAbsolutePath());
            String path = root.getAbsolutePath() + "/"+date.getText().toString()+"2.pdf";
            Log.v("pdfrootpathdd2" , path);
            document.save(path);
            document.close();

            return path;

        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while creating PDF", e);
        }

        return "error";
    }

    class SaveTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String path = createPdf();
            return path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(SelectedUserDiaryVIewActivity.this, "잠시 기다려주세요.", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);

            Toast.makeText(SelectedUserDiaryVIewActivity.this, path+"에 PDF 파일로 저장했습니다.", Toast.LENGTH_LONG).show();
        }

    }

    class SaveTask2 extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String path = createPdf2();
            return path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(SelectedUserDiaryVIewActivity.this, "잠시 기다리세요.", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);

            Toast.makeText(SelectedUserDiaryVIewActivity.this, path+"에 PDF 파일로 저장했습니다.", Toast.LENGTH_LONG).show();
        }

    }

    //남은 PDF 갯수 확인
    private void CheckCountPDF() {
        FunctionCountDTO dto = new FunctionCountDTO();
        dto.setUserid(loginId);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> CheckCountPDF = NetRetrofit.getInstance().getService().CheckCountPDF(objJson);
        CheckCountPDF.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int result = Integer.parseInt(response.body().string());

                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectedUserDiaryVIewActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.purchasedpdf, null);
                    builder.setView(view);

                    final TextView snack = (TextView) view.findViewById(R.id.Tx_snack);
                    final TextView Tx_use = (TextView) view.findViewById(R.id.Tx_use);
                    final TextView Tx_cancel = (TextView) view.findViewById(R.id.Tx_cancel);
                    final AlertDialog dialog = builder.create();
                    String pdf = String.valueOf(result);
                    snack.setText(pdf);

                    //구매
                    Tx_use.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (result == 0) {
                                Toast.makeText(getApplicationContext(), "PDF 변한권을 구매해 주세요", Toast.LENGTH_SHORT).show();
                            } else {
                                if(imageName.length()==0){
                                    final SelectedUserDiaryVIewActivity.SaveTask saveTask = new SelectedUserDiaryVIewActivity.SaveTask();
                                    saveTask.execute();
                                }else{
                                    final SelectedUserDiaryVIewActivity.SaveTask saveTask = new SelectedUserDiaryVIewActivity.SaveTask();
                                    saveTask.execute();
                                    final SelectedUserDiaryVIewActivity.SaveTask2 saveTask2 = new SelectedUserDiaryVIewActivity.SaveTask2();
                                    saveTask2.execute();
                                }

                                dialog.dismiss();
                            }
                        }
                    });

                    Tx_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("남은 PDF권 가져오기 실패", t.getMessage());
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