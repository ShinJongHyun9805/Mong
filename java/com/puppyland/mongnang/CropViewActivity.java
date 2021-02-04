package com.puppyland.mongnang;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import com.lyft.android.scissors.CropView;
import com.puppyland.ImageRotate.ExifUtils;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static android.graphics.Bitmap.CompressFormat.JPEG;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;


public class CropViewActivity extends AppCompatActivity {

    //사진 시저
    CropView crop_view;
    Bitmap bitmap;
    //완료
    Button btn_cropview;
    Uri contentUri;
    CompositeSubscription subscriptions = new CompositeSubscription();
    public static File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_view);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Intent intent = getIntent(); /*데이터 수신*/
        String img = intent.getExtras().getString("img");

        Uri galleryPictureUri = Uri.parse(img);

        //이미지 시저
        crop_view = findViewById(R.id.crop_view);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), galleryPictureUri);
            //비율에 따라 자동 회전되는거 방지
            bitmap = ExifUtils.rotateBitmap(getImagePathToUri(galleryPictureUri), bitmap);
            crop_view.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //완료버튼
        btn_cropview = findViewById(R.id.btn_cropview);
        btn_cropview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    imageFile = File.createTempFile("JPEG_", ".jpg", getCacheDir());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Observable<Void> onSave = Observable.from(crop_view.extensions()
                        .crop()
                        .quality(100)
                        .format(JPEG)
                        .into(imageFile))
                        .subscribeOn(io())
                        .observeOn(mainThread());

                subscriptions.add(onSave
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void nothing) {
                                contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.puppyland.mongnang.fileprovider", imageFile);

                                Intent intent = new Intent();
                                String temp = contentUri.toString();
                                intent.putExtra("contenturi", temp);
                                setResult(1124, intent);
                                finish();
                            }
                        }));
            }
        });
    }

    public String getImagePathToUri(Uri data) {
        //사용자가 선택한 이미지의 정보를 받아옴
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        //이미지의 경로 값
        String imgPath = cursor.getString(column_index);
        Log.d("imgPath", imgPath);

        //이미지의 이름 값
        return imgPath;
    }//end of getImagePathToUri()


}