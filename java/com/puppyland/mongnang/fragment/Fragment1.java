package com.puppyland.mongnang.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.puppyland.mongnang.DTO.DogDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.PickDTO;
import com.puppyland.mongnang.ListViewAdapter;
import com.puppyland.mongnang.MainActivity2;
import com.puppyland.mongnang.Pick.Pickadapter;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.contract.SearchContract;
import com.puppyland.mongnang.presenter.SearchMemberListPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment1 extends Fragment {

    LinearLayout dataChipLayout;
    private ChipGroup defaultChipGroup;
    String cate;

    RecyclerView PickrecyclerView;
    public Pickadapter pickadapter;
    public List<PickDTO> pickDTOArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = (View) inflater.inflate(R.layout.fragment1, container, false);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        dataChipLayout = rootview.findViewById(R.id.dataChipLayout);

        //카테고리 칩
        defaultChipGroup = rootview.findViewById(R.id.defaultChipGroup);
        defaultChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == R.id.defaultChip1) {
                    cate = "A";
                    getPickList();
                } else if (checkedId == R.id.defaultChip2) {
                    cate = "B";
                    getPickList();
                } else if (checkedId == R.id.defaultChip3) {
                    cate = "C";
                    getPickList();
                } else if (checkedId == R.id.defaultChip4) {
                    cate = "D";
                    getPickList();
                } else if (checkedId == R.id.defaultChip5) {
                    cate = "E";
                    getPickList();
                } else if (checkedId == R.id.defaultChip6) {
                    cate = "F";
                    getPickList();
                } else if (checkedId == R.id.defaultChip7) {
                    cate = "G";
                    getPickList();
                } else if (checkedId == R.id.defaultChip8) {
                    cate = "H";
                    getPickList();
                } else if (checkedId == R.id.defaultChip9) {
                    cate = "I";
                    getPickList();
                } else if (checkedId == R.id.defaultChip10) {
                    cate = "J";
                    getPickList();
                }
            }
        });

        PickrecyclerView = rootview.findViewById(R.id.PickrecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        PickrecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(PickrecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        PickrecyclerView.addItemDecoration(dividerItemDecoration);

        return rootview;
    }

    public void getPickList() {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("cate", cate);
        Log.d("###", cate);
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
