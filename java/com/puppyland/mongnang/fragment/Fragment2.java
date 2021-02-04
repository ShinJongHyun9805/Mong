package com.puppyland.mongnang.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.LottieAnimationView;
import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.puppyland.mongnang.AlarmLogActivity;
import com.puppyland.mongnang.AlarmSetActivity;
import com.puppyland.mongnang.BoardActivtiy;
import com.puppyland.mongnang.ChatMainActivity;
import com.puppyland.mongnang.DTO.GpsTracker;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.TimeCheckDTO;
import com.puppyland.mongnang.DTO.WeatherDTO;
import com.puppyland.mongnang.GiveActivity;
import com.puppyland.mongnang.HospitalActivity;
import com.puppyland.mongnang.InformationActivity;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.NotiboardActivity;
import com.puppyland.mongnang.OneDayDecorator;
import com.puppyland.mongnang.OpenSourceActivity;
import com.puppyland.mongnang.PickActivity;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.ScheduleActivity;
import com.puppyland.mongnang.SearchActivity;
import com.puppyland.mongnang.SelectedUserDiaryActivity;
import com.puppyland.mongnang.StoreActivity;
import com.puppyland.mongnang.StoryActivity;
import com.puppyland.mongnang.contract.TimeCheckContract;
import com.puppyland.mongnang.contract.WeatherContract;
import com.puppyland.mongnang.presenter.TimeCheckPresenter;
import com.puppyland.mongnang.presenter.WeatherPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class Fragment2 extends Fragment implements WeatherContract.view, TimeCheckContract.view {//by 2020/08/01 신종현.

    public static Fragment2 newInstance() {
        return new Fragment2();
    }

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    //기상청 관련
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    LinearLayout exweather;
    private TextView Tx_check;
    //날씨 로티
    LottieAnimationView animationWeather, animationdog;

    public static Fragment2 mContext;// 이래야지 다른 프라그먼트에 있는 함수 호출 가능
    private Context context;
    WeatherDTO dto = new WeatherDTO();

    private Fragment fragmentSearch, fragmentCamera, fragmentCall;

    TextView wv, TT3H, textval, text3, textex, Tx_Schedule, Tx_alarm;


    WeatherContract.presenter weatherpresenter;
    boolean flag = false;
    TimeCheckContract.presenter timePresenter;
    Button search, myinfo, mydog, diary;
    private FragmentManager fragmentManager;

    //달력 관련
    MaterialCalendarView materialCalendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    //병원
    private LinearLayout LL_hospital, LL_Toy;

    //일정관리하는 Alert Dialogs
    MaterialAlertDialogBuilder builder;
    TextInputEditText name;
    TextInputEditText location;
    TextInputEditText time;

    AVLoadingIndicatorView avLoadingIndicatorView;
    boolean isFlag = false;
    private Fragment2.OnMyListener onMyListener;

    String rating_result = null;
    SharedPreferences sharedPreferences2;
    //동그란 버튼
    CircleImageView mydiary, myInfo, myDogInfo;
    private FloatingNavigationView mFloatingNavigationView;
    ImageView imageView;

    public MaterialAlertDialogBuilder getBuilder() {
        return this.builder;
    }

    // 화면 업데이트 되면 바꿀려고 쓰는거
    @Override
    public void UpdateCalendar(List<TimeCheckDTO> list) {

    }

    public interface OnMyListener {
        String onReceivedData(String userId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Context ctx = this.getActivity();
        builder = new MaterialAlertDialogBuilder(ctx);
        name = new TextInputEditText(ctx);
        location = new TextInputEditText(ctx);
        time = new TextInputEditText(ctx);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //     onMyListener = null;
    }


    public Fragment2() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        SharedPreferences sharedPreferences3 = getActivity().getSharedPreferences("Loginfile", MODE_PRIVATE);
        final String loginId = sharedPreferences3.getString("id", null);

        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //MainAcitivity2에서 넘오는 false값의 Flag로 최초 실행 확인
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("weatherFlag", MODE_PRIVATE);
        flag = sharedPreferences.getBoolean("Flag", false);

        fragmentSearch = new Fragment2();
        weatherpresenter = new WeatherPresenter(this);
        timePresenter = new TimeCheckPresenter(this);
        context = container.getContext();
        mContext = this;// 이래야지 다른 프라그먼트에 있는 함수 호출 가능

        sharedPreferences2 = getContext().getSharedPreferences("nickname", MODE_PRIVATE);
        String nickname = sharedPreferences2.getString("nickname", loginId);

        text3 = (TextView) view.findViewById(R.id.text3);
        text3.setText("안녕하세요 " + nickname + "님");

        LottieAnimationView animationView = (LottieAnimationView) view.findViewById(R.id.Lottie_dog1);
        animationView.setAnimation("30281-storyboard-icon.json");
        animationView.playAnimation();
        animationView.loop(true);
        animationView.setOnClickListener(new View.OnClickListener() { // 피드 눌렀을때 동작
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), SelectedUserDiaryActivity.class);
                intent.putExtra("selectedId", loginId);
                intent.putExtra("selectedNickname", nickname);
                startActivity(intent);
            }
        });

 /*       LottieAnimationView animationPick = (LottieAnimationView) view.findViewById(R.id.Lt_Pick);
        animationPick.setAnimation("30953-social-media-marketing.json");
        animationPick.playAnimation();
        animationPick.loop(true);
        animationPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), PickActivity.class);
                *//*intent.putExtra("id", loginId);*//*
                startActivity(intent);
            }
        });*/

        LottieAnimationView animationView2 = (LottieAnimationView) view.findViewById(R.id.Lottie_story);
        animationView2.setAnimation("23675-read-a-book.json");
        animationView2.playAnimation();
        animationView2.loop(true);
        animationView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StoryActivity.class);
                startActivity(intent);
            }
        });

        LottieAnimationView animationView5 = (LottieAnimationView) view.findViewById(R.id.Lottie_chat1);
        animationView5.setAnimation("26433-chit-chatting-rounded.json");
        animationView5.playAnimation();
        animationView5.loop(true);
        animationView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getContext(), ChatMainActivity.class);
                //LoginActivty에서 로그인한 회원 아이디 정보
                String userID3 = getActivity().getIntent().getStringExtra("id");//이건 정상적으로 넘어올때 id
                if (userID3 == null) {
                    SharedPreferences sharedPreferences3 = getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
                    String loginId3 = sharedPreferences3.getString("id", null);
                    intent3.putExtra("id3", loginId3);
                    startActivity(intent3);
                } else {
                    intent3.putExtra("id3", userID3);
                    startActivity(intent3);
                }
            }
        });

        animationdog = (LottieAnimationView) view.findViewById(R.id.Lottie_WeatherWalk);
        animationdog.setAnimation("9711-payment-from-illustration-animation.json");
        animationdog.playAnimation();
        animationdog.loop(true);


        LottieAnimationView animationBoard = (LottieAnimationView) view.findViewById(R.id.Lottie_board);
        animationBoard.setAnimation("lf30_editor_ngyvkn3r.json");
        animationBoard.playAnimation();
        animationBoard.loop(true);
        animationBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BoardActivtiy.class);
                startActivity(intent);
            }
        });

        LottieAnimationView animationView4 = (LottieAnimationView) view.findViewById(R.id.Lottie_dog2); // 여기가 이제 주변검색
        animationView4.setAnimation("24278-pet-lovers.json");
        animationView4.playAnimation();
        animationView4.loop(true);
        animationView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 주변검색 프라그먼트를 액티비티로 만들어서 여기다가 넣어야함
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        LottieAnimationView animationView6 = (LottieAnimationView) view.findViewById(R.id.Lottie_store);
        animationView6.setAnimation("21249-shopping-cart.json");
        animationView6.playAnimation();
        animationView6.loop(true);
        animationView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StoreActivity.class);
                startActivity(intent);
            }
        });

        //병원
        LottieAnimationView animationHospital = (LottieAnimationView) view.findViewById(R.id.Lottie_hospital);
        animationHospital.setAnimation("15411-angoamericana-1.json");
        animationHospital.playAnimation();
        animationHospital.loop(true);
        //병원 검색
        LL_hospital = view.findViewById(R.id.LL_hospital);
        LL_hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HospitalActivity.class);
                startActivity(intent);
            }
        });

        //장난감 후기
        LottieAnimationView animationToy = (LottieAnimationView) view.findViewById(R.id.Lottie2);
        animationToy.setAnimation("lf30_editor_yjixlnlv.json");
        animationToy.playAnimation();
        animationToy.loop(true);
        LL_Toy = view.findViewById(R.id.LL_toy);
        LL_Toy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GiveActivity.class);
                startActivity(intent);
            }
        });


        //기상청
        wv = (TextView) view.findViewById(R.id.weatherID);
        TT3H = (TextView) view.findViewById(R.id.T3H);
        textval = (TextView) view.findViewById(R.id.textval);
        exweather = view.findViewById(R.id.exweather);
        animationWeather = view.findViewById(R.id.Lottie_weather);
        Tx_check = view.findViewById(R.id.Tx_check);

        //로딩버튼
        avLoadingIndicatorView = view.findViewById(R.id.avi);

        //기상청 리니어 클릭했을 때 GPS 설정
        exweather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tx_check.setVisibility(View.GONE);
                animationdog.setVisibility(View.GONE);

                //GPS가 활성화 유무
                if (!checkLocationServicesStatus()) {
                    Log.d("###1", String.valueOf(flag));

                    //로딩창
                    avLoadingIndicatorView.setVisibility(View.VISIBLE);
                    avLoadingIndicatorView.smoothToShow();

                    Log.d("###2", String.valueOf(flag));

                    //GPS 활성화 유무 창
                    showDialogForLocationServiceSetting();

                    Log.d("###3", String.valueOf(flag));

                    if (flag != true) {
                        weatherRequest();

                        //flag 값 true 변경
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("weatherFlag", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("Flag", true);
                        editor.commit();
                        Log.d("###4", String.valueOf(flag));

                    }
                } else {

                    //로딩창
                    avLoadingIndicatorView.setVisibility(View.VISIBLE);
                    avLoadingIndicatorView.smoothToShow();

                    if (flag != true) {
                        restart();

                        //flag 값 true 변경
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("weatherFlag", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("Flag", true);
                        editor.commit();
                    }
                }
            }
        });


        Log.d("###5", String.valueOf(flag));
        initButton();
        mFloatingNavigationView = (FloatingNavigationView) view.findViewById(R.id.floating_navigation_view);
        mFloatingNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingNavigationView.open();
            }
        });
        mFloatingNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //  Snackbar.make((View) mFloatingNavigationView.getParent(), item.getTitle() + " Selected!", Snackbar.LENGTH_SHORT).show();
                if (item.getTitle().equals("프로필 관리")) {
                    Intent intent = new Intent(getContext(), InformationActivity.class);
                    intent.putExtra("id", loginId);
                    startActivity(intent);
                } else if (item.getTitle().equals("알람설정")) {
                    Intent intent = new Intent(getContext(), AlarmSetActivity.class);
                    startActivity(intent);
                } else if (item.getTitle().equals("일정관리")) {
                    Intent intent = new Intent(getContext(), ScheduleActivity.class);
                    startActivity(intent);
                } else if (item.getTitle().equals("공지사항")) {
                    Intent intent = new Intent(getContext(), NotiboardActivity.class);
                    startActivity(intent);
                } else if (item.getTitle().equals("Thanks to")) {
                    Intent intent = new Intent(getContext(), OpenSourceActivity.class);
                    startActivity(intent);
                } else if (item.getTitle().equals("알림")) {
                    Intent intent = new Intent(getContext(), AlarmLogActivity.class);
                    startActivity(intent);
                } else if (item.getTitle().equals("로그아웃")) {
                    SharedPreferences auto = getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = auto.edit();
                    editor.clear();
                    editor.commit();


                    SharedPreferences sharedPreferences3 = getContext().getSharedPreferences("ChatuserListfile", MODE_PRIVATE);
                    SharedPreferences.Editor editor23 = sharedPreferences3.edit();
                    editor23.clear();
                    editor23.commit(); // 로컬에 id 저장하는것.

                    SharedPreferences sharedPreferences4 = getContext().getSharedPreferences("ChatuserListfileNoAccept", MODE_PRIVATE);
                    SharedPreferences.Editor editor24 = sharedPreferences4.edit();
                    editor24.clear();
                    editor24.commit(); // 로컬에 id 저장하는것.


                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                    editor2.clear();
                    editor2.commit();

                    if (UserManagement.getInstance() != null) { // 카카오 로그인을 했던거면 이게 동작 아니면 그냥 원래 로그아웃대로
                        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    }

                    Toast.makeText(getContext(), "안녕히 가세요:)", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(Fragment2.this).commit();
                    fragmentManager.popBackStack();

                }
                mFloatingNavigationView.close();
                return true;
            }
        });
        return view;
    }

    private void getProfileImage(String selectedId, ImageView imageView2) {
        // 동그란 프로필 사진 가져오는 코드

        MemberDTO prodto = new MemberDTO();
        prodto.setUserId(selectedId);
        Gson progson = new Gson();
        String proobjJson = progson.toJson(prodto); // DTO 객체를 json 으로 변환
        Call<MemberDTO> getMyInfo = NetRetrofit.getInstance().getService().fileName1(proobjJson);
        getMyInfo.enqueue(new retrofit2.Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                Picasso.get().load("http://192.168.219.100:8092/upload/" + response.body().getMemberimage()).error(R.drawable.monglogo2).fit().centerInside().into(imageView2);
            }

            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Loginfile", MODE_PRIVATE);
        final String id = sharedPreferences.getString("id", "");

        functionCount_select_rating(id);

        sharedPreferences2 = getContext().getSharedPreferences("nickname", MODE_PRIVATE);
        String nickname = sharedPreferences2.getString("nickname", id);
        text3.setText("안녕하세요 " + nickname + "님");
    }


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다. 값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            //위도
            double latitude = location.getLatitude();
            //경도
            double longitude = location.getLongitude();
            //위치제공자
            String provider = location.getProvider();
            if (location == null) {
                Toast.makeText(getActivity(), "위치 정보를 켜 주시고, 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
            }

            // 위도 경도 값을 가지고 현재 위치 알려주는 부분
            gpsTracker = new GpsTracker(getActivity());
            getCurrentAddress(latitude, longitude);
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    //위도 경도 값을 가지고 현재 위치 알려주기 위해 가공하는 부분
    public String getCurrentAddress(double latitude, double longitude) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getActivity(), "위치정보를 받아오지 못했습니다.", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "위치정보를 받아오지 못했습니다.", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getActivity(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        String se = address.getAdminArea();
        String gu = String.valueOf(addresses.get(0).getSubLocality());
        String dong = address.getThoroughfare();

        //내 위치 주소
        weatherpresenter.Location(se, gu, dong);
        Log.d("!!!", se);
        Log.d("!!!", gu);
        Log.d("!!!", dong);

        return address.getAddressLine(0).toString() + "\n";
    }

    public void initButton() {
    /*
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StoreActivity.class);
                startActivity(intent);
            }
        });
*/
        /*
        stroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StoryActivity.class);
                startActivity(intent);
            }
        });*/

        /*
        mydiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DiaryActivity.class);
                startActivity(intent);
            }
        });*/

        /*
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BoardActivtiy.class);
                startActivity(intent);
            }
        });*/
        /*
        chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getContext(), ChatMainActivity.class);
                //LoginActivty에서 로그인한 회원 아이디 정보
                String userID3 = getActivity().getIntent().getStringExtra("id");//이건 정상적으로 넘어올때 id
                if (userID3 == null) {
                    SharedPreferences sharedPreferences3 = getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
                    String loginId3 = sharedPreferences3.getString("id", null);
                    intent3.putExtra("id3", loginId3);
                    startActivity(intent3);
                } else {
                    intent3.putExtra("id3", userID3);
                    startActivity(intent3);
                }
            }
        });*/

        /*
        myInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyinfoActivity.class);
                //LoginActivty에서 로그인한 회원 아이디 정보
                String userID = getActivity().getIntent().getStringExtra("id"); //이건 정상적으로 넘어올때 id //인증안된 상태에서 이미지액티비티에서 넘어오는건
                if (userID == null) {
                    //ID로 넘어옴
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
                    String loginId = sharedPreferences.getString("id", null);
                    intent.putExtra("id", loginId);
                    startActivity(intent);
                } else {
                    intent.putExtra("id", userID);
                    startActivity(intent);
                }
            }
        });*/

        /*
        myDogInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getContext(), DogActivity.class);
                //LoginActivty에서 로그인한 회원 아이디 정보
                String userID2 = getActivity().getIntent().getStringExtra("id");//이건 정상적으로 넘어올때 id
                if (userID2 == null) {
                    //ID로 넘어옴
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
                    String loginId2 = sharedPreferences.getString("id", null);

                    intent2.putExtra("id2", loginId2);
                    startActivity(intent2);
                } else {
                    intent2.putExtra("id2", userID2);
                    startActivity(intent2);
                }
            }
        });*/

    }


    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessFineLocation = true;
        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessCoarseLocation = true;
        }
        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    //위치정보 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_ACCESS_FINE_LOCATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는\n 위치 서비스가 필요합니다");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        callPermission();
                        return;
                    }
                }
                break;
        }
    }

    //GPS 활성화 체크
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    //서버로부터 가져오는 값 가공
    public void weatherValue(String res) {
        String result = res;
        String[] array = result.split(" ");
        dto.setPOP(array[0]);
        dto.setPTY(array[1]);
        dto.setREH(array[2]);
        dto.setSKY(array[3]);
        dto.setT3H(array[4]);

        Log.v("poppop", String.valueOf(dto.getPOP()));

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("wv", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("POP", dto.getPOP());
        editor.putString("PTY", dto.getPTY());
        editor.putString("REH", dto.getREH());
        editor.putString("SKY", dto.getSKY());
        editor.putString("T3H", dto.getT3H());
        editor.commit(); // 로컬에 id랑 암호화 된 비밀번호를 저장하는것.
    }

    //기상청
    public void weatherRequest() {
        // LocationManager 객체를 얻어온다
        final LocationManager lm = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        if (!flag) {
            Log.d("sjh", String.valueOf(flag));
            try {
                // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                        100, // 통지사이의 최소 시간간격 (miliSecond)
                        1, // 통지사이의 최소 변경거리 (m)
                        mLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                        100, // 통지사이의 최소 시간간격 (miliSecond)
                        1, // 통지사이의 최소 변경거리 (m)
                        mLocationListener);

            } catch (SecurityException ex) {
                Log.d("err", ex.getMessage());
            }

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    lm.removeUpdates(mLocationListener);
                    SharedPreferences sharedPreferences2 = getContext().getSharedPreferences("wv", MODE_PRIVATE);
                    String POP = sharedPreferences2.getString("POP", "수신 상태가 양호하지 않습니다.");
                    String PTY = sharedPreferences2.getString("PTY", "수신 상태가 양호하지 않습니다.");
                    String REH = sharedPreferences2.getString("REH", "수신 상태가 양호하지 않습니다.");
                    String SKY = sharedPreferences2.getString("SKY", "");
                    Log.d("sjhsky", SKY);
                    String T3H = sharedPreferences2.getString("T3H", "수신 상태가 양호하지 않습니다.");
                    wv.setVisibility(View.VISIBLE);
                    animationWeather.setVisibility(View.VISIBLE);

                    if (SKY.equals("")) {
                        TT3H.setText("수신 상태가 양호하지 않습니다.\n위치 정보 확인 후, 새로고침을 클릭 해주세요");
                    }

                    if (POP != null) {
                        if (PTY.equals("0") && SKY.equals("0")) {
                            wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                            TT3H.setText("기온 : " + T3H + " 'C");
                            textval.setText("산책가기 좋은 날씨입니다.");
                            animationWeather.setAnimation("14444-sunny.json");
                            animationWeather.playAnimation();
                        } else if (PTY.equals("0") && SKY.equals("1")) {
                            wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                            TT3H.setText("기온 : " + T3H + " 'C");
                            textval.setText("오늘 날씨가 맑아요");
                            animationWeather.setAnimation("14444-sunny.json");
                            animationWeather.playAnimation();
                            animationWeather.loop(true);
                        } else if (PTY.equals("0") && SKY.equals("3")) {
                            wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                            TT3H.setText("기온 : " + T3H + " 'C");
                            textval.setText("구름이 많아요");
                            animationWeather.setAnimation("18348-weather-cloudy.json");
                            animationWeather.playAnimation();
                            animationWeather.loop(true);
                        } else if (PTY.equals("0") && SKY.equals("4")) {
                            wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                            TT3H.setText("기온 : " + T3H + " 'C");
                            textval.setText("흐려여");
                            animationWeather.setAnimation("4795-weather-mist.json");
                            animationWeather.playAnimation();
                            animationWeather.loop(true);
                        } else if (PTY.equals("1")) {
                            wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                            TT3H.setText("기온 : " + T3H + " 'C");
                            textval.setText("비가 와요");
                            animationWeather.setAnimation("28137-weather-icon-drizzle.json");
                            animationWeather.playAnimation();
                            animationWeather.loop(true);
                        } else if (PTY.equals("2")) {
                            wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                            TT3H.setText("기온 : " + T3H + " 'C");
                            textval.setText("눈과 비가 같이 와요");
                        } else if (PTY.equals("3")) {
                            wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                            TT3H.setText("기온 : " + T3H + " 'C");
                            textval.setText("눈이 내려요");
                            animationWeather.setAnimation("12055-snowing.json");
                            animationWeather.playAnimation();
                            animationWeather.loop(true);
                        } else if (PTY.equals("4")) {
                            wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                            TT3H.setText("기온 : " + T3H + " 'C");
                            textval.setText("소나기가 와요");
                            animationWeather.setAnimation("28140-weather-icon-rain.json");
                            animationWeather.playAnimation();
                            animationWeather.loop(true);
                        } else if (PTY.equals("5")) {
                            wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                            TT3H.setText("기온 : " + T3H + " 'C");
                            textval.setText("빗방울이 살짝 와요");
                            animationWeather.setAnimation("11703-line-spray-spread.json");
                            animationWeather.playAnimation();
                            animationWeather.loop(true);
                        }
                    }
                    textval.setVisibility(View.VISIBLE);
                    wv.setVisibility(View.VISIBLE);
                    TT3H.setVisibility(View.VISIBLE);
                    avLoadingIndicatorView.hide();
                }
            }, 10000);

        } else {
            wv.setVisibility(View.VISIBLE);
            SharedPreferences sharedPreferences2 = getContext().getSharedPreferences("wv", MODE_PRIVATE);
            String POP = sharedPreferences2.getString("POP", "수신 상태가 양호하지 않습니다.");
            String PTY = sharedPreferences2.getString("PTY", "수신 상태가 양호하지 않습니다.");
            String REH = sharedPreferences2.getString("REH", "수신 상태가 양호하지 않습니다.");
            String SKY = sharedPreferences2.getString("SKY", "수신 상태가 양호하지 않습니다.");
            String T3H = sharedPreferences2.getString("T3H", "수신 상태가 양호하지 않습니다.");

            if (PTY.equals("0") && SKY.equals("0")) {
                wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                TT3H.setText("기온 : " + T3H + " 'C");
                textval.setText("거~ 산책가기 좋은 날씨다");
                animationWeather.setAnimation("14444-sunny.json");
                animationWeather.playAnimation();
            } else if (PTY.equals("0") && SKY.equals("1")) {
                wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                TT3H.setText("기온 : " + T3H + " 'C");
                textval.setText("오늘 날씨가 맑아여");
                animationWeather.setAnimation("14444-sunny.json");
                animationWeather.playAnimation();
                animationWeather.loop(true);
            } else if (PTY.equals("0") && SKY.equals("3")) {
                wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                TT3H.setText("기온 : " + T3H + " 'C");
                textval.setText("구름이 많아여");
                animationWeather.setAnimation("18348-weather-cloudy.json");
                animationWeather.playAnimation();
                animationWeather.loop(true);
            } else if (PTY.equals("0") && SKY.equals("4")) {
                wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                TT3H.setText("기온 : " + T3H + " 'C");
                textval.setText("흐려여");
                animationWeather.setAnimation("4795-weather-mist.json");
                animationWeather.playAnimation();
                animationWeather.loop(true);
            } else if (PTY.equals("1")) {
                wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                TT3H.setText("기온 : " + T3H + " 'C");
                textval.setText("비가 와여");
                animationWeather.setAnimation("28137-weather-icon-drizzle.json");
                animationWeather.playAnimation();
                animationWeather.loop(true);
            } else if (PTY.equals("2")) {
                wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                TT3H.setText("기온 : " + T3H + " 'C");
                textval.setText("눈과 비가 같이 와여");
            } else if (PTY.equals("3")) {
                wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                TT3H.setText("기온 : " + T3H + " 'C");
                textval.setText("눈이 내려여");
                animationWeather.setAnimation("12055-snowing.json");
                animationWeather.playAnimation();
                animationWeather.loop(true);
            } else if (PTY.equals("4")) {
                wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                TT3H.setText("기온 : " + T3H + " 'C");
                textval.setText("소나기가 와여");
                animationWeather.setAnimation("28140-weather-icon-rain.json");
                animationWeather.playAnimation();
                animationWeather.loop(true);
            } else if (PTY.equals("5")) {
                wv.setText("강수 확률 : " + POP + "% " + " 습도 : " + REH + "%");
                TT3H.setText("기온 : " + T3H + " 'C");
                textval.setText("빗방울이 살짝 와여");
                animationWeather.setAnimation("11703-line-spray-spread.json");
                animationWeather.playAnimation();
                animationWeather.loop(true);
            }
        }
    }

    //기상청 새로고침
    public void restart() {
        avLoadingIndicatorView.show();
        textval.setVisibility(View.GONE);
        wv.setVisibility(View.GONE);
        TT3H.setVisibility(View.GONE);
        animationWeather.setVisibility(View.GONE);

        weatherRequest();
    }

  /*
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        ArrayList<String> Time_Result;


        ApiSimulator(ArrayList<String> Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for (int i = 0; i < Time_Result.size(); i++) {
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result.get(i).split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year, month - 1, dayy);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);


            materialCalendarView.addDecorator(new EventDecorator(Color.BLACK, calendarDays, getActivity()));
        }
    }*/

    //회원 등급 가져오기
    public void functionCount_select_rating(String id) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        String objJson = gson.toJson(object);

        Call<ResponseBody> functionCount_select_rating = NetRetrofit.getInstance().getService().functionCount_select_rating(objJson);
        functionCount_select_rating.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        rating_result = response.body().string();
                        Log.d("###", rating_result);

                        SharedPreferences sharedPref = getActivity().getSharedPreferences("rating", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("rate", rating_result);
                        editor.commit();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

