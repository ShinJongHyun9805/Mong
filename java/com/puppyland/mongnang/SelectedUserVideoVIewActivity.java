package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
import com.puppyland.mongnang.DTO.NotificationDTO;
import com.puppyland.mongnang.DTO.VideoDTO;
import com.puppyland.mongnang.DTO.WarningDTO;
import com.puppyland.mongnang.contract.NotificationContract;
import com.puppyland.mongnang.contract.VideoContract;
import com.puppyland.mongnang.presenter.NotificationPresenter;
import com.puppyland.mongnang.presenter.VideoPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectedUserVideoVIewActivity extends AppCompatActivity implements  VideoContract.view , NotificationContract.View{

    private final int SELECT_MOVIE = 2;
    private String img_path = new String();
    private String serverURL = "http://192.168.219.100:8092/upload2.jsp";  //<<서버주소
    private String MOVIE_URL = "http://192.168.219.100:8092/upload/videoupload/";
    String userID ,nickname , videoname , videoid;
    TextView Tx_id;

    //좋아요 관련
    private boolean like;
    private int vno;
    VideoContract.presenter vpresenter;
    NotificationContract.Presenter npresenter;
    //Minimum Video you want to buffer while Playing
    private int MIN_BUFFER_DURATION = 500;
    //Max Video you want to buffer during PlayBack
    private int MAX_BUFFER_DURATION = 1500;
    //Min Video you want to buffer before start Playing it
    private int MIN_PLAYBACK_START_BUFFER = 500;
    //Min video You want to buffer when user resumes video
    private int MIN_PLAYBACK_RESUME_BUFFER = 0;
    TrackSelector trackSelector;
    LoadControl loadControl;
    LottieAnimationView animationView;
    //동영상 재생
    private PlayerView exoPlayerView;
    private SimpleExoPlayer player;
    private Boolean playWhenReady = true;
    private int currentWindow = 0;
    private Long playbackPosition = 0L;

    Uri uuu;
    FirebaseStorage storage;
    int result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_selected_user_video_v_iew);
        vpresenter = new VideoPresenter(this);
        npresenter = new NotificationPresenter(this);
        storage = FirebaseStorage.getInstance();


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, 16))
                .setBufferDurationsMs(MIN_BUFFER_DURATION,
                        MAX_BUFFER_DURATION,
                        MIN_PLAYBACK_START_BUFFER,
                        MIN_PLAYBACK_RESUME_BUFFER)
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl();
        trackSelector = new DefaultTrackSelector();

        exoPlayerView = findViewById(R.id.Videoview);

        //로그인한 유저 아이디
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        userID = sharedPreferences.getString("id", "");

        //로그인한 유저 닉네임
        SharedPreferences sharedPreferences2 = getSharedPreferences("nickname", MODE_PRIVATE);
        nickname = sharedPreferences2.getString("nickname", userID);

        //닉네임
        Tx_id = findViewById(R.id.Tx_id);

        Intent intent = getIntent(); /*데이터 수신*/
        Tx_id.setText(intent.getExtras().getString("nickname"));
        videoname = intent.getExtras().getString("videoname");
        vno =  intent.getExtras().getInt("vno");
        videoid = intent.getExtras().getString("videoid");

        //좋아요 체크
        CheckLike(vno);


        //좋아요
        animationView = (LottieAnimationView)findViewById(R.id.Lottie);
        animationView.setAnimation("lf30_editor_yjixlnlv.json");
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like == false) {
                    vpresenter.UnLike(vno ,userID );
                    animationView.setProgress(0);

                    like = true;
                } else {
                    NotificationDTO dto = new NotificationDTO();
                    dto.setUserid(videoid);
                    dto.setNo(String.valueOf(vno));
                    dto.setItem("비디오");
                    dto.setDate("");
                    dto.setVideovideoname(videoname);
                    dto.setVideonickname(Tx_id.getText().toString());
                    npresenter.UpdateNotification(dto);
                    if(!userID.equals(videoid)){ // videoId 랑 로그인한 사람의 아이디인 userID가 다르면
                        getDeviceId(videoid ,7,String.valueOf(vno));
                    }
                    animationView.playAnimation();
                    vpresenter.UpdateLike(videoname);
                    vpresenter.InsertLike(userID, vno);

                    like = false;
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();
    }

    private void getDeviceId(String userid ,  final int flag ,final String vno) { // 이 함수를 가져다 쓰기만하면 3개를 다 돌려막기 할 수 있음.

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

                    Call<ResponseBody> boardPushAlarm = NetRetrofit.getInstance().getService().PushAlarm(flag,objJson , vno);
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

    private void CheckLike(int var) {
        VideoDTO dto = new VideoDTO();
        dto.setVno(var);
        dto.setUserid(userID);
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> CheckLike = NetRetrofit.getInstance().getService().CheckLike(objJson);
        CheckLike.enqueue(new Callback<ResponseBody>() {
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
            }
        });
    }


    private void initializePlayer() {
        if (player == null) {

            player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector, loadControl);
            //플레이어 연결
            exoPlayerView.setPlayer(player);
            //컨트롤러 없애기
            exoPlayerView.setUseController(false);
            //사이즈 조절
            exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL); // or RESIZE_MODE_FILL
            //음량조절
            player.setVolume(5);
            //프레임 포지션 설정
            player.seekTo(currentWindow, playbackPosition);
        }
        String sample = "https://kr.object.iwinv.kr/mongnyangvideo/"+videoname;
        MediaSource mediaSource = buildMediaSource(Uri.parse(sample));

        /********************************************************************************************************************************
         * 신고영상 체크
         ****************************************************************************************************************************** */
        WarningDTO dto = new WarningDTO();
        dto.setNo(String.valueOf(vno));
        dto.setItem("비디오");
        dto.setId(userID);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> VideoCheckWarning = NetRetrofit.getInstance().getService().VideoCheckWarning(objJson);
        VideoCheckWarning.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        result = Integer.parseInt(response.body().string());

                        if (result == 1) { // 신고한거면 정지
                            player.setPlayWhenReady(false);
                            Toast.makeText(SelectedUserVideoVIewActivity.this, "확인 중인 영상입니다.", Toast.LENGTH_SHORT).show();
                        } else { // 아니면 실행
                            //prepare
                            player.prepare(mediaSource, true, false);
                            //start,stop
                            player.setPlayWhenReady(playWhenReady);
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



    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(this, "blackJin");
        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {
            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else if (uri.getLastPathSegment().contains("m3u8")) {
            //com.google.android.exoplayer:exoplayer-hls 확장 라이브러리를 빌드 해야 합니다.
            return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else {
            return new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this, userAgent))
                    .createMediaSource(uri);
        }

    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();

            exoPlayerView.setPlayer(null);
            player.release();
            player = null;

        }
    }

}