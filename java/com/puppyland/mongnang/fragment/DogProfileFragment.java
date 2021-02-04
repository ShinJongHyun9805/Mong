package com.puppyland.mongnang.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.DogDTO;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.userClickImageActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogProfileFragment extends Fragment {

    String id , dogimagename;
    TextView dogname , dogage , dogsex , dogsize;
    CircleImageView dogProfileImage;
    private List<DogDTO> Adto;

    public DogProfileFragment(){

    }
    public DogProfileFragment(String selectedId) {
        this.id = selectedId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_dog_profile, container, false);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        dogProfileImage = view.findViewById(R.id.dogProfileImage);
        dogname = view.findViewById(R.id.dogname);
        dogage = view.findViewById(R.id.dogage);
        dogsex = view.findViewById(R.id.dogsex);
        dogsize = view.findViewById(R.id.dogsize);

        dogProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext() , userClickImageActivity.class);
                intent1.putExtra("id" ,id);
                intent1.putExtra("dogimage",dogimagename);
                startActivity(intent1);
            }
        });
        dogInfo(id);

        return view;
    }


    private void dogInfo(String id) {
        DogDTO dto = new DogDTO();
        dto.setUserId(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Adto = new ArrayList<DogDTO>();
        Call<List<DogDTO>> dogfileName = NetRetrofit.getInstance().getService().dogfileName(objJson);
        dogfileName.enqueue(new Callback<List<DogDTO>>() {
            @Override
            public void onResponse(Call<List<DogDTO>> call, Response<List<DogDTO>> response) {
                Adto = response.body();
                if(Adto == null){

                }
                for (DogDTO dog : Adto) {
                    Picasso.get().load("http://192.168.219.100:8092/upload/" + dog.getDogImage()).error(R.drawable.monglogo2).fit().centerCrop().into(dogProfileImage);
                    dogname.setText( dog.getDogName());
                    dogimagename =  dog.getDogImage();
                    if(dog.getDogGender() ==null){
                        dogsex.setText("");
                    }else if(dog.getDogGender().equals("f")){
                        dogsex.setText("암컷/");
                    }else{
                        dogsex.setText("수컷/");
                    }
                    dogage.setText(dog.getDogAge()+"세/");
                    dogsize.setText(dog.getDogKind());
                }
            }

            @Override
            public void onFailure(Call<List<DogDTO>> call, Throwable t) {
                Log.e("###DogInfofail", t.getMessage());
            }
        });
    }
}