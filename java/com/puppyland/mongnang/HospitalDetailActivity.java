package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.HospitalDTO;
import com.puppyland.mongnang.DTO.HospitalReviewDTO;
import com.puppyland.mongnang.hospital.ListViewAdapterHospitaldetail;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalDetailActivity extends AppCompatActivity {

    //리스트뷰 한칸 값들
    private String name, num, address;
    //상세보기 값들
    private TextView Tx_back, Tx_hosname, Tx_hosnumber, Tx_hosaddress, Tx_detailreview;
    //후기 작성
    private EditText Et_hoscomment;
    //닉네임
    private String nickname, loginId;
    //리스트
    private List<HospitalReviewDTO> list;
    private ListView hoslist;
    private ListViewAdapterHospitaldetail adapter;
    //전화걸기
    private LinearLayout LL_CallHospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_detail);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //별명
        SharedPreferences sharedPreferences = getSharedPreferences("nickname", MODE_PRIVATE);
        nickname = sharedPreferences.getString("nickname", "");

        //로그인한 유저 ID값
        SharedPreferences sharedPreferences2 = getSharedPreferences("Loginfile", MODE_PRIVATE);
        loginId = sharedPreferences2.getString("id", "qwer@naver.com");

        //리스트뷰에서 넘어온 아이템 값
        Intent intent = getIntent();
        name = intent.getExtras().getString("name");
        num = intent.getExtras().getString("num");
        address = intent.getExtras().getString("address");

        //전화걸기
        LL_CallHospital = findViewById(R.id.LL_CallHospital);
        LL_CallHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = "tel:" + num;
                Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
                startActivity(tt);
            }
        });

        //후기 가져오기
        getReviews();

        //병원 정보 값
        Tx_hosname = findViewById(R.id.Tx_hosname);
        Tx_hosname.setText(name);
        Tx_hosnumber = findViewById(R.id.Tx_hosnumber);
        Tx_hosnumber.setText(num);
        Tx_hosaddress = findViewById(R.id.Tx_hosaddress);
        Tx_hosaddress.setText(address);

        //후기
        Tx_detailreview = findViewById(R.id.Tx_detailreview);
        hoslist = findViewById(R.id.hoslist);

        Et_hoscomment = findViewById(R.id.Et_hoscomment);
        Et_hoscomment.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        Et_hoscomment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        String content = Et_hoscomment.getText().toString();
                        content = plzNoHacking(content);

                        if (content.equals("")) {
                            Toast.makeText(HospitalDetailActivity.this, "내용을 작성해 주세요.", Toast.LENGTH_LONG).show();
                        } else {
                            //후기작성
                            WriteReview(content);
                            //후기 갯수
                            ReviewCount();
                            //리스트뷰 크기조절
                            setListViewHeightBasedOnItems(hoslist);
                            //입력창 초기화
                            Et_hoscomment.setText(null);
                            break;
                        }
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });


        //뒤로가기
        Tx_back = findViewById(R.id.Tx_back);
        Tx_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }/////////////////////////////////////////////////////////////// onCreate 끝


    /*******************************************************************************************************************************************
     * 후기 가져오기
     * *****************************************************************************************************************************************/
    private void getReviews(){
        HospitalReviewDTO dto = new HospitalReviewDTO();
        dto.setNumber(num);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        list = new ArrayList<HospitalReviewDTO>();

        Call<List<HospitalReviewDTO>> getHosReviews = NetRetrofit.getInstance().getService().getHosReviews(objJson);
        getHosReviews.enqueue(new Callback<List<HospitalReviewDTO>>() {
            @Override
            public void onResponse(Call<List<HospitalReviewDTO>> call, Response<List<HospitalReviewDTO>> response) {
                if(response.isSuccessful()){
                    list = response.body();

                    adapter = new ListViewAdapterHospitaldetail();
                    hoslist.setAdapter(adapter);
                    try{
                        if(list != null){
                            for(HospitalReviewDTO hospitalReviewDTO : list){
                                adapter.addItem(hospitalReviewDTO.getContents(), hospitalReviewDTO.getNickname());
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }
                    setListViewHeightBasedOnItems(hoslist);
                }
            }

            @Override
            public void onFailure(Call<List<HospitalReviewDTO>> call, Throwable t) {
                Log.e("후기 리스트 로드 실패", t.getMessage());
            }
        });
    }

    /*******************************************************************************************************************************************
     * 후기 등록
     * *****************************************************************************************************************************************/
    private void WriteReview(String contents){
        HospitalReviewDTO dto = new HospitalReviewDTO();
        dto.setNumber(num);
        dto.setId(loginId);
        dto.setContents(contents);
        dto.setNickname("qwer@naver.com");

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> WriteReview = NetRetrofit.getInstance().getService().WriteReview(objJson);
        WriteReview.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //후기리스트
                getReviews();

                Toast.makeText(HospitalDetailActivity.this, "작성 완료", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("후기등록실패", t.getMessage());
            }
        });
    }

    /*******************************************************************************************************************************************
     * 후기 갯수(여기서는 더하기)
     * *****************************************************************************************************************************************/
    private void ReviewCount(){
        HospitalDTO dto = new HospitalDTO();
        dto.setNumber(num); //빈값통신어캐하누

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> ReviewCount = NetRetrofit.getInstance().getService().ReviewCount(objJson);
        ReviewCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("갯수 카운트 실패", t.getMessage());
            }
        });
    }

    //리스트뷰 크기조절
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