package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.puppyland.mongnang.DTO.DogDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.contract.SearchContract;
import com.puppyland.mongnang.presenter.SearchMemberListPresenter;
import com.puppyland.mongnang.widget.FButton;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchContract.View{

    private ChipGroup chipGroup, addressChipGroup, personGenderChipGroup, dogGenderChipGroup, dogSizeChipGroup;
    String result_age, result_address, result_gender, result_doggender, result_dogkind; // 드랍다운을 누른 값들이 담기는 변수.
    SearchContract.Presenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        presenter = new SearchMemberListPresenter(this); // 프레젠터와 view 의 연결
        chipGroup = findViewById(R.id.ageChipGroup);
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == R.id.chip_0) {
                    result_age = "all";
                } else if (checkedId == R.id.chip_1) {
                    result_age = "10";
                } else if (checkedId == R.id.chip_2) {
                    result_age = "20";
                } else if (checkedId == R.id.chip_3) {
                    result_age = "30";
                } else if (checkedId == R.id.chip_4) {
                    result_age = "40";
                } else if (checkedId == R.id.chip_5) {
                    result_age = "50";
                } else if (checkedId == R.id.chip_6) {
                    result_age = "60";
                }
            }
        });

        addressChipGroup = findViewById(R.id.addressChipGroup);
        addressChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == R.id.addresschip_0) {
                    result_address = "all";
                } else if (checkedId == R.id.addresschip_1) {
                    result_address = "시or도";
                } else if (checkedId == R.id.addresschip_2) {
                    result_address = "구or군";
                } else if (checkedId == R.id.addresschip_3) {
                    result_address = "동or면";
                }
            }
        });
        personGenderChipGroup = findViewById(R.id.personGenderChipGroup);
        personGenderChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == R.id.personGenderchip_0) {
                    result_gender = "all";
                } else if (checkedId == R.id.personGenderchip_1) {
                    result_gender = "M";
                } else if (checkedId == R.id.personGenderchip_2) {
                    result_gender = "F";
                }
            }
        });

        dogGenderChipGroup = findViewById(R.id.dogGenderChipGroup);
        dogGenderChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == R.id.dogGenderchip_0) {
                    result_doggender = "all";
                } else if (checkedId == R.id.dogGenderchip_1) {
                    result_doggender = "M";
                } else if (checkedId == R.id.dogGenderchip_2) {
                    result_doggender = "F";
                }
            }
        });

        dogSizeChipGroup = findViewById(R.id.dogSizeChipGroup);
        dogSizeChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == R.id.dogSizechip_0) {
                    result_dogkind = "all";
                } else if (checkedId == R.id.dogSizechip_1) {
                    result_dogkind = "대형";
                } else if (checkedId == R.id.dogSizechip_2) {
                    result_dogkind = "중형";
                } else if (checkedId == R.id.dogSizechip_3) {
                    result_dogkind = "소형";
                }
            }
        });

        FButton SearchButton;
        SearchButton = findViewById(R.id.resultbutton); //검색실행 버튼
        SearchButton.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                String loginId = sharedPreferences.getString("id", null);

                if(((result_age ==null || result_address ==null) ||  (result_gender==null || result_doggender==null )) || result_dogkind==null){
                    Toast.makeText(getApplicationContext(), "선택 하지않은 조건이 있습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("loginId", loginId);
                    intent.putExtra("result_age", result_age);
                    intent.putExtra("result_address", result_address);
                    intent.putExtra("result_gender", result_gender);
                    intent.putExtra("result_doggender", result_doggender);
                    intent.putExtra("result_dogkind", result_dogkind);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void showResult(ArrayList<MemberDTO> list) {

    }

    @Override
    public void showClickMember(MemberDTO member) {

    }

    @Override
    public void showClickMemberDog(List<DogDTO> dog) {

    }
}