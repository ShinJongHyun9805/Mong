package com.puppyland.mongnang;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.ForecdTerminationService.ForecdTerminationService;
import com.puppyland.mongnang.contract.ChatuserListContract;
import com.puppyland.mongnang.fragment.Fragment3;
import com.puppyland.mongnang.fragment.SwipeViewPager;
import com.puppyland.mongnang.fragment.VPAdapter;
import com.puppyland.mongnang.fragment.Fragment2;
import com.puppyland.mongnang.presenter.ChatuserListPresenter;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements ChatuserListContract.View {

    private  Context mContext;

    ChatuserListContract.Presenter chatuserListPresenter;
    private Fragment fragmentSearch, fragmentCamera, fragmentCall;
    List<ChatUserDTO> prelistCheck;
    List<ChatUserDTO> prelistCheckNoAccept;
    ViewPager pager;
    private BackPressCloseHandler backPressCloseHandler;
    Button SearchButton;
    private FragmentManager fragmentManager;

    SharedPreferences sharedPreferences1;
    SharedPreferences sharedPreferences2;

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    //저장소 권한
    private static final int MY_PERMISSION_STORAGE = 1111;
    // 위치 거절했을때
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    String pushflag ="2";
    private dialog_native_ad dialog; // 광고 미리로드
    public static  String start ="1";
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        startService(new Intent(this, ForecdTerminationService.class));
        callPermission();

//진저브레드에서 부터 추가된 일종의 개발툴로 개발자가 실수하는 것들을 감지하고 해결 할 수 있도록 돕는 모드, (실제로 수정하지는 않음 단지 알려줌)
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        try {
            pushflag = intent.getStringExtra("pushflag");
            if(pushflag.equals("1")){
                initCreate();
                Intent pushintent = new Intent(MainActivity2.this , AlarmLogActivity.class);
                startActivity(pushintent);
            }else {
                pushflag = "2";
                initCreate();
            }
        }catch (Exception e){
            initCreate();

        }


    }

    public void initCreate(){
        mContext = getApplicationContext();
        dialog = new dialog_native_ad(MainActivity2.this);// 광고 미리로드

        chatuserListPresenter = new ChatuserListPresenter(this);
        backPressCloseHandler = new BackPressCloseHandler(this);

        SearchButton = findViewById(R.id.resultbutton); //검색실행 버튼

        pager = findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(2);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        Fragment2 fragment2 = new Fragment2();
        adapter.addItem(fragment2);

        Fragment3 fragment3 = new Fragment3();
        adapter.addItem(fragment3);

        pager.setAdapter(adapter);
        fragmentManager = getSupportFragmentManager();

        // 화면 전환 프래그먼트 선언 및 초기 화면 설정
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.viewpager, Fragment2.newInstance()).commit();

        //인디케이터
        SwipeViewPager vp = findViewById(R.id.viewpager);
        VPAdapter adapter1 = new VPAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter1);

        // 연동
        TabLayout tab = findViewById(R.id.tab);
        tab.setupWithViewPager(vp);

        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.hometab);
        images.add(R.drawable.pick8);
        images.add(R.drawable.videotab);


        tab.setTabTextColors(Color.parseColor("#FFAEAEAE"), Color.parseColor("#FFFFD600"));
        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#FFFFD600"), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#FFAEAEAE"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        for(int i=0; i<3; i++)
        {
            tab.getTabAt(i).setIcon(images.get(i));
        }
        tab.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#FFFFD600"), PorterDuff.Mode.SRC_IN);

        SharedPreferences sharedPreferences3 = getSharedPreferences("weatherFlag", MODE_PRIVATE);
        SharedPreferences.Editor editor3 = sharedPreferences3.edit();
        editor3.putBoolean("Flag", false);
        editor3.commit();

        prelistCheck = new ArrayList<ChatUserDTO>();
        prelistCheckNoAccept = new ArrayList<ChatUserDTO>();

        //밑에 레트로핏을 비동기로 바꿔서 onresponse 에서  응답오면 sharedPreferences1 여기에 넣는걸로 바꾸는게 좋을듯3듯
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", Activity.MODE_PRIVATE);
        String userID = sharedPreferences.getString("id", "");
        Log.v("userIDID", userID);

        SharedPreferences sf = getSharedPreferences("infocheck",MODE_PRIVATE);
        boolean infocheck = sf.getBoolean("infocheck", false);

        if (!infocheck) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
            LayoutInflater inflater2 = getLayoutInflater();
            View view2 = inflater2.inflate(R.layout.infocheck, null);
            builder.setView(view2);

            final TextView Tx_OK = (TextView) view2.findViewById(R.id.Tx_OK);
            final TextView Tx_cancel = (TextView) view2.findViewById(R.id.Tx_cancel);
            final AlertDialog dialog2 = builder.create();

            Tx_OK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.dismiss();

                    Intent intent = new Intent(MainActivity2.this, InformationActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);

                    SharedPreferences sharedPreferences2 = getSharedPreferences("infocheck",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences2.edit();
                    boolean infocheck = true;
                    editor.putBoolean("infocheck", infocheck);
                    editor.commit();
                }
            });

            Tx_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.dismiss();

                    SharedPreferences sharedPreferences2 = getSharedPreferences("infocheck",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences2.edit();
                    boolean infocheck = true;
                    editor.putBoolean("infocheck", infocheck);
                    editor.commit();
                }
            });
            dialog2.show();
        }
    }
    //위치정보 권한 요청
    private void callPermission() {
        //TODO : 처음에 권한 요청 창이 뜨면 if 문으로 무조건 들어감 체크 기능
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_ACCESS_FINE_LOCATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            //TODO : 위치정보 권한만 허용했을때 저장소 권한 띄우기
            isPermission = true;
            checkPermission();
        }
    }

    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //TODO : 위치정보 수락 눌렀을때  1000
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessFineLocation = true;
            //저장소 권한 요청
            checkPermission();
        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessCoarseLocation = true;
        } //TODO : 위치정보 권한 거절 눌렀을 때
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
            //저장소 권한 요청
            checkPermission();
        }
        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    //저장소 권한 요청
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            // TODO: 처음에 저장소 권한 거절하면 이창이 뜸.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다.\n몽냥몽냥 기능 사용을 원하시면\n설정에서 해당 권한을 허용하셔야 사용 가능합니다.")
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
                                //finish();
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



    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewpager, fragment).commit();      // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
    }



    //어댑터 안에서 각각의 아이템을 데이터로서 관리한다
    class PagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item) {
            items.add(item);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("weatherFlag", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Flag", false);
        Log.d("flagActivity", "False");
        editor.commit();

        SharedPreferences sharedPreferences2 = getSharedPreferences("wv", MODE_PRIVATE);
        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.clear();
        Log.d("acFlagExit", "종료");
        editor2.commit();

        clearApplicationCache(null);
    }

    private void clearApplicationCache(java.io.File dir){
        if(dir==null)
            dir = getCacheDir();
        else;
        if(dir==null)
            return;
        else;
        java.io.File[] children = dir.listFiles();
        try{
            for(int i=0;i<children.length;i++)
                if(children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else children[i].delete();
        }
        catch(Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    @Override
    public void onBackPressed() {
            dialog.show();
    }

}

