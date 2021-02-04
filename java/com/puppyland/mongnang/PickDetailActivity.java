package com.puppyland.mongnang;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.puppyland.mongnang.DTO.PickDTO;
import com.puppyland.mongnang.Pick.Pickadapter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickDetailActivity extends AppCompatActivity {

    //카를로스 뷰
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.dog2, R.drawable.draw5, R.drawable.draw6, R.drawable.draw7, R.drawable.black_dot};

    //별점
    ImageView backbutton;
    TextView textView2, pick_link1, Tx_title, Tx_content, pick_link2, tx_star;
    RatingBar ratingBar;
    String str;
    Float starpoint;
    List<String> webImageURLs;
    Boolean starCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_detail);

        Intent intent = getIntent();
        PickDTO pickDTO = (PickDTO) intent.getSerializableExtra("pickDTO");

        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", null);


        checkpick(id, pickDTO.getPno());

        webImageURLs = new ArrayList<>();


        webImageURLs.add(pickDTO.getPic1());
        webImageURLs.add(pickDTO.getPic2());
        webImageURLs.add(pickDTO.getPic3());
        webImageURLs.add(pickDTO.getPic4());
        webImageURLs.add(pickDTO.getPic5());


        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //제목
        Tx_title = findViewById(R.id.Tx_title);
        Tx_title.setText(pickDTO.getTitle());
        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(webImageURLs.size());
        carouselView.setImageListener(imageListener);

        //내용
        Tx_content = findViewById(R.id.Tx_content);
        Tx_content.setText(pickDTO.getContent());


        //별점
        //별점 남기기
        tx_star = findViewById(R.id.tx_star);
        tx_star.setPaintFlags(tx_star.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tx_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (starCheck == false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PickDetailActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.star, null);
                    builder.setView(view);

                    final TextView title = view.findViewById(R.id.Tx_title);
                    final TextView Tx_use = view.findViewById(R.id.Tx_use);
                    final TextView Tx_cancel = view.findViewById(R.id.Tx_cancel);
                    final LinearLayout star = view.findViewById(R.id.star);
                    final RatingBar ratingBar = view.findViewById(R.id.ratingBar);
                    final TextView textView2 = view.findViewById(R.id.textView2);
                    final AlertDialog dialog = builder.create();

                    //별점 남기기
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            str = String.valueOf(rating);
                            str = Float.toString(rating);
                            starpoint = rating;
                            textView2.setText(str);
                        }
                    });

                    //별점 업데이트
                    Tx_use.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setStarPoint(starpoint, pickDTO.getPno());
                            insertStar(id, pickDTO.getPno());
                            dialog.dismiss();
                        }
                    });

                    //취소
                    Tx_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(PickDetailActivity.this, "이미 별점을 주셨습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //링크
        pick_link1 = findViewById(R.id.pick_link1);
        pick_link1.setText(pickDTO.getLink1());
        pick_link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(pickDTO.getLink1()));
                startActivity(i);
            }
        });

        //링크
        pick_link2 = findViewById(R.id.pick_link2);
        pick_link2.setText(pickDTO.getLink2());
        pick_link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(pickDTO.getLink2()));
                startActivity(i);
            }
        });
    }

    //카를로스 이미지
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.get().load("http://192.168.219.100:8092/upload/" + webImageURLs.get(position)).error(R.drawable.monglogo2).into(imageView);
        }
    };

    public void setStarPoint(Float f, int pno) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("starpoint", f);
        object.addProperty("pno", pno);
        String objJson = gson.toJson(object);

        Call<Map<String, Object>> setStarPoint = NetRetrofit.getInstance().getService().setStarPoint(objJson);
        setStarPoint.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();
                map.get("starpoint");
                map.get("staruserCount");
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });
    }

    //별점 업데이트시 DB에 등록
    public void insertStar(String id, int pno) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("pno", pno);
        String objJson = gson.toJson(object);

        Call<ResponseBody> insertStar = NetRetrofit.getInstance().getService().insertStar(objJson);
        insertStar.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getApplicationContext(), "별점 등록을 완료하였습니다. 감사합니다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###별점체크 가져오기 실패", t.getMessage());
            }
        });
    }

    //이 글의 평가를 했는지 안했는지
    public void checkpick(String id, int pno) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("pno", pno);
        String objJson = gson.toJson(object);

        Call<Integer> checkpick = NetRetrofit.getInstance().getService().checkpick(objJson);
        checkpick.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {
                    starCheck = true; // 별점체크한거
                } else {//안한거
                    starCheck = false;
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("###별점체크 가져오기 실패", t.getMessage());
            }
        });
    }

}