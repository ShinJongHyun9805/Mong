package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.puppyland.mongnang.DTO.PickDTO;
import com.puppyland.mongnang.Pick.Pickadapter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickActivity extends AppCompatActivity {

    ImageView backbutton;
    LinearLayout dataChipLayout;
    private ChipGroup defaultChipGroup;
    String cate;

    RecyclerView PickrecyclerView;
    public Pickadapter pickadapter;
    public List<PickDTO> pickDTOArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

       /* Intent intent = getIntent();
        String id = intent.getExtras().getString("id");*/

        dataChipLayout = findViewById(R.id.dataChipLayout);

        //카테고리 칩
        defaultChipGroup = findViewById(R.id.defaultChipGroup);
        defaultChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == R.id.defaultChip1) {
                    cate = "A";
                    getPickList();
                } else if (checkedId == R.id.defaultChip2) {
                    cate = "B";
                    getPickList();
                } else if(checkedId == R.id.defaultChip3){
                    cate = "C";
                    getPickList();
                }
            }
        });

        PickrecyclerView = findViewById(R.id.PickrecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        PickrecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(PickrecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        PickrecyclerView.addItemDecoration(dividerItemDecoration);

        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getPickList() {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("cate", cate);
        String objJson = gson.toJson(object);

        pickDTOArrayList = new ArrayList<PickDTO>();

        Call<List<PickDTO>> getPickList = NetRetrofit.getInstance().getService().getPickList(objJson);
        getPickList.enqueue(new Callback<List<PickDTO>>() {
            @Override
            public void onResponse(Call<List<PickDTO>> call, Response<List<PickDTO>> response) {
                pickDTOArrayList = response.body();
                if (pickDTOArrayList != null) {
                    pickadapter = new Pickadapter(pickDTOArrayList);
                    PickrecyclerView.setAdapter(pickadapter);
                    pickadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<PickDTO>> call, Throwable t) {
                Log.d("@@@홍보 글 리스트 가져오기 실패", t.getMessage());
            }
        });
    }
}