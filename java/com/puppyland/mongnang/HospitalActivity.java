package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.HospitalDTO;
import com.puppyland.mongnang.hospital.ListViewAdapterHospital;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalActivity extends AppCompatActivity {

    //입력 Edittext
    private EditText hospital_address1, hospital_address2;
    //검색
    private TextView Tx_search;
    //리스트 담을 변수
    private List<HospitalDTO> list;
    //리스트뷰
    private ListView hospital_listview;
    //리스트뷰 어댑터
    private ListViewAdapterHospital adapter;
    //시, 구
    private String First_address, Second_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        hospital_listview = findViewById(R.id.hospital_listview);
        hospital_address1 = findViewById(R.id.hospital_address1);
        hospital_address2 = findViewById(R.id.hospital_address2);

        //검색
        Tx_search = findViewById(R.id.Tx_search);
        Tx_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                First_address = hospital_address1.getText().toString();
                Second_address = hospital_address2.getText().toString();

                First_address= plzNoHacking(First_address);
                Second_address = plzNoHacking(Second_address);

                if ((First_address == null || First_address.getBytes().length == 0) && (Second_address == null || Second_address.getBytes().length == 0)) {
                    Toast.makeText(getApplicationContext(), "내용을 작성해 주세요.", Toast.LENGTH_LONG).show();
                } else {
                    HospitalDTO dto = new HospitalDTO();
                    dto.setAddress1(First_address);
                    dto.setAddress2(Second_address);

                    Gson gson = new Gson();
                    String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                    Call<List<HospitalDTO>> getHospitalInfo = NetRetrofit.getInstance().getService().getHospitalInfo(objJson);
                    getHospitalInfo.enqueue(new Callback<List<HospitalDTO>>() {
                        @Override
                        public void onResponse(Call<List<HospitalDTO>> call, Response<List<HospitalDTO>> response) {
                            list = response.body();

                            adapter = new ListViewAdapterHospital();
                            hospital_listview.setAdapter(adapter);
                            try {
                                if (list != null) {
                                    for (HospitalDTO hospitalDTO : list) {
                                        adapter.addItem(hospitalDTO.getAddress1(), hospitalDTO.getAddress2(), hospitalDTO.getName(), hospitalDTO.getRealaddress(), hospitalDTO.getNumber(), hospitalDTO.getReviews());
                                        Log.d("###", "###");
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<HospitalDTO>> call, Throwable t) {
                            Log.e("정보가져오기 실패", t.getMessage());
                        }
                    });
                }
            }
        });

        hospital_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //게시판 글 상세보기로 데이터 전달
                Intent intent = new Intent(getApplicationContext(), HospitalDetailActivity.class);
                HospitalDTO dto = list.get(position);

                intent.putExtra("name", dto.getName());
                intent.putExtra("num", dto.getNumber());
                intent.putExtra("address", dto.getRealaddress());

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * 그 mapper에서 if문 때문에 null포인트에러가 터지는데 돌아가는데 터질만큼의 에러는 아님
         * */

        HospitalDTO dto = new HospitalDTO();
        dto.setAddress1(First_address);
        dto.setAddress2(Second_address);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        list = new ArrayList<HospitalDTO>();

        Call<List<HospitalDTO>> getHospitalInfo = NetRetrofit.getInstance().getService().getHospitalInfo(objJson);
        getHospitalInfo.enqueue(new Callback<List<HospitalDTO>>() {
            @Override
            public void onResponse(Call<List<HospitalDTO>> call, Response<List<HospitalDTO>> response) {
                list = response.body();

                adapter = new ListViewAdapterHospital();
                hospital_listview.setAdapter(adapter);
                try {
                    if (list != null) {
                        for (HospitalDTO hospitalDTO : list) {
                            adapter.addItem(hospitalDTO.getAddress1(), hospitalDTO.getAddress2(), hospitalDTO.getName(), hospitalDTO.getRealaddress(), hospitalDTO.getNumber(), hospitalDTO.getReviews());
                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<List<HospitalDTO>> call, Throwable t) {
                Log.e("정보가져오기 실패", t.getMessage());
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