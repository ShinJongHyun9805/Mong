package com.puppyland.mongnang;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.puppyland.mongnang.DTO.BoardDTO;
import com.puppyland.mongnang.DTO.CommentDTO;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.NotificationDTO;
import com.puppyland.mongnang.DTO.WarningDTO;
import com.puppyland.mongnang.board.LIstViewAdapter3;
import com.puppyland.mongnang.contract.BoardContract;
import com.puppyland.mongnang.contract.NotificationContract;
import com.puppyland.mongnang.presenter.BoardPresenter;
import com.puppyland.mongnang.presenter.NotificationPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetail extends AppCompatActivity implements BoardContract.view , NotificationContract.View {

    private String bno, ImageName, loginId , selectedId, selectedNickname , writeuserid;;
    private TextView Tx_user, Tx_detailreview, Tx_reviews, Tx_warning;;
    private EditText Et_title, Et_content, Et_comment;
    private ImageView photo, menu3dot , replygoCommitBtn , button7;
    private LinearLayout LL_img;
    private ScrollView scrollView;
    BoardContract.presenter bpresenter;
    NotificationContract.Presenter npresenter;
    CircleImageView userProfileImage;


    //사진 가져오기 및 입력
    private static final int MY_PERMISSION_STORAGE = 1111;
    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "http://192.168.219.100:8092/upload.jsp";  //<<서버주소
    private Bitmap image_bitmap_copy = null;
    private Bitmap image_bitmap = null;
    private String imageName = null;
    private String fileName = null;
    private String fileName2;
    private int result;
    //댓글 리스트
    private ListView listview;
    private LIstViewAdapter3 adapter;
    List<CommentDTO> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        bpresenter = new BoardPresenter(this);
        npresenter = new NotificationPresenter(this);
        //로그인한 유저 ID값
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        loginId = sharedPreferences.getString("id", null);
        //댓글 리스트
        listview = findViewById(R.id.list);
        list = new ArrayList<CommentDTO>();

        //앱 권한 관리
        checkPermission();

        //진저브레드에서 부터 추가된 일종의 개발툴로 개발자가 실수하는 것들을 감지하고 해결 할 수 있도록 돕는 모드, (실제로 수정하지는 않음 단지 알려줌)
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        //리스트뷰에서 넘어온 아이템 값
        Intent intent = getIntent(); /*데이터 수신*/


        //별명
        SharedPreferences sharedPreferences2 = getSharedPreferences("nickname", MODE_PRIVATE);
        final String nickname = sharedPreferences2.getString("nickname", "");

        bno = intent.getExtras().getString("bno");
      //  String title = intent.getExtras().getString("title");
        String user = intent.getExtras().getString("user");

        //조회수 증가
        bpresenter.updateHit(bno);

        //댓글가져오기
        getComment(bno);
        //신고글 체크
        CheckWarning();
        //제목
        Et_title = findViewById(R.id.Et_title);
        //글쓴이
        Tx_user = findViewById(R.id.Tx_user);
        //내용
        Et_content = findViewById(R.id.Et_content);
        //사진
        photo = findViewById(R.id.photo);
        //조회수
        Tx_reviews = findViewById(R.id.Tx_reviews);
        //이미지 영역 리니어아웃 아이디
        LL_img = findViewById(R.id.LL_img);
        //스크롤뷰
        scrollView = findViewById(R.id.scrollView);
        //상세보기안에 댓글갯수표기
        Tx_detailreview = findViewById(R.id.Tx_detailreview);
        // 동그란 프로필 사진
        userProfileImage = findViewById(R.id.userProfileImage);
        //뒤로가기 버튼
        button7 = findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //신고기능
        Tx_warning = findViewById(R.id.Tx_warning);
        Tx_warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Warning();
            }
        });

        //수정 삭제 기능 메뉴
        menu3dot = findViewById(R.id.menu3dot);
        menu3dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.menu_main, p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("저장")) {
                            String title = Et_title.getText().toString();
                            String content = Et_content.getText().toString();

                            if ((title == null || title.getBytes().length == 0 || title.getBytes().length < 5) || (content == null || content.getBytes().length == 0 || content.getBytes().length <= 10)) {
                                Toast.makeText(getApplicationContext(), "5글자 이상으로 적어주세요.", Toast.LENGTH_SHORT).show();
                            } else if (content.getBytes().length > 500) {
                                Toast.makeText(getApplicationContext(), "500자 이하로 작성해 주세요:(", Toast.LENGTH_SHORT).show();
                            } else {
                                if (ImageName == null) {
                                    //사진 업로드
                                    DoFileUpload(serverURL, img_path);

                                    bpresenter.UpdateBoard(bno, title, content, fileName2);
                                    Toast.makeText(BoardDetail.this, "수정 완료.", Toast.LENGTH_LONG).show();
                                    finish();

                                } else {
                                    //사진 업로드
                                    DoFileUpload(serverURL, img_path);

                                    bpresenter.UpdateBoard(bno, title, content, ImageName);
                                    Toast.makeText(BoardDetail.this, "수정 완료.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }

                        } else if (item.getTitle().equals("삭제")) {
                            bpresenter.DeleteBoard(bno);

                            Toast.makeText(BoardDetail.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (item.getTitle().equals("사진첨부")) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                        }
                        return false;
                    }
                });
                p.show();
            }
        });
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BoardDetail.this , userClickImageActivity.class);
                intent1.putExtra("id" ,selectedId);
                startActivity(intent1);
            }
        });

        Tx_user.setOnClickListener(new View.OnClickListener() { // 클릭하면 셀렉티드로 감
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SelectedUserDiaryActivity.class);
                intent.putExtra("selectedId" ,selectedId);
                intent.putExtra("selectedNickname",selectedNickname);
                v.getContext().startActivity(intent);
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
                        String content = Et_comment.getText().toString();
                        content = plzNoHacking(content);
                        if (content.equals("")) {
                            Toast.makeText(BoardDetail.this, "내용을 작성해 주세요.", Toast.LENGTH_LONG).show();
                        } else {
                            //댓글달기
                            bpresenter.InsertContent(bno, loginId, content, nickname);
                            //댓글 갯수
                            bpresenter.updateComment(bno, Tx_detailreview, listview);

                            getCountComment();
                            setListViewHeightBasedOnItems(listview);

                            NotificationDTO dto = new NotificationDTO();
                            dto.setUserid(selectedId);
                            dto.setNo(bno);
                            dto.setItem("게시판");
                            dto.setDate("");
                            dto.setNickname(selectedNickname);

                            npresenter.UpdateNotification(dto);
                            if(!selectedId.equals(loginId)) { // 글쓴이랑 로그인한 사람이랑 다를때만 푸시알람 보냄
                                getDeviceId(selectedId,1 , bno);
                            }
                            Toast.makeText(BoardDetail.this, "작성 완료", Toast.LENGTH_LONG).show();

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

        replygoCommitBtn = findViewById(R.id.replygoCommitBtn);
        replygoCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = Et_comment.getText().toString();
                content = plzNoHacking(content);
                if (content.equals("")) {
                    Toast.makeText(BoardDetail.this, "내용을 작성해 주세요.", Toast.LENGTH_LONG).show();
                } else {
                    //댓글달기
                    bpresenter.InsertContent(bno, loginId, content, nickname);
                    //댓글 갯수
                    bpresenter.updateComment(bno, Tx_detailreview, listview);
                    getCountComment();
                    setListViewHeightBasedOnItems(listview);

                    //여기에다가 tbl_notification 에 데이터를 넣는 작업을 해야한다.
                    // 그 사람의 알람 리스트를 만들어야하기 때문

                    NotificationDTO dto = new NotificationDTO();
                    dto.setUserid(selectedId);
                    dto.setNo(bno);
                    dto.setItem("게시판");
                    dto.setDate("");
                    dto.setNickname(selectedNickname);
                    npresenter.UpdateNotification(dto);
                    if(!selectedId.equals(loginId)) { // 글쓴이랑 로그인한 사람이랑 다를때만 푸시알람 보냄
                        getDeviceId(selectedId, 1, bno);
                    }
                    Toast.makeText(BoardDetail.this, "작성 완료", Toast.LENGTH_LONG).show();

                    Et_comment.setText(null);
                }
            }
        });



        if (loginId.equals(user)) {
            BoardDTO dto = new BoardDTO();
            dto.setBno(bno);

            Gson gson = new Gson();
            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

            Call<BoardDTO> boarddetailInfo = NetRetrofit.getInstance().getService().boarddetailInfo(objJson);
            boarddetailInfo.enqueue(new Callback<BoardDTO>() {
                @Override
                public void onResponse(Call<BoardDTO> call, Response<BoardDTO> response) {
                    BoardDTO dto2 = response.body();
                    //첨부파일 가져오기
                    fileName2 = dto2.getImg();

                    selectedId = dto2.getUserid(); //셀렉티드 할려고 아이디랑 닉네임
                    selectedNickname = dto2.getNickname();
                    getProfileImage(selectedId);
                    if (fileName2.equals("1")) {
                        Et_title.setText(dto2.getTitle());
                        Tx_user.setText(dto2.getNickname());
                        Et_content.setText(dto2.getBcontent());
                        writeuserid = dto2.getUserid();
                        Tx_detailreview.setText("댓글" + "(" + dto2.getCnt() + ")");

                        String reviews = String.valueOf(dto2.getHit());
                        Tx_reviews.setText("조회 " + reviews);
                    } else {
                        LL_img.setVisibility(View.VISIBLE);
                        writeuserid = dto2.getUserid();
                        Et_title.setText(dto2.getTitle());
                        Tx_user.setText(dto2.getNickname());
                        Et_content.setText(dto2.getBcontent());
                        Tx_detailreview.setText("댓글" + "(" + dto2.getCnt() + ")");

                        String reviews = String.valueOf(dto2.getHit());
                        Tx_reviews.setText("조회 " + reviews);

                        Picasso.get().load("http://192.168.219.100:8092/upload/" + fileName2).error(R.drawable.monglogo2).into(photo);
                    }
                }

                @Override
                public void onFailure(Call<BoardDTO> call, Throwable t) {
                    Log.d("###", t.getMessage());
                }
            });

        } else {
            //남의 글 제목 내용 수정 방지
            Et_title.setEnabled(false);
            Et_content.setEnabled(false);
            menu3dot.setVisibility(View.INVISIBLE);
            //menu3dot.setVisibility(View.GONE);

            BoardDTO dto = new BoardDTO();
            dto.setBno(bno);

            Gson gson = new Gson();
            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

            Call<BoardDTO> boarddetailInfo = NetRetrofit.getInstance().getService().boarddetailInfo(objJson);
            boarddetailInfo.enqueue(new Callback<BoardDTO>() {
                @Override
                public void onResponse(Call<BoardDTO> call, Response<BoardDTO> response) {
                    BoardDTO dto2 = response.body();

                    //첨부파일 가져오기
                    String fileName2 = dto2.getImg();
                    selectedId = dto2.getUserid(); //셀렉티드 할려고 아이디랑 닉네임
                    selectedNickname = dto2.getNickname();
                    getProfileImage(selectedId);
                    if (fileName2.equals("1")) {
                        Et_title.setText(dto2.getTitle());
                        Tx_user.setText(dto2.getNickname());
                        Et_content.setText(dto2.getBcontent());
                        Tx_detailreview.setText("댓글" + "(" + dto2.getCnt() + ")");
                        writeuserid = dto2.getUserid();
                        String reviews = String.valueOf(dto2.getHit());
                        Tx_reviews.setText("조회 " + reviews);
                    } else {
                        LL_img.setVisibility(View.VISIBLE);

                        Et_title.setText(dto2.getTitle());
                        Tx_user.setText(dto2.getNickname());
                        writeuserid = dto2.getUserid();
                        Et_content.setText(dto2.getBcontent());
                        Tx_detailreview.setText("댓글" + "(" + dto2.getCnt() + ")");

                        String reviews = String.valueOf(dto2.getHit());
                        Tx_reviews.setText("조회 " + reviews);

                        Picasso.get().load("http://192.168.219.100:8092/upload/" + fileName2).error(R.drawable.monglogo2).into(photo);
                    }
                }

                @Override
                public void onFailure(Call<BoardDTO> call, Throwable t) {
                    Log.d("###", t.getMessage());
                }
            });
        }
    }
    /*********************************************************************************************************************************************************
     * 신고
     ********************************************************************************************************************************************************/
    private void Warning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.warningcheck, null);
        builder.setView(view);
        final TextView warning = (TextView) view.findViewById(R.id.btn_warning);
        final TextView cancel = (TextView) view.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();

        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WarningDTO dto = new WarningDTO();
                dto.setNo(bno);
                dto.setItem("게시판");
                dto.setWriteuserid(writeuserid);
                dto.setId(loginId);

                Gson gson = new Gson();
                String objJson = gson.toJson(dto);

                Call<ResponseBody> Warning = NetRetrofit.getInstance().getService().Warning(objJson);
                Warning.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(getApplicationContext(), "신고 완료.", Toast.LENGTH_SHORT).show();
                        Tx_warning.setText("신고완료");
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "신고 실패!< 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    /*********************************************************************************************************************************************************
     * 신고글 체크
     ********************************************************************************************************************************************************/
    private void CheckWarning() {
        WarningDTO dto = new WarningDTO();
        dto.setNo(bno);
        dto.setItem("게시판");
        dto.setId(loginId);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> CheckWarning = NetRetrofit.getInstance().getService().CheckWarning(objJson);
        CheckWarning.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        result = Integer.parseInt(response.body().string());
                        if (result == 1) {
                            Tx_warning.setText("신고완료");
                            Tx_warning.setClickable(false);
                        } else {
                            Tx_warning.setText("신고");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "신고 체크 실패. 확인중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getDeviceId(String userid ,  final int flag ,final String bno) { // 이 함수를 가져다 쓰기만하면 3개를 다 돌려막기 할 수 있음.



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

                        Call<ResponseBody> boardPushAlarm = NetRetrofit.getInstance().getService().PushAlarm(flag,objJson , bno);
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
                Picasso.get().load("http://192.168.219.100:8092/upload/" + response.body().getMemberimage()).error(R.drawable.monglogo2).fit().into(userProfileImage);
            }
            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {

            }
        });
    }

    private void getComment(String bno) {

        CommentDTO dto = new CommentDTO();
        dto.setBno(bno);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        list = new ArrayList<CommentDTO>();

        Call<List<CommentDTO>> getComment = NetRetrofit.getInstance().getService().getComment(objJson);
        getComment.enqueue(new Callback<List<CommentDTO>>() {
            @Override
            public void onResponse(Call<List<CommentDTO>> call, Response<List<CommentDTO>> response) {
                if (response.isSuccessful()) {
                    list = response.body();

                    adapter = new LIstViewAdapter3();
                    listview.setAdapter(adapter);
                    try {
                        if (list != null) {
                            for (CommentDTO commentDTO : list) { // 리스트로 담기는// 것들 하나씩 출력
                                adapter.addItem(commentDTO.getId(), commentDTO.getContent(), commentDTO.getNickname());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setListViewHeightBasedOnItems(listview);
                }
            }

            @Override
            public void onFailure(Call<List<CommentDTO>> call, Throwable t) {
                Log.d("###fail", t.getMessage());
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

    //앱 권한 요청
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    LL_img.setVisibility(View.VISIBLE);
                    int reWidth = (int) (getWindowManager().getDefaultDisplay().getWidth());
                    int reHeight = (int) (getWindowManager().getDefaultDisplay().getHeight());
                    img_path = getImagePathToUri(data.getData()); //이미지의 URI를 얻어 경로값으로 반환.

                    //이미지를 비트맵형식으로 반환
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    //image_bitmap 으로 받아온 이미지의 사이즈를 임의적으로 조절함. width: 400 , height: 300
                    image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, reWidth, reHeight, true);
                    ImageView image = findViewById(R.id.photo);  //이미지를 띄울 위젯 ID값
                    image.setImageBitmap(image_bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//end of onActivityResult()

    public String getImagePathToUri(Uri data) {
        //사용자가 선택한 이미지의 정보를 받아옴
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        //이미지의 경로 값
        String imgPath = cursor.getString(column_index);
        Log.d("imgPath", imgPath);

        //이미지의 이름 값
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        Toast.makeText(BoardDetail.this, "이미지 이름 : " + imgName, Toast.LENGTH_SHORT).show();
        this.imageName = imgName;
        ImageName = imgName;
        Log.d("###", ImageName);

        return imgPath;
    }//end of getImagePathToUri()


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

            // write2 data
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



    /*********************************************************************************************************************************************************
     * 댓글 갯수 새로고침
     ********************************************************************************************************************************************************/
    private void getCountComment() {
        BoardDTO dto = new BoardDTO();
        dto.setBno(bno);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<BoardDTO> getCountComment = NetRetrofit.getInstance().getService().getCountComment(objJson);
        getCountComment.enqueue(new Callback<BoardDTO>() {
            @Override
            public void onResponse(Call<BoardDTO> call, Response<BoardDTO> response) {
                BoardDTO dto2 = response.body();

                Tx_detailreview.setText("댓글" + "(" + dto2.getCnt() + ")");
            }

            @Override
            public void onFailure(Call<BoardDTO> call, Throwable t) {
                Log.d("댓글 갯수 새로고침 실패", t.getMessage());
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