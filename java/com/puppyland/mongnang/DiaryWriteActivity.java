package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.puppyland.mongnang.contract.DiaryContract;
import com.puppyland.mongnang.presenter.DiaryPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryWriteActivity extends AppCompatActivity implements DiaryContract.view {

    DiaryContract.presenter dpresenter;

    private static final int MY_PERMISSION_STORAGE = 1111;
    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "http://192.168.219.100:8092/upload.jsp";  //<<서버주소
    private Bitmap image_bitmap_copy = null;
    private Bitmap image_bitmap = null;
    private String imageName = null;

    String tempFont;

    ImageView photo ,button2;
    String ImageName;
    TextView textview10, textView11, textView12 ,button;
    EditText editText8;
    LinearLayout btn_img;
    String loginId;
    String tempuri;
    private ChipGroup fontChipGroup; // 폰트 칩그룹
    Switch shareSwitch;
    Boolean shareflag= false;
    int shareConfirmation=1; // 공유변수//1은 공유 x 기본
    //글쓰기페이지 역할
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        dpresenter = new DiaryPresenter(this);


        //게시판 리스트 뷰에서 로그인한 유저 ID값
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        loginId = sharedPreferences.getString("id", null);

        SharedPreferences sharedPreferences2 = getSharedPreferences("nickname", MODE_PRIVATE);
        final String nickname = sharedPreferences2.getString("nickname", loginId);

        shareSwitch = (Switch) findViewById(R.id.shareSwitch);
        shareSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareflag==false){ //공유 안되어있을때 누르면 공유하는 동작
                    shareConfirmation = 2;
                }
                else{ //반대
                    shareConfirmation =1;
                }
            }
        });


        editText8 = findViewById(R.id.editText8);
        button = findViewById(R.id.button6);
        button.setOnClickListener(new View.OnClickListener() { // 글쓰기 완료버튼
            @Override
            public void onClick(View v) {
                //사진 업로드
                DoFileUpload(serverURL, img_path);

                String title = ((EditText) findViewById(R.id.editText6)).getText().toString();
                title = plzNoHacking(title);
                String content = editText8.getText().toString();
                content = plzNoHacking(content);

                if(ImageName == null){
                    if(tempFont ==null){
                        tempFont ="ibmplexsanskrsemibold.ttf";
                    }
                    if ((title == null || title.getBytes().length == 0 || title.getBytes().length < 5) ) {
                        Toast.makeText(getApplicationContext(), "제목을 5글자 이상으로 적어주세요.", Toast.LENGTH_SHORT).show();
                    }else if((content == null || content.getBytes().length == 0 || content.getBytes().length <= 10)){
                        Toast.makeText(getApplicationContext(), "내용을 10글자 이상으로 적어주세요.", Toast.LENGTH_SHORT).show();
                    }
                    else if(content.getBytes().length > 500) {
                        Toast.makeText(getApplicationContext(), "500자 이하로 작성해 주세요:(", Toast.LENGTH_SHORT).show();
                    }else{
                        dpresenter.InsertBoard(title,loginId,content, "" ,shareConfirmation, nickname , tempFont);
                        Toast.makeText(getApplicationContext(), "글 작성 완료", Toast.LENGTH_LONG).show();
                        finish(); //작성 완료하고 나면 창 닫기는 곳
                    }

                }else{
                    if(tempFont ==null){
                        tempFont ="ibmplexsanskrsemibold.ttf";
                    }
                    if ((title == null || title.getBytes().length == 0|| title.getBytes().length < 5) || content == null || content.getBytes().length == 0 || content.getBytes().length <= 10) {
                        Toast.makeText(getApplicationContext(), "5글자 이상으로 적어주세요.", Toast.LENGTH_SHORT).show();
                    } else if(content.getBytes().length > 500) {
                        Toast.makeText(getApplicationContext(), "500자 이하로 작성해 주세요:(", Toast.LENGTH_SHORT).show();
                    }else{
                        dpresenter.InsertBoard(title, loginId, content, ImageName,shareConfirmation, nickname , tempFont);
                        Toast.makeText(getApplicationContext(), "글 작성 완료", Toast.LENGTH_LONG).show();
                        finish(); //작성 완료하고 나면 창 닫기는 곳
                    }

                }
                if(shareConfirmation ==2){
                    Intent intent = new Intent(getApplicationContext(),shareCompleteActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        });

        button2 = findViewById(R.id.button7);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //앱 권한 관리
        checkPermission();

        //진저브레드에서 부터 추가된 일종의 개발툴로 개발자가 실수하는 것들을 감지하고 해결 할 수 있도록 돕는 모드, (실제로 수정하지는 않음 단지 알려줌)
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        //이미지를 띄울 위젯 연결
        photo = findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropViewActivity.imageFile = null;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });
        btn_img = findViewById(R.id.LL_addimg);
        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropViewActivity.imageFile = null;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        fontChipGroup = findViewById(R.id.fontChipGroup);
        fontChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                    if(checkedId == 1){

                    }
            }
        });


        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("userid", loginId);
        String objJson = gson.toJson(object);
        Call<List<String>> getfontList = NetRetrofit.getInstance().getService().getfontList(objJson);
        getfontList.enqueue(new Callback<List<String>>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> list = response.body();
                    for (int i = 0; i < list.size(); i++) {
                        Chip chip = new Chip(DiaryWriteActivity.this);
                        if (list.get(i).equals("dossaemmul.ttf")) {
                            chip.setText("도트체");
                            chip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Typeface typeFace = Typeface.createFromAsset(getAssets(), "dossaemmul.ttf");
                                    editText8.setTypeface(typeFace);
                                    tempFont = "dossaemmul.ttf";
                                }
                            });
                            chip.setCheckableResource(R.color.fbutton_color_sun_flower);
                            chip.setChipBackgroundColorResource(R.color.colorFontchipBackground);
                            fontChipGroup.addView(chip);
                        } else if (list.get(i).equals("gmarketsansttflight.ttf")) {
                            chip.setText("G마켓 라이트체");
                            chip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Typeface typeFace = Typeface.createFromAsset(getAssets(), "gmarketsansttflight.ttf");
                                    editText8.setTypeface(typeFace);
                                    tempFont = "gmarketsansttflight.ttf";
                                }
                            });
                            chip.setCheckableResource(R.color.fbutton_color_sun_flower);
                            chip.setChipBackgroundColorResource(R.color.colorFontchipBackground);
                            fontChipGroup.addView(chip);

                        } else if (list.get(i).equals("godomaum.ttf")) {
                            chip.setText("고도 마음체");
                            chip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Typeface typeFace = Typeface.createFromAsset(getAssets(), "godomaum.ttf");
                                    editText8.setTypeface(typeFace);
                                    tempFont = "godomaum.ttf";
                                }
                            });
                            chip.setCheckableResource(R.color.fbutton_color_sun_flower);
                            chip.setChipBackgroundColorResource(R.color.colorFontchipBackground);
                            fontChipGroup.addView(chip);
                        } else if (list.get(i).equals("yanulljaregular.ttf")) {
                            chip.setText("야놀자체");
                            chip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Typeface typeFace = Typeface.createFromAsset(getAssets(), "yanulljaregular.ttf");
                                    editText8.setTypeface(typeFace);
                                    tempFont = "yanulljaregular.ttf";
                                }
                            });
                            chip.setCheckableResource(R.color.fbutton_color_sun_flower);
                            chip.setChipBackgroundColorResource(R.color.colorFontchipBackground);
                            fontChipGroup.addView(chip);
                        }
                    }

                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SELECT_IMAGE
                && resultCode == Activity.RESULT_OK) {
            Uri galleryPictureUri = data.getData();
            String uri = galleryPictureUri.toString();


            Intent intent = new Intent(getApplicationContext(),CropViewActivity.class);
            intent.putExtra("img",uri);
            startActivityForResult(intent , 1123);

        }
        if(requestCode == 1123 && resultCode ==1124){
            tempuri = data.getStringExtra("contenturi");
            Picasso.get().load(tempuri).error(R.drawable.monglogo2).fit().centerCrop().into(photo);
            Bitmap bitmap = BitmapFactory.decodeFile(tempuri);
            photo.setVisibility(View.VISIBLE);
            photo.setImageBitmap(bitmap);
            img_path =getFilePathFromURI(getApplicationContext(),Uri.parse(tempuri));
        }
    }//end of onActivityResult()

    public  String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        ImageName = fileName;
        return fileName;
    }
    public  String getFilePathFromURI(Context context, Uri contentUri) {
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

            // HttpURLConnection 통신
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
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