package com.puppyland.mongnang.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.util.IOUtils;
import com.google.gson.Gson;
import com.puppyland.mongnang.CropViewActivity;
import com.puppyland.mongnang.DTO.DogDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.InformationActivity;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.contract.ImageContract;
import com.puppyland.mongnang.contract.MemberContract;
import com.puppyland.mongnang.presenter.ImagePresenter;
import com.puppyland.mongnang.presenter.MemberPresenter;
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
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogInfoFragment extends Fragment implements ImageContract.View, MemberContract.View {

    InformationActivity informationActivity;

    private ImageView user_photo;
    private TextView Tx_save;
    private EditText name, age, gender;
    private String dogName, dogkind, dogage, doggender, dtoImage, dtoName, dtoKind, dtoAge, dtoGender, tempuri, ImageName , id;
    private RadioButton big, mid, small;
    private RadioGroup radioGroup;
    private List<DogDTO> Adto;

    ImageContract.Presenter presenter;
    MemberContract.Presenter Mpresenter;
    SharedPreferences sharedPreferences;
    int PICK_IMAGE_FROM_GALLERY = 10001;
    private static final int MY_PERMISSION_STORAGE = 1111;
    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "http://192.168.219.100:8092/upload.jsp";  //<<서버주소

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        informationActivity = (InformationActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        informationActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //프래그먼트 메인을 인플레이트해주고 컨테이너에 붙여달라는 뜻임
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_doginfo, container, false);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Bundle bundle = getArguments();
        id = bundle.getString("id");

        presenter = new ImagePresenter(this);
        Mpresenter = new MemberPresenter(this);
        dtoKind = "대형";

        //강아지 사진을 등록한 경우 자동 불러오기
        dogInfo(id);

        //앱 권한 관리
        checkPermission();

        //진저브레드에서 부터 추가된 일종의 개발툴로 개발자가 실수하는 것들을 감지하고 해결 할 수 있도록 돕는 모드, (실제로 수정하지는 않음 단지 알려줌)
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        name = rootView.findViewById(R.id.dog_id);
        radioGroup = rootView.findViewById(R.id.radioGroup);
        big = rootView.findViewById(R.id.radiobutton1);
        mid = rootView.findViewById(R.id.radiobutton2);
        small = rootView.findViewById(R.id.radiobutton3);
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);
        age = rootView.findViewById(R.id.dog_age);
        gender = rootView.findViewById(R.id.dog_gender);

        //이미지를 띄울 위젯 연결
        user_photo = rootView.findViewById(R.id.user_photo);
        user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropViewActivity.imageFile = null;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

            }
        });


        //업데이트 완료
        Tx_save = rootView.findViewById(R.id.Tx_save);
        Tx_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoFileUpload(serverURL, img_path);

                Toast.makeText(getContext().getApplicationContext(), "수정 완료 되었습니다.", Toast.LENGTH_SHORT).show();


                //강아지 정보 업데이트
                dogName = ((EditText) rootView.findViewById(R.id.dog_id)).getText().toString();
                dogage = ((EditText) rootView.findViewById(R.id.dog_age)).getText().toString();
                doggender = ((EditText) rootView.findViewById(R.id.dog_gender)).getText().toString();

                dogName = plzNoHacking(dogName);
                dogage = plzNoHacking(dogage);
                doggender = plzNoHacking(doggender);
                

                if (ImageName == null) {
                    ImageName = dtoImage;
                }
                Mpresenter.dogUpdate(id, dogName, dogkind, dogage, doggender, ImageName);

            }
        });
        return rootView;
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if (i == R.id.radiobutton1) {
                dogkind = "대형";
            } else if (i == R.id.radiobutton2) {
                dogkind = "중형";
            } else if (i == R.id.radiobutton3) {
                dogkind = "소형";
            }
        }
    };

    //앱 권한 요청
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(getContext())
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().remove(DogInfoFragment.this).commit();
                                fragmentManager.popBackStack();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri galleryPictureUri = data.getData();
            String uri = galleryPictureUri.toString();

            Intent intent = new Intent(getContext().getApplicationContext(), CropViewActivity.class);
            intent.putExtra("img", uri);
            startActivityForResult(intent, 1123);

        }
        if (requestCode == 1123 && resultCode == 1124) {
            tempuri = data.getStringExtra("contenturi");
            Picasso.get().load(tempuri).error(R.drawable.monglogo2).fit().centerCrop().into(user_photo);
            Bitmap bitmap = BitmapFactory.decodeFile(tempuri);
            user_photo.setImageBitmap(bitmap);
            img_path = getFilePathFromURI(getContext().getApplicationContext(), Uri.parse(tempuri));
        }

        if (requestCode == PICK_IMAGE_FROM_GALLERY
                && resultCode == Activity.RESULT_OK) {
            Uri galleryPictureUri = data.getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//end of onActivityResult()

    public String getFileName(Uri uri) {
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


        } catch (Exception e) {
            Log.d("Message", "exception " + e.getMessage());
        }
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
                for (DogDTO dog : Adto) {
                    dtoImage = dog.getDogImage();
                    Picasso.get().load("http://192.168.219.100:8092/upload/" + dtoImage).error(R.drawable.monglogo2).fit().centerCrop().into(user_photo);

                    dtoName = dog.getDogName();
                    name.setText(dtoName);

                    dtoKind = dog.getDogKind();
                    try {
                        if (dtoKind.equals("대형")) {
                            radioGroup.check(R.id.radiobutton1);
                        } else if (dtoKind.equals("중형")) {
                            radioGroup.check(R.id.radiobutton2);
                        } else if (dtoKind.equals("소형")) {
                            radioGroup.check(R.id.radiobutton3);
                        }
                    } catch (Exception e) {
                        radioGroup.check(R.id.radiobutton1);
                    }

                    dtoAge = dog.getDogAge();
                    age.setText(dtoAge);

                    dtoGender = dog.getDogGender();
                    gender.setText(dtoGender);
                }
            }

            @Override
            public void onFailure(Call<List<DogDTO>> call, Throwable t) {
                Log.e("###DogInfofail", t.getMessage());
            }
        });
    }

    @Override
    public void getMyImage(MemberDTO member) {

    }

    @Override
    public void showResult(String result) {

    }

    @Override
    public void goMainView() {

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