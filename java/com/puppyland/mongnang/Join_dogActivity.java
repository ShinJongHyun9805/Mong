package com.puppyland.mongnang;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.DogDTO;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.widget.FButton;
import com.squareup.picasso.Picasso;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Join_dogActivity extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler; //by 신종현

    //이미지
    private ImageView dog_photo;
    private String tempuri, ImageName, id;
    private static final int MY_PERMISSION_STORAGE = 1111;
    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "http://192.168.219.100:8092/upload.jsp";  //<<서버주소

    private FButton skip, joincomplete_btn;
    private RadioGroup radioGroup;
    private RadioButton joinradiobutton1, joinradiobutton2, joinradiobutton3;
    private EditText joindog_id, joindog_age, joindog_gender;
    private String kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_dog);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        checkPermission();

        // 뒤로가기 핸들러
        backPressCloseHandler = new BackPressCloseHandler(this);

        Intent intent = getIntent();
        id = intent.getExtras().getString("ID");

        //종류 체크
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);
        joinradiobutton1 = findViewById(R.id.joinradiobutton1);
        joinradiobutton2 = findViewById(R.id.joinradiobutton2);
        joinradiobutton3 = findViewById(R.id.joinradiobutton3);


        joindog_id = findViewById(R.id.joindog_id);
        joindog_age = findViewById(R.id.joindog_age);
        joindog_gender = findViewById(R.id.joindog_gender);

        //이미지 등록
        dog_photo = findViewById(R.id.dog_photo);
        dog_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropViewActivity.imageFile = null;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        //건너뛰기
        skip = findViewById(R.id.skip);
        skip.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity3.class);//Join 대신 main2activtiy 로 가야함
                Toast.makeText(getApplicationContext(), "나중에 등록 해주세요", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        //완료
        joincomplete_btn = findViewById(R.id.joincomplete_btn);
        joincomplete_btn.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        joincomplete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dogname = joindog_id.getText().toString();
                String dogage = joindog_age.getText().toString();
                String doggender = joindog_gender.getText().toString();

                dogname = plzNoHacking(dogname);
                dogage = plzNoHacking(dogage);
                doggender = plzNoHacking(doggender);

                if (ImageName == null || (dogname == null || dogname.getBytes().length == 0) || (dogage == null || dogage.getBytes().length == 0) || (doggender == null || doggender.getBytes().length == 0)) {
                    Toast.makeText(getApplicationContext(), "사진 등록 및 값을 기재해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    //사진 등록
                    DoFileUpload(serverURL, img_path);

                    //강아지 정보 등록
                    InsertDogInfo(id, dogname, dogage, doggender);
                }
            }
        });

    } //End of onCreate

    //종류
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if (i == R.id.joinradiobutton1) {
                kind = "대형";
            } else if (i == R.id.joinradiobutton2) {
                kind = "중형";
            } else if (i == R.id.joinradiobutton3) {
                kind = "소형";
            }
        }
    };

    //앱 권한 요청
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            }
        }
    }// end of checkPermission

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SELECT_IMAGE
                && resultCode == Activity.RESULT_OK) {
            Uri galleryPictureUri = data.getData();
            String uri = galleryPictureUri.toString();

            Intent intent = new Intent(getApplicationContext(), CropViewActivity.class);
            intent.putExtra("img", uri);
            startActivityForResult(intent, 1123);

            //updateButtons();
        }
        if (requestCode == 1123 && resultCode == 1124) {
            tempuri = data.getStringExtra("contenturi");
            Log.v("xoxoxoxo", tempuri);
            Picasso.get().load(tempuri).error(R.color.colorAccent).fit().centerCrop().into(dog_photo);
            Bitmap bitmap = BitmapFactory.decodeFile(tempuri);
            dog_photo.setImageBitmap(bitmap);
            //파일 업로드시에 사용
            img_path = getFilePathFromURI(getApplicationContext(), Uri.parse(tempuri));
        }

    }

    public String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        ImageName = fileName;
        Log.v("wgxogxwqqq", fileName);
        return fileName;
    }

    public String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        File rootDataDir = context.getFilesDir();
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(rootDataDir + File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 여기서부터가 jsp 이미지를 보내는 코드
    public void DoFileUpload(String apiUrl, String absolutePath) {
        HttpFileUpload(apiUrl, "", absolutePath);
    }

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    //서버에 선택한 이미지를 전송하기 위한 함수 이미지의 URI정보를 매개로 하여 byte 단위로 서버에 전달
    public void HttpFileUpload(String urlString, String params, String fileName) {
        try {
            FileInputStream mFileInputStream = new FileInputStream(fileName);
            //하드 디스크상에 존재하는 파일로부터 바이트 단위의 입력을 받는 클래스이다.
            URL connectUrl = new URL(urlString);
            Log.d("Stream", "mFileInputStream  is " + mFileInputStream);

            // HttpURLConnection 통신
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            Log.d("uri", String.valueOf(conn));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            Log.d("Read", "image byte is " + bytesRead);

            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("Written", "File is written");
            mFileInputStream.close();
            dos.flush();
            // finish upload...

            // get response
            InputStream is = conn.getInputStream();

            StringBuffer b = new StringBuffer();
            for (int ch = 0; (ch = is.read()) != -1; ) {
                b.append((char) ch);
            }
            is.close();
            Log.e("zxcv", b.toString());


        } catch (Exception e) {
            Log.d("Message", "exception " + e.getMessage());
        }
    }

    public void InsertDogInfo(String id, String dogname, String dogage, String doggender) {
        DogDTO dto = new DogDTO();
        dto.setUserId(id);
        dto.setDogName(dogname);
        dto.setDogAge(dogage);
        dto.setDogGender(doggender);
        dto.setDogKind(kind);
        dto.setDogImage(ImageName);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        final Call<ResponseBody> updateDogInfo = NetRetrofit.getInstance().getService().dogUpdate(objJson);
        updateDogInfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "등록 완료:)", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity3.class);//Join 대신 main2activtiy 로 가야함
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });

    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
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