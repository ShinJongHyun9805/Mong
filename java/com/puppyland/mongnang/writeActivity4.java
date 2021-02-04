package com.puppyland.mongnang;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.puppyland.ImageRotate.ExifUtils;
import com.puppyland.mongnang.DTO.BoardDTO;
import com.puppyland.mongnang.contract.BoardContract;
import com.puppyland.mongnang.presenter.BoardPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class writeActivity4 extends AppCompatActivity implements BoardContract.view {

    BoardContract.presenter bpresenter;

    public static Activity wrtieActivity4;
    private static final int MY_PERMISSION_STORAGE = 1111;
    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "http://192.168.219.100:8092/upload.jsp";  //<<서버주소
    private Bitmap image_bitmap_copy = null;
    private Bitmap image_bitmap = null;
    private String imageName = null;
    private String tempuri;

    private TextView Tx_save;
    private LinearLayout LL_addimg, LL_img;
    private ImageView photo , button7;
    String ImageName;
    String loginId;
    //글쓰기페이지 역할
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write4);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        bpresenter = new BoardPresenter(this);
        wrtieActivity4 = writeActivity4.this;

        //게시판 리스트 뷰에서 로그인한 유저 ID값
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        loginId = sharedPreferences.getString("id", null);

        //닉네임
        SharedPreferences sharedPreferences2 = getSharedPreferences("nickname", MODE_PRIVATE);
        final String nickname = sharedPreferences2.getString("nickname", loginId);

        Tx_save = findViewById(R.id.Tx_save);
        Tx_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사진 업로드
                DoFileUpload(serverURL, img_path);

                String title = ((EditText) findViewById(R.id.editText6)).getText().toString();
                title = plzNoHacking(title);

                String content = ((EditText) findViewById(R.id.editText8)).getText().toString();
                content = plzNoHacking(content);
                if (ImageName == null) {
                    if ((title == null || title.getBytes().length == 0 || title.getBytes().length < 5) ) {
                        Toast.makeText(getApplicationContext(), "제목을 5글자 이상으로 적어주세요.", Toast.LENGTH_SHORT).show();
                    }else if((content == null || content.getBytes().length == 0 || content.getBytes().length <= 10)){
                        Toast.makeText(getApplicationContext(), "내용을 10글자 이상으로 적어주세요.", Toast.LENGTH_SHORT).show();
                    } else if (content.getBytes().length > 500) {
                        Toast.makeText(getApplicationContext(), "500자 이하로 작성해 주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        InsertNoImageBoard(title, loginId, content, "1", nickname);
                        Toast.makeText(getApplicationContext(), "글 작성 성공:)", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else {
                    if ((title == null || title.getBytes().length == 0 || title.getBytes().length < 5) ) {
                        Toast.makeText(getApplicationContext(), "제목을 5글자 이상으로 적어주세요.", Toast.LENGTH_SHORT).show();
                    }else if((content == null || content.getBytes().length == 0 || content.getBytes().length <= 10)){
                        Toast.makeText(getApplicationContext(), "내용을 10글자 이상으로 적어주세요.", Toast.LENGTH_SHORT).show();
                    } else if(content.getBytes().length > 500) {
                        Toast.makeText(getApplicationContext(), "500자 이하로 작성해 주세요:(", Toast.LENGTH_SHORT).show();
                    }else{
                        InsertImageBoard(title, loginId, content, ImageName, nickname);
                        Toast.makeText(getApplicationContext(), "글 작성 성공:)", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

        LL_addimg = findViewById(R.id.LL_addimg);
        LL_addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  LL_img = findViewById(R.id.LL_img);
                photo = findViewById(R.id.photo);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

            }
        });
        //뒤로가기 버튼
        button7 = findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
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
        //Toast.makeText(getBaseContext(), "resultCode : " + data, Toast.LENGTH_SHORT).show();
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                //    LL_img.setVisibility(View.VISIBLE);
                    int reWidth = (int) (getWindowManager().getDefaultDisplay().getWidth());
                    int reHeight = (int) (getWindowManager().getDefaultDisplay().getHeight());

                    img_path = getImagePathToUri(data.getData()); //이미지의 URI를 얻어 경로값으로 반환.
                    //Toast.makeText(getBaseContext(), "img_path : " + img_path, Toast.LENGTH_SHORT).show();
                    //이미지를 비트맵형식으로 반환
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    image_bitmap = ExifUtils.rotateBitmap(img_path,image_bitmap);
                    //image_bitmap 으로 받아온 이미지의 사이즈를 임의적으로 조절함. width: 400 , height: 300
                //    image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, reWidth, reHeight, true);
                    ImageView image = findViewById(R.id.photo);  //이미지를 띄울 위젯 ID값
                    image.setVisibility(View.VISIBLE);
                    image.setImageBitmap(image_bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//end of onActivityResult()

    public String getImagePathToUri(Uri data) {
        //사용자가 선택한 이미지의 정보를 받아옴
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        //이미지의 경로 값
        String imgPath = cursor.getString(column_index);

        //이미지의 이름 값
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        this.imageName = imgName;
        ImageName = imgName;

        return imgPath;
    }//end of getImagePathToUri()


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

    /*
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
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        Toast.makeText(writeActivity4.this, "이미지 이름 : " + imgName, Toast.LENGTH_SHORT).show();
        this.imageName = imgName;
        ImageName = imgName;
        Log.d("###", ImageName);

        return imgPath;
    }//end of getImagePathToUri()


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

            // write2 data
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
    }*/

    private void InsertNoImageBoard(String title, String loginId, String content, String image, String nickname) {
        BoardDTO dto = new BoardDTO();
        dto.setTitle(title);
        dto.setUserid(loginId);
        dto.setBcontent(content);
        dto.setImg("1");
        dto.setNickname(nickname);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> boardWrite = NetRetrofit.getInstance().getService().boardWrite(objJson);
        boardWrite.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("@@@su", "성공");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });

        finish();
    }

    private void InsertImageBoard(String title, String loginId, String content, String ImageName, String nickname) {
        BoardDTO dto = new BoardDTO();
        dto.setTitle(title);
        dto.setUserid(loginId);
        dto.setBcontent(content);
        dto.setImg(ImageName);
        dto.setNickname(nickname);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> boardWrite = NetRetrofit.getInstance().getService().boardWrite(objJson);
        boardWrite.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("@@@su", "성공");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });

        finish();
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