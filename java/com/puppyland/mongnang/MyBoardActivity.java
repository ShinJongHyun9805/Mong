package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.BoardDTO;
import com.puppyland.mongnang.board.ListViewAdapter2;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBoardActivity extends AppCompatActivity {

    private ListView listview;
    private ListViewAdapter2 adapter;
    //배너 광고
    private AdView mAdView;
    private List<BoardDTO> list;
    private EditText eSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_activtiy);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        listview = findViewById(R.id.list);

        //등급확인
        SharedPreferences sharedPreferences = getSharedPreferences("rating", MODE_PRIVATE);
        String rating = sharedPreferences.getString("rate", null);
        if (rating.equals("0")) {
            MobileAds.initialize(this, getString(R.string.admob_app_id)); //네이티브 광고
            AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                    .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().build();

                            TemplateView template = findViewById(R.id.my_template);
                            template.setStyles(styles);
                            template.setNativeAd(unifiedNativeAd);
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            // Handle the failure by logging, altering the UI, and so on.
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
            eSearch = findViewById(R.id.edit_search);
            eSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            eSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_SEARCH:
                            //검색
                            String searchValue = eSearch.getText().toString();

                            BoardDTO dto = new BoardDTO();
                            dto.setTitle(searchValue);

                            Gson gson = new Gson();
                            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                            Call<List<BoardDTO>> SearchBoard = NetRetrofit.getInstance().getService().Searchboard(objJson);
                            SearchBoard.enqueue(new Callback<List<BoardDTO>>() {
                                @Override
                                public void onResponse(Call<List<BoardDTO>> call, Response<List<BoardDTO>> response) {
                                    list = response.body();

                                    adapter = new ListViewAdapter2();
                                    listview.setAdapter(adapter);
                                    try {
                                        if (list != null) {
                                            for (BoardDTO boardDTO : list) { // 리스트로 담기는// 것들 하나씩 출력
                                                adapter.addItem(boardDTO.getBno(), boardDTO.getTitle(), boardDTO.getUserid(), boardDTO.getCreate_date(), boardDTO.getCnt(), boardDTO.getNickname(), boardDTO.getHit());
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onFailure(Call<List<BoardDTO>> call, Throwable t) {
                                    Log.d("fail", t.getMessage());
                                }
                            });

                            break;
                        default:
                            // 기본 엔터키 동작
                            return false;
                    }
                    return true;
                }
            });

        } else {
            //광고제거
            MobileAds.initialize(this, getString(R.string.admob_app_id));
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.setVisibility(View.GONE);

            list = new ArrayList<BoardDTO>();

            eSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            eSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_SEARCH:
                            //검색
                            String searchValue = eSearch.getText().toString();

                            BoardDTO dto = new BoardDTO();
                            dto.setTitle(searchValue);

                            Log.d("###", dto.getTitle());

                            Gson gson = new Gson();
                            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                            Call<List<BoardDTO>> SearchBoard = NetRetrofit.getInstance().getService().Searchboard(objJson);
                            SearchBoard.enqueue(new Callback<List<BoardDTO>>() {
                                @Override
                                public void onResponse(Call<List<BoardDTO>> call, Response<List<BoardDTO>> response) {
                                    list = response.body();

                                    adapter = new ListViewAdapter2();
                                    listview.setAdapter(adapter);
                                    try {
                                        if (list != null) {
                                            for (BoardDTO boardDTO : list) { // 리스트로 담기는// 것들 하나씩 출력
                                                adapter.addItem(boardDTO.getBno(), boardDTO.getTitle(), boardDTO.getUserid(), boardDTO.getCreate_date(), boardDTO.getCnt(), boardDTO.getNickname(), boardDTO.getHit());
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<BoardDTO>> call, Throwable t) {
                                    Log.d("fail", t.getMessage());
                                }
                            });

                            break;
                        default:
                            // 기본 엔터키 동작
                            return false;
                    }
                    return true;
                }
            });
        }
    }

    //글 작성 후 새로고침
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");

        BoardDTO dto = new BoardDTO();
        dto.setUserid(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        list = new ArrayList<BoardDTO>();

        Call<List<BoardDTO>> myboardlistInfo = NetRetrofit.getInstance().getService().myboardlistInfo(objJson);
        myboardlistInfo.enqueue(new Callback<List<BoardDTO>>() {
            @Override
            public void onResponse(Call<List<BoardDTO>> call, Response<List<BoardDTO>> response) {
                list = response.body();

                adapter = new ListViewAdapter2();
                listview.setAdapter(adapter);
                try {
                    if (list != null) {
                        for (BoardDTO boardDTO : list) { // 리스트로 담기는// 것들 하나씩 출력
                            adapter.addItem(boardDTO.getBno(), boardDTO.getTitle(), boardDTO.getUserid(), boardDTO.getCreate_date(), boardDTO.getCnt(), boardDTO.getNickname(), boardDTO.getHit());
                            Log.d("###boardActivity", String.valueOf(list.get(0).getHit()));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<BoardDTO>> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });

        //리스트 뷰 아이템 클릭시 해당 아이템의 문자열을 가져옴
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //로그인한 유저 ID값
                SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                String loginId = sharedPreferences.getString("id", null);

                //게시판 글 상세보기로 데이터 전달
                Intent intent = new Intent(getApplicationContext(), BoardDetail.class);
                BoardDTO dto = list.get(position);

                intent.putExtra("bno", dto.getBno());
                intent.putExtra("title", dto.getTitle());
                intent.putExtra("user", dto.getUserid());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", loginId);
                editor.commit();

                startActivity(intent);
            }
        });
    }

    //게시글 가져 오기, list에 레트로핏 통신 값 담기
    public List<BoardDTO> list(String id) {
        BoardDTO dto = new BoardDTO();
        dto.setUserid(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        //이걸 내거만 가져오게 만들어야할듯
        Call<List<BoardDTO>> myboardlistInfo = NetRetrofit.getInstance().getService().myboardlistInfo(objJson);
        myboardlistInfo.enqueue(new Callback<List<BoardDTO>>() {
            @Override
            public void onResponse(Call<List<BoardDTO>> call, Response<List<BoardDTO>> response) {
                list = response.body();
            }

            @Override
            public void onFailure(Call<List<BoardDTO>> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });
        return list;
    }
}