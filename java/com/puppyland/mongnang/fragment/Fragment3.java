package com.puppyland.mongnang.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.airbnb.lottie.LottieAnimationView;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
import com.puppyland.mongnang.DTO.EncryptionDTO;
import com.puppyland.mongnang.DTO.FollowDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.VideoDTO;
import com.puppyland.mongnang.DTO.WarningDTO;
import com.puppyland.mongnang.Encryption.EncryptionUtil;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.SelectedUserDiaryActivity;
import com.puppyland.mongnang.contract.BoardContract;
import com.puppyland.mongnang.contract.VideoContract;
import com.puppyland.mongnang.presenter.VideoPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Member;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class Fragment3 extends Fragment implements BoardContract.view, Serializable, VideoContract.view {


    public Fragment3() {
    }

    //동영상 업로드
    long position;
    public static Context context2;
    public static Fragment3 mContext;
    private final int SELECT_MOVIE = 2;
    private String img_path = new String();
    private String serverURL = "http://192.168.219.100:8092/upload2.jsp";  //<<서버주소
    private String MOVIE_URL = "http://192.168.219.100:8092/upload/videoupload/";
    private final int REQ_CODE_SELECT_IMAGE = 100;
    String path, name, uriId, userID, getVideoName, nickname, getnickname, videouserID, access, secret, key;
    TextView Tx_id, videoLike, tooltip1, tooltip2, tooltip3, tooltip4;
    Uri uuu;
    //신고
    private int result; //신고
    int getLike;

    String daccess = null;
    String dsecret = null;

    CircleImageView userProfileImage;
    //조하요
    private boolean like;
    private int var;
    //팔로우 체크
    private boolean follow;
    LottieAnimationView animationView;


    int rand;

    //토톡 텍스트
    TextView todogText;

    //동영상 재생
    private PlayerView exoPlayerView;
    private SimpleExoPlayer player;
    private Boolean playWhenReady = true;
    private int currentWindow = 0;
    private Long playbackPosition = 0L;
    LinearLayout profileCard;

    //팝업 광고창 체크
    private int check;
    private InterstitialAd mInterstitialAd;

    private int doubleClickFlag = 0;
    private final long CLICK_DELAY = 250;
    File localImgFile;

    RelativeLayout linearLayout;

    TrackSelector trackSelector;
    DefaultBandwidthMeter defaultBandwidthMeter;

    VideoContract.presenter vpresenter;
    FirebaseStorage storage;
    //플로팅 버튼
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3, fab4;

    AWSCredentials awsCredentials;
    AmazonS3Client s3Client;
    TransferUtility transferUtility;
    private AVLoadingIndicatorView avLoadingIndicatorView;

    LoadControl loadControl;
    //Minimum Video you want to buffer while Playing
    private int MIN_BUFFER_DURATION = 2000;
    //Max Video you want to buffer during PlayBack
    private int MAX_BUFFER_DURATION = 5000;
    //Min Video you want to buffer before start Playing it
    private int MIN_PLAYBACK_START_BUFFER = 1500;
    //Min video You want to buffer when user resumes video
    private int MIN_PLAYBACK_RESUME_BUFFER = 2000;

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
        userID = sharedPreferences.getString("id", "");
        // view1.

    /*    EncryptionUtil encryptionUtil = new EncryptionUtil();
        MemberDTO dto = new MemberDTO();
        dto.setUserId(userID);
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);
        Call<EncryptionDTO> TimeCheckDelete = NetRetrofit.getInstance().getService().Encryption(objJson);
        TimeCheckDelete.enqueue(new Callback<EncryptionDTO>() {
            @Override
            public void onResponse(Call<EncryptionDTO> call, Response<EncryptionDTO> response) {
                EncryptionDTO encryptionDTO = response.body();
                access = encryptionDTO.getF3accessKey();
                secret = encryptionDTO.getF3secretKey();
                key = encryptionDTO.getKey();
                try {
                    daccess = encryptionUtil.decAES(access, key);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    dsecret = encryptionUtil.decAES(secret, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                awsCredentials = new BasicAWSCredentials(daccess, dsecret);
                s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));
                s3Client.setEndpoint("http://kr.object.iwinv.kr/");
            }*//*

            @Override
            public void onFailure(Call<EncryptionDTO> call, Throwable t) {
                Log.v("keykkkkeky", t.getMessage());
            }
        });*/


        loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, 16))
                .setBufferDurationsMs(MIN_BUFFER_DURATION,
                        MAX_BUFFER_DURATION,
                        MIN_PLAYBACK_START_BUFFER,
                        MIN_PLAYBACK_RESUME_BUFFER)
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl();


        //storage
        storage = FirebaseStorage.getInstance();

        //좋아요 갯수
        videoLike = view.findViewById(R.id.videoLike);

        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) view.findViewById(R.id.fab4);
        //로딩창
        avLoadingIndicatorView = view.findViewById(R.id.f3_avi);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 신고
                anim();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 갤러리
                anim();
                Warning();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() { // 업로드
            @Override
            public void onClick(View v) {
                anim();
                linearLayout.setClickable(false);
                Random random = new Random();
                rand = random.nextInt(9999);
                todogText.setVisibility(View.VISIBLE);
                //동영상 업로드
                if (name != null) {
                    compress();
                    //DoFileUpload(serverURL, path);
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "업로드할 파일을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() { //동영상 선택
            @Override
            public void onClick(View v) {
                anim();
                doSelectMovie();
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkShare();
            }
        });

        tooltip1 = view.findViewById(R.id.tooltip1);
        tooltip2 = view.findViewById(R.id.tooltip2);
        tooltip3 = view.findViewById(R.id.tooltip3);
        tooltip4 = view.findViewById(R.id.tooltip4);
        //팝업 광고창 체크
        check = 0;
        profileCard = view.findViewById(R.id.profileCard);
        userProfileImage = view.findViewById(R.id.userProfileImage);

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-1267905668913700/6920223884");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                //player.setPlayWhenReady(playWhenReady);
                player.setPlayWhenReady(false);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        final long defaultMaxInitialBitrate = Integer.MAX_VALUE;
        defaultBandwidthMeter = new DefaultBandwidthMeter.Builder(getContext()).setInitialBitrateEstimate(defaultMaxInitialBitrate).build();
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(defaultBandwidthMeter);
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);


        //닉네임
        SharedPreferences sharedPreferences2 = getContext().getSharedPreferences("nickname", MODE_PRIVATE);
        nickname = sharedPreferences2.getString("nickname", userID);
        Log.d("@@@nick", nickname);

        //닉네임
        Tx_id = view.findViewById(R.id.Tx_id);
        Tx_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), SelectedUserDiaryActivity.class);
                intent.putExtra("selectedId", videouserID);
                intent.putExtra("selectedNickname", getnickname);
                startActivity(intent);
            }
        });

        //토독 텍스트
        todogText = view.findViewById(R.id.todogText);
        vpresenter = new VideoPresenter(this);

        //동영상 실행
        exoPlayerView = view.findViewById(R.id.Videoview);

        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
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

        linearLayout = view.findViewById(R.id.VideoLinear);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileCard.setVisibility(View.VISIBLE);
                todogText.setVisibility(View.GONE); //누르면 알림 텍스트가 사라짐
                doubleClickFlag++;
                Handler handler = new Handler();
                Runnable clickRunnable = new Runnable() {
                    @Override
                    public void run() {
                        doubleClickFlag = 0;
                    }
                };
                if (doubleClickFlag == 1) {
                    handler.postDelayed(clickRunnable, CLICK_DELAY);
                } else if (doubleClickFlag == 2) {
                    //로딩창
                    avLoadingIndicatorView.setVisibility(View.VISIBLE);
                    avLoadingIndicatorView.smoothToShow();

                    doubleClickFlag = 0;
                    initializePlayer();
                    animationView.setVisibility(View.VISIBLE);
                    videoLike.setVisibility(View.VISIBLE);
                    animationView.setProgress(0);
                }

            }
        });

        //좋아요
        animationView = (LottieAnimationView) view.findViewById(R.id.Lottie);
        animationView.setAnimation("lf30_editor_yjixlnlv.json");
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like == false) {
                    getLike = getLike - 1;
                    String sublike = String.valueOf(getLike);
                    videoLike.setText(sublike);

                    vpresenter.UnLike(var, userID);
                    vpresenter.subtractLike(getVideoName);
                    animationView.setProgress(0);

                    like = true;
                } else {
                    getLike = getLike + 1;
                    String pluslike = String.valueOf(getLike);
                    videoLike.setText(pluslike);
                    animationView.playAnimation();
                    vpresenter.UpdateLike(getVideoName);
                    vpresenter.InsertLike(userID, var);
                    if (!userID.equals(videouserID)) { // videoId 랑 로그인한 사람의 아이디인 userID가 다르면
                        getDeviceId(videouserID, 7, String.valueOf(var));
                    }
                    like = false;
                }
            }
        });

        return view;
    }

    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            tooltip1.setVisibility(View.GONE);
            tooltip2.setVisibility(View.GONE);
            tooltip3.setVisibility(View.GONE);
            tooltip4.setVisibility(View.GONE);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            tooltip1.setVisibility(View.VISIBLE);
            tooltip2.setVisibility(View.VISIBLE);
            tooltip3.setVisibility(View.VISIBLE);
            tooltip4.setVisibility(View.VISIBLE);
            isFabOpen = true;
        }
    }

    // 동영상선택
    private void doSelectMovie() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivityForResult(i, SELECT_MOVIE);
        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    //클립보드 공유하기
    private void linkShare() {

        Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
        Sharing_intent.setType("text/plain");

        String Test_Message = String.valueOf(uuu);

        Sharing_intent.putExtra(Intent.EXTRA_TEXT, Test_Message);

        Intent Sharing = Intent.createChooser(Sharing_intent, "공유하기");
        startActivity(Sharing);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_MOVIE) {

                // 업로드 안하고 새로 했을때 기존에 있던 파일은 지워야해서
                Uri uri = data.getData();
                path = getPath(getContext().getApplicationContext(), uri);

                name = getName(uri);
                String filesize = getSize(uri);
                //     Log.v("ekeekkekek" , asdf);//여기 파일 크기 넘어옴

                if (Integer.parseInt(filesize) < 50000000) {
                    if (uri != null && path != null) {

                        InputStream in = null;//src
                        try {
                            in = getContext().getApplicationContext().getContentResolver().openInputStream(uri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        String extension = path.substring(path.lastIndexOf("."));
                        localImgFile = new File(getContext().getApplicationContext().getFilesDir(), name);

                        if (in != null) {
                            try {
                                OutputStream out = new FileOutputStream(localImgFile);//dst
                                try {
                                    // Transfer bytes from in to out
                                    byte[] buf = new byte[1024];
                                    int len;
                                    while ((len = in.read(buf)) > 0) {
                                        out.write(buf, 0, len);
                                    }
                                } finally {
                                    out.close();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        path = localImgFile.getAbsolutePath();
                        //   Log.v("whatpppath2", path);
                        Log.v("whatpppath2", localImgFile.toString());
                    }


                    uriId = getUriId(uri);
                    Toast.makeText(getContext().getApplicationContext(), "동영상 선택 완료! 업로드를 눌러주세요.", Toast.LENGTH_SHORT).show();
                    //         Log.e("###", "실제경로 : " + path + "\n파일명 : " + name + "\nuri : " + uri.toString() + "\nuri id : " + uriId);
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "50mb 이하의 영상을 골라주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    public void compress() {
        VideoCompressor.start(path, getContext().getApplicationContext().getCacheDir() + "compress" + String.valueOf(rand) + name, new CompressionListener() {
            @Override
            public void onStart() {
                // Compression start
                Toast.makeText(getContext(), "동영상 압축 중", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                // On Compression success
                todogText.setText("동영상 압축 성공 및 업로드 진행중.\n기다려주세요.");
                DoFileUpload(serverURL, getContext().getApplicationContext().getCacheDir() + "compress" + String.valueOf(rand) + name);

            }

            @Override
            public void onFailure(String failureMessage) {
                // On Failure
                Toast.makeText(getContext(), "압축에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(float v) {
                todogText.setText(String.valueOf(v));
            }

            @Override
            public void onCancelled() {
                VideoCompressor.cancel();
            }
        }, VideoQuality.HIGH, false, false);
    }


    public static String getPath(final Context context, final Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            Log.d("wheredofile", "들어옴10");
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Log.d("wheredofile", "들어옴11");
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    Toast.makeText(context, "Could not get file path. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                Log.d("wheredofile", "들어옴12");
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                Log.d("wheredofile", "들어옴13");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    Log.d("wheredofile", "들어옴14");
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    contentUri = MediaStore.Files.getContentUri("external");
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                Log.d("wheredofile", "들어옴15");
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.d("wheredofile", "들어옴20");
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getRemovableSDCardPath(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null)
            return storages[1].toString();
        else
            return "";
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        Log.d("wheredofile", "들어옴16");
        try {
            Log.d("wheredofile", "들어옴17");
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    // 파일명 찾기
    private String getName(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private String getSize(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns.SIZE};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // uri 아이디 찾기
    private String getUriId(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns._ID};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // 여기서부터가 jsp 이미지를 보내는 코드
    public void DoFileUpload(String apiUrl, String absolutePath) {
        Log.d("wheredofile", "들어옴1");
        //  HttpFileUpload(apiUrl, "", absolutePath);
        HttpFileUpload2();

    }

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    public void HttpFileUpload(String urlString, String params, String fileName) {
        try {


            FileInputStream mFileInputStream = new FileInputStream(fileName);
            storage.getReference().child("files").child("compress" + String.valueOf(rand) + name).putStream(mFileInputStream).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(getContext().getApplicationContext(), "업로드 성공:)", Toast.LENGTH_SHORT).show();
                }
            });

            // 동영상 정보 DB 입력
            vpresenter.InsertVideo(userID, "compress" + String.valueOf(rand) + name, nickname);


            String dir = getContext().getApplicationContext().getFilesDir().getAbsolutePath();
            File f0 = new File(dir, name);
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(f0.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            String ppp = saveBitmapToJpeg(getContext(), bitmap, "compress" + String.valueOf(rand) + name);
            FileInputStream mFileInputStream2 = new FileInputStream(ppp);
            URL connectUrl = new URL(urlString);
            //    Log.d("Stream", "mFileInputStream  is " + mFileInputStream2);
            //      Log.d("wheredofile", "들어옴2");
            // HttpURLConnection 통신

            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            //    Log.d("uri1", String.valueOf(conn));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setConnectTimeout(15000); // 타임아웃 추가해봤음
            //    Log.d("wheredofile", "들어옴3");
            // write2 data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + ppp + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            //  Log.d("wheredofile", "들어옴4");
            int bytesAvailable = mFileInputStream2.available();
            int maxBufferSize = 30 * 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream2.read(buffer, 0, bufferSize);

            //  Log.d("Read", "image byte is " + bytesRead);
            //  Log.d("wheredofile", "들어옴5");
            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream2.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream2.read(buffer, 0, bufferSize);
            }
            //    Log.d("wheredofile", "들어옴6");
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            //    Log.e("Written", "File is written");
            mFileInputStream2.close();
            dos.flush();
            // finish upload...

            // get response
            InputStream is = conn.getInputStream();

            StringBuffer b = new StringBuffer();
            for (int ch = 0; (ch = is.read()) != -1; ) {
                b.append((char) ch);
            }
            // Log.e("zxcv", "쓰임?");
            is.close();


            boolean d0 = f0.delete();

            if (d0) {
                Log.d("ddelelte", String.valueOf(d0));
            } else {
                Log.d("ddelelte", String.valueOf(d0));
            }

            File dele2 = new File(getContext().getApplicationContext().getCacheDir() + "compress" + String.valueOf(rand) + name);
            boolean d1 = dele2.delete();
            if (d1) {
                Log.d("ddelelte2", String.valueOf(d1));
            } else {
                Log.d("ddelelte2", String.valueOf(d1));
            }
        } catch (Exception e) {
            Log.d("Message", "exception " + e.getMessage());
        }
    }

    public void HttpFileUpload2() {
        try {

            File dele2 = new File(getContext().getApplicationContext().getCacheDir() + "compress" + String.valueOf(rand) + name);
            if (dele2 == null) {
                Log.d("nofile", "파일 없음");
            }
            transferUtility = TransferUtility.builder().s3Client(s3Client).context(getContext()).build();
            TransferObserver uploadObserver = transferUtility.upload("mongnyangvideo", "compress" + String.valueOf(rand) + name, dele2);
            uploadObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.d("viiiviiivi", "onStateChanged: " + id + ", " + state.toString());
                    if (state.toString().equals("COMPLETED")) {
                        try {
                            String dir = getContext().getApplicationContext().getFilesDir().getAbsolutePath();
                            File f0 = new File(dir, name);
                            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(f0.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                            String ppp = saveBitmapToJpeg(getContext(), bitmap, "compress" + String.valueOf(rand) + name);
                            FileInputStream mFileInputStream2 = new FileInputStream(ppp);
                            URL connectUrl = new URL(serverURL);
                            Log.d("Stream", "mFileInputStream  is " + mFileInputStream2);
                            Log.d("wheredofile", "들어옴2");
                            // HttpURLConnection 통신

                            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                            //   Log.d("uri1", String.valueOf(conn));
                            conn.setDoInput(true);
                            conn.setDoOutput(true);
                            conn.setUseCaches(false);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Connection", "Keep-Alive");
                            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                            conn.setConnectTimeout(15000); // 타임아웃 추가해봤음
                            //     Log.d("wheredofile", "들어옴3");
                            // write2 data
                            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + ppp + "\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            //      Log.d("wheredofile", "들어옴4");
                            int bytesAvailable = mFileInputStream2.available();
                            int maxBufferSize = 30 * 1024 * 1024;
                            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                            byte[] buffer = new byte[bufferSize];
                            int bytesRead = mFileInputStream2.read(buffer, 0, bufferSize);

                            //      Log.d("Read", "image byte is " + bytesRead);
                            //    Log.d("wheredofile", "들어옴5");
                            // read image
                            while (bytesRead > 0) {
                                dos.write(buffer, 0, bufferSize);
                                bytesAvailable = mFileInputStream2.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = mFileInputStream2.read(buffer, 0, bufferSize);
                            }
                            //    Log.d("wheredofile", "들어옴6");
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                            // close streams
                            //    Log.e("Written", "File is written");
                            mFileInputStream2.close();
                            dos.flush();
                            // finish upload...

                            // get response
                            InputStream is = conn.getInputStream();

                            StringBuffer b = new StringBuffer();
                            for (int ch = 0; (ch = is.read()) != -1; ) {
                                b.append((char) ch);
                            }
                            //         Log.e("zxcv", "쓰임?");
                            is.close();


                            boolean d0 = f0.delete();

                            if (d0) {
                                Log.d("ddelelte", String.valueOf(d0));
                            } else {
                                Log.d("ddelelte", String.valueOf(d0));
                            }

                            boolean d1 = dele2.delete();
                            if (d1) {
                                Log.d("ddelelte2", String.valueOf(d1));
                            } else {
                                Log.d("ddelelte2", String.valueOf(d1));
                            }

                            // 동영상 정보 DB 입력
                            vpresenter.InsertVideo(userID, "compress" + String.valueOf(rand) + name, nickname);

                            todogText.setText("업로드를 완료했습니다.");
                        } catch (Exception e) {

                        }

                    }

                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                    Log.d("viiiviiivi", "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e("viiiviiivi", ex.getMessage());
                }
            });

            linearLayout.setClickable(true);
        } catch (Exception e) {
            Log.d("Message", "exception " + e.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("efefwgwg", "스탑");
        releasePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(context2, trackSelector, loadControl);
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
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    private void initializePlayer() {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();

        object.addProperty("send", "1");
        String objJson = gson.toJson(object);

        Call<VideoDTO> getRandVideo = NetRetrofit.getInstance().getService().getRandVideo(objJson);
        getRandVideo.enqueue(new Callback<VideoDTO>() {
            @Override
            public void onResponse(Call<VideoDTO> call, Response<VideoDTO> response) {
                if (response.isSuccessful()) {
                    VideoDTO videoDTO = response.body();
                    getVideoName = videoDTO.getVideoname();
                    Tx_id.setText(videoDTO.getNickname());
                    getnickname = response.body().getNickname();
                    var = response.body().getVno();
                    videouserID = response.body().getUserid();
                    getLike = response.body().getLikey();

                    String getvideoLike = String.valueOf(response.body().getLikey());
                    videoLike.setText(getvideoLike);

                    getmyImgName(videouserID);
                    //좋아요 체크
                    CheckLike(var);
                    //팝업 광고창 체크
                    check = check + 1;

                    String vno = String.valueOf(var);
                    WarningDTO dto = new WarningDTO();
                    dto.setNo(vno);
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

                                    if (check % 5 == 0 && check != 0) {
                                        player.setPlayWhenReady(false);
                                        ShowAd();

                                    } else {
                                        if (result == 1) { // 신고한거면 정지
                                            player.setPlayWhenReady(false);
                                            Toast.makeText(getContext(), "확인 중인 영상입니다.", Toast.LENGTH_SHORT).show();
                                        } else { // 아니면 실행

                                            if (player == null) {
                                                player = ExoPlayerFactory.newSimpleInstance(requireContext(), trackSelector);
                                                //플레이어 연결
                                                exoPlayerView.setPlayer(player);
                                                //컨트롤러 없애기
                                                exoPlayerView.setKeepScreenOn(true);
                                                exoPlayerView.setUseController(false);
                                                //사이즈 조절
                                                exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL); // or RESIZE_MODE_FILL
                                                //음량조절
                                                player.setVolume(5);
                                                //프레임 포지션 설정
                                                player.seekTo(currentWindow, playbackPosition);
                                            }
                                            SurfaceView view1 = (SurfaceView) exoPlayerView.getVideoSurfaceView();
                                            view1.setSecure(true);

                                            String sample = "https://kr.object.iwinv.kr/mongnyangvideo/" + getVideoName;
                                            uuu = Uri.parse(sample);
                                            MediaSource mediaSource = buildMediaSource(Uri.parse(sample));
                                            //prepare
                                            player.prepare(mediaSource, true, true);
                                            //start,stop
                                            player.setPlayWhenReady(playWhenReady);
                                            //stop loading
                                            avLoadingIndicatorView.hide();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), "신고 체크 실패. 확인중입니다.", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }

            @Override
            public void onFailure(Call<VideoDTO> call, Throwable t) {
                Log.e("getRandVideoFail", t.getMessage());
            }
        });
    }

    //내 사진 가져오기
    public void getmyImgName(String userID) {
        MemberDTO dto = new MemberDTO();
        dto.setUserId(userID);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        final Call<MemberDTO> getMyInfo = NetRetrofit.getInstance().getService().getMyInfo(objJson);
        getMyInfo.enqueue(new Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                if (response.isSuccessful()) {
                    MemberDTO dto = response.body();
                    Picasso.get().load("https://mongnyang.shop/upload/" + dto.getMemberimage()).error(R.color.color_ffffffff).fit().centerCrop().into(userProfileImage);

                }
            }

            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {
                Log.d("###fail", t.getMessage());
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
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private MediaSource buildMediaSource(Uri uri) {

        String userAgent = Util.getUserAgent(context2, "blackJin");

        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {

            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent, defaultBandwidthMeter))//테스트
                    .createMediaSource(uri);

        } else if (uri.getLastPathSegment().contains("m3u8")) {

            //com.google.android.exoplayer:exoplayer-hls 확장 라이브러리를 빌드 해야 합니다.
            return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);

        } else {

            return new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(getContext(), userAgent))
                    .createMediaSource(uri);
        }

    }

    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;

            todogText.setVisibility(View.VISIBLE);
            profileCard.setVisibility(View.GONE);
        }
    }

    private void ShowAd() {
        player.setPlayWhenReady(false); // 동영상 멈추게 하는거
        mInterstitialAd.show();
        //"ca-app-pub-3940256099942544/1033173712" 는 구글에서 테스트용으로 공개한 Test Ad Id - 배포시 실제 Id 로 변경 해야 함
        /*
            @안드로이드용 테스트광고 ID -- (https://developers.google.com/admob/unity/test-ads?hl=ko)
            - 배너 광고: ca-app-pub-3940256099942544/6300978111
            - 전면 광고: ca-app-pub-3940256099942544/1033173712
            - 보상형 동영상 광고: ca-app-pub-3940256099942544/5224354917
            - 네이티브 광고 고급형: ca-app-pub-3940256099942544/2247696110
        */
    }

    //신고
    private void Warning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.warningcheck, null);
        builder.setView(view);
        final TextView warning = (TextView) view.findViewById(R.id.btn_warning);
        final TextView cancel = (TextView) view.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();

        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vno = String.valueOf(var);

                WarningDTO dto = new WarningDTO();
                dto.setNo(vno);
                dto.setItem("비디오");
                dto.setWriteuserid(videouserID);
                dto.setId(userID);

                Gson gson = new Gson();
                String objJson = gson.toJson(dto);

                Call<ResponseBody> Warning = NetRetrofit.getInstance().getService().Warning(objJson);
                Warning.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(getContext(), "신고 완료.", Toast.LENGTH_SHORT).show();
                        player.setPlayWhenReady(false);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "신고 실패!< 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    public static String saveBitmapToJpeg(Context context, Bitmap bitmap, String name) {

        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로
        for (int i = 0; i < 4; i++) {
            name = name.substring(0, name.length() - 1); // 뒤에 .mp4 지우고
        }
        String fileName = name + ".jpg";  // 파일이름은 마음대로!

        File tempFile = new File(storage, fileName);

        try {
            tempFile.createNewFile();  // 파일을 생성해주고

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌

            out.close(); // 마무리로 닫아줍니다.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile.getAbsolutePath();   // 임시파일 저장경로를 리턴해주면 끝!
    }


    private void getDeviceId(String userid, final int flag, final String vno) { // 이 함수를 가져다 쓰기만하면 3개를 다 돌려막기 할 수 있음.

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

                    Call<ResponseBody> boardPushAlarm = NetRetrofit.getInstance().getService().PushAlarm(flag, objJson, vno);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        context2 = context;
    }

}