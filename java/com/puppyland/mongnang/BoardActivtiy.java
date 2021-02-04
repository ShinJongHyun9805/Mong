package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.puppyland.mongnang.DTO.BoardDTO;
import com.puppyland.mongnang.board.ListViewAdapter2;
import com.puppyland.mongnang.contract.BoardContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardActivtiy extends AppCompatActivity implements BoardContract.view, Serializable {

    //당겨서 새로고침
    private SwipeRefreshLayout swipe_list;
    private ListView listview;
    private ListViewAdapter2 adapter;
    //배너 광고
    private AdView mAdView;
    private List<BoardDTO> list;
    private EditText eSearch;
    private FloatingActionButton fab ,fab2;
    TemplateView template;
    //상단 메뉴
    private LinearLayout LL_menu;
    AdLoader adLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_activtiy);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        listview = findViewById(R.id.list);
        //게시글
       // boardlist();


            //광고
            MobileAds.initialize(this, getString(R.string.admob_app_id)); //네이티브 광고
            adLoader = new AdLoader.Builder(this, "ca-app-pub-1267905668913700/7722826201")
                    .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().build();

                            template = findViewById(R.id.my_template);
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
        //    adLoader.loadAd(new AdRequest.Builder().build());

            eSearch = findViewById(R.id.edit_search);

        eSearch.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$");
                if (source.equals("") || ps.matcher(source).matches()) {
                    return source;
                }
                Toast.makeText(getApplicationContext(), "한글, 영문, 숫자만 입력 가능합니다.", Toast.LENGTH_SHORT).show();
                return "";
            }
        }});

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
            //당겨서 새로고침
            swipe_list = findViewById(R.id.swipe_list);
            swipe_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                 boardlist();
                }
            });
            //작성
            fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                    String id = sharedPreferences.getString("id", "");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("id", id);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), writeActivity4.class);
                    startActivity(intent);
                }
            });

            //게시판 메뉴
            LL_menu = findViewById(R.id.LL_menu);
            LL_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu p = new PopupMenu(getApplicationContext(), v);
                    getMenuInflater().inflate(R.menu.board_menu, p.getMenu());
                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if (item.getTitle().equals("내 글 보기")) {
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
                            } else if (item.getTitle().equals("조회수 높은 순")) {
                                //빈 값 통신은 어떻게 해야하는 거죠?

                                BoardDTO dto = new BoardDTO();
                                dto.setUserid("1");

                                Gson gson = new Gson();
                                String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                                Call<List<BoardDTO>> Highview = NetRetrofit.getInstance().getService().Highview(objJson);
                                Highview.enqueue(new Callback<List<BoardDTO>>() {
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
                                        Log.e("게시판 메뉴 쪽 에러", t.getMessage());
                                    }
                                });

                            } else if (item.getTitle().equals("최신 글")) {
                                SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                                String id = sharedPreferences.getString("id", "");

                                BoardDTO dto = new BoardDTO();
                                dto.setUserid(id);

                                Gson gson = new Gson();
                                String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                                list = new ArrayList<BoardDTO>();

                                Call<List<BoardDTO>> boardInfo = NetRetrofit.getInstance().getService().boardInfo(objJson);
                                boardInfo.enqueue(new Callback<List<BoardDTO>>() {
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
                                        Log.e("최신글 가져오기 실패", t.getMessage());
                                    }
                                });
                            }
                            return false;
                        }
                    });
                    p.show();
                }
            });
    }

    //글 작성 후 새로고침
    @Override
    public void onResume() {
        super.onResume();
        adLoader.loadAd(new AdRequest.Builder().build()); // 나올때마다 새로운 광고 노출

        boardlist();
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

    private void boardlist() {
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");

        BoardDTO dto = new BoardDTO();
        dto.setUserid(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<List<BoardDTO>> boardInfo = NetRetrofit.getInstance().getService().boardInfo(objJson);
        boardInfo.enqueue(new Callback<List<BoardDTO>>() {
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
                //새로고침 완료 !!필수!!
                swipe_list.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<BoardDTO>> call, Throwable t) {
                Log.d("게시글 가져오기 실패", t.getMessage());
            }
        });
    }


    //게시글 가져 오기, list에 레트로핏 통신 값 담기
    public List<BoardDTO> list(String id) {
        BoardDTO dto = new BoardDTO();
        dto.setUserid(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<List<BoardDTO>> boardInfo = NetRetrofit.getInstance().getService().boardInfo(objJson);
        boardInfo.enqueue(new Callback<List<BoardDTO>>() {
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