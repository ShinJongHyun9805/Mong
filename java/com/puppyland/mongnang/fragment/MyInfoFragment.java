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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.kakao.auth.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.puppyland.mongnang.CropViewActivity;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.InformationActivity;
import com.puppyland.mongnang.InsertAddressActivity;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.contract.ImageContract;
import com.puppyland.mongnang.contract.MemberContract;
import com.puppyland.mongnang.presenter.ImagePresenter;
import com.puppyland.mongnang.presenter.MemberPresenter;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MyInfoFragment extends Fragment implements ImageContract.View, MemberContract.View {

    InformationActivity informationActivity;

    private ImageContract.Presenter presenter;
    private MemberContract.Presenter mpresenter;

    FButton fbtn_address;
    private String ImageName, fileName, getnickname;
    private ImageView user_photo;
    private EditText user_id, user_age, user_sex, user_nickname, user_msg;
    private TextView Tx_save, Tx_logout, Tx_goodbye;
    private SharedPreferences sharedPreferences;
    private String tempuri , id;
    SharedPreferences sharedPreferences2;
    //중복확인을 했는지 안했는지 체크
    private int overlap_check = 0;
    //닉네임을 사용할 수 있는지 없는지 체크
    private int nickname_check;

    private LinearLayout LL_check;

    //사진
    private static final int MY_PERMISSION_STORAGE = 1111;
    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "http://192.168.219.100:8092/upload.jsp";  //<<서버주소

    //파이어스토어 users 삭제
    private FirebaseFirestore db;


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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_myinfo, container, false);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Bundle bundle = getArguments();
        id = bundle.getString("id");

        sharedPreferences2 = getContext().getSharedPreferences("nickname", MODE_PRIVATE);

        presenter = new ImagePresenter(this);
        mpresenter = new MemberPresenter(this);

        //파이어스토어 users 삭제
        db = FirebaseFirestore.getInstance();


        //회원의 등록된 정보 가져오기
        getmyImgName(id);

        //유저 아이디
        user_id = rootView.findViewById(R.id.user_id);
        user_id.setText(id);
        user_id.setEnabled(false);

        //유저 나이
        user_age = rootView.findViewById(R.id.user_age);

        //유저 성별
        user_sex = rootView.findViewById(R.id.user_sex);

        //유저 닉네임
        user_nickname = rootView.findViewById(R.id.user_nickname);

        //유저 상태메세지
        user_msg = (EditText) rootView.findViewById(R.id.user_msg);

        //중복확인 눌렀는지 체크
        LL_check = rootView.findViewById(R.id.LL_check);

        fbtn_address = rootView.findViewById(R.id.fbtn_address);
        fbtn_address.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        fbtn_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InsertAddressActivity.class);
                startActivity(intent);
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
                // 이미지 변경
                if (ImageName != null) {
                    presenter.image(ImageName, id);
                    Toast.makeText(getContext().getApplicationContext(), "수정 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                }
                MemberDTO updateinfoMember = new MemberDTO();
                String nickname = user_nickname.getText().toString();
                String age = user_age.getText().toString();
                String gender = user_sex.getText().toString();

                nickname = plzNoHacking(nickname);
                age = plzNoHacking(age);
                gender = plzNoHacking(gender);

                if ((nickname == null || nickname.getBytes().length == 0) || (age == null || age.getBytes().length == 0) || (gender == null || gender.getBytes().length == 0)) {
                    Toast.makeText(getContext().getApplicationContext(), "공백을 채워주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //닉네임 변경 시
                    if (!getnickname.equals(nickname) || overlap_check == 0) {
                        Toast.makeText(getContext().getApplicationContext(), "중복 확인을 눌러주세요", Toast.LENGTH_SHORT).show();
                        //상태 메세지만 변경
                    }
                    if (getnickname.equals(nickname) || (overlap_check == 1 && nickname_check == 1)) {
                        updateinfoMember.setUserId(id);
                        updateinfoMember.setNickname(nickname);
                        updateinfoMember.setGender(gender);
                        updateinfoMember.setAge(age);
                        updateinfoMember.setUsermsg(user_msg.getText().toString());

                        //TODO : 파이어스토어에 별명 수정
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DocumentReference washingtonRef = db.collection("users").document(user.getUid());
                        washingtonRef
                                .update("usernm", nickname)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

                        presenter.userInfoUpdate(updateinfoMember);

                        SharedPreferences.Editor editorr = sharedPreferences2.edit();
                        editorr.putString("nickname", nickname);// 변경했을때 프리팹에 다시 넣어줌
                        editorr.commit();

                        Toast.makeText(getContext().getApplicationContext(), "수정 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //닉네임 중복확인
        LL_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = user_nickname.getText().toString();

                if (nickname.equals("") || nickname.getBytes().length == 0) {
                    Toast.makeText(getContext(), "공백을 채워주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Checkingoverlap(nickname);
                }
            }
        });


        //로그아웃
        /*
        Tx_logout = rootView.findViewById(R.id.Tx_logout);
        Tx_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences auto = getContext().getSharedPreferences("Loginfile", MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                editor.clear();
                editor.commit();


                SharedPreferences sharedPreferences3 = getContext().getSharedPreferences("ChatuserListfile", MODE_PRIVATE);
                SharedPreferences.Editor editor23 = sharedPreferences3.edit();
                editor23.clear();
                editor23.commit(); // 로컬에 id 저장하는것.

                SharedPreferences sharedPreferences4 = getContext().getSharedPreferences("ChatuserListfileNoAccept", MODE_PRIVATE);
                SharedPreferences.Editor editor24 = sharedPreferences4.edit();
                editor24.clear();
                editor24.commit(); // 로컬에 id 저장하는것.


                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.clear();
                editor2.commit();

                if(UserManagement.getInstance() !=null){ // 카카오 로그인을 했던거면 이게 동작 아니면 그냥 원래 로그아웃대로
                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                }

                Toast.makeText(getContext(), "안녕히 가세요:)", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(MyInfoFragment.this).commit();
                fragmentManager.popBackStack();
            }
        });*/

        //회원 탈퇴
        Tx_goodbye = rootView.findViewById(R.id.Tx_goodbye);
        Tx_goodbye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //파이어 베이스 사용자 재인증 -> 보안에 관한 작업은 최근에 로그인한 적이 있어야함.
                show();

            }
        });
        return rootView;
    }

    //탈퇴를 위한 재 인증
    private void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.recertification, null);
        builder.setView(view);
        final Button submit = (Button) view.findViewById(R.id.buttonSubmit);
        final EditText email = (EditText) view.findViewById(R.id.edittextEmailAddress);
        email.setText(id);
        email.setEnabled(false);
        final EditText password = (EditText) view.findViewById(R.id.edittextPassword);

        final AlertDialog dialog = builder.create();
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String strEmail = email.getText().toString();
                String strPassword = password.getText().toString();

                /**
                 * 인증
                 * */
                AuthCredential credential = EmailAuthProvider.getCredential(strEmail, strPassword);

                /**
                 * 파이어스토어 rooms 삭제
                 * */
                db.collection("rooms").whereEqualTo("uid", myUid)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("###", document.getId());

                                        db.collection("rooms").document(document.getId())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("###", "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("###", e.getMessage());
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                /**
                 * 파이어스토어 users 삭제
                 * */
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    db.collection("users").document(myUid)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("###", "DocumentSnapshot successfully deleted!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("###", e.getMessage());
                                                }
                                            });


                                    /**
                                     * 탈퇴
                                     * */
                                    //디바이스 아이디 삭제 -> 강아지 테이블 정보 삭제 -> 멤버쉽 삭제 -> 채팅리스트 유저 정보 삭제 -> 회원 탈퇴
                                    mpresenter.DeviceIdDelete(id);
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getActivity(), "잘 가 :<", Toast.LENGTH_LONG).show();

                                                        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                                                        editor2.clear();
                                                        editor2.commit();

                                                        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() { // 카카오 언링크
                                                            @Override
                                                            public void onFailure(ErrorResult errorResult) {
                                                                int result = errorResult.getErrorCode();
                                                                Log.v("eieieikek", "카톡탈퇴함1");
                                                                if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                                                                    Toast.makeText(getContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(getContext(), "회원탈퇴에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onSessionClosed(ErrorResult errorResult) {
                                                                Log.v("eieieikek", "카톡탈퇴함2");
                                                                Toast.makeText(getContext(), "로그인 세션이 닫혔습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                                startActivity(intent);
                                                            }

                                                            @Override
                                                            public void onNotSignedUp() {
                                                                Log.v("eieieikek", "카톡탈퇴함3");
                                                                Toast.makeText(getContext(), "가입되지 않은 계정입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                                startActivity(intent);
                                                            }

                                                            @Override
                                                            public void onSuccess(Long result) {
                                                                Log.v("eieieikek", "카톡탈퇴함4");
                                                               Toast.makeText(getContext(), "회원탈퇴에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });

                                                        dialog.dismiss();

                                                    //    startActivity(new Intent(getContext(), MainActivity.class));
                                                    } else {
                                                        Toast.makeText(getContext(), "다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(getContext(), "다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        dialog.show();
    }

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
                                fragmentManager.beginTransaction().remove(MyInfoFragment.this).commit();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        return fileName;
    }

    public String getFilePathFromURI(Context context, Uri contentUri) {
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

    public String passwordEncryption(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");// 비밀번호 암호화
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            String retVal = sb.toString();
            return retVal;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    //내 정보 가져오기
    public void getmyImgName(String userID) {
        MemberDTO dto = new MemberDTO();
        dto.setUserId(userID);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        final Call<MemberDTO> getMyInfo = NetRetrofit.getInstance().getService().getMyInfo(objJson);
        getMyInfo.enqueue(new Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                if (response.isSuccessful()) {
                    MemberDTO dto = response.body();

                    fileName = dto.getMemberimage();
                    Picasso.get().load("http://192.168.219.100:8092/upload/" + fileName).error(R.color.color_ffffffff).fit().centerCrop().into(user_photo);

                    user_age.setText(dto.getAge());
                    user_sex.setText(dto.getGender());
                    user_nickname.setText(dto.getNickname());
                    getnickname = dto.getNickname();
                    try {
                        user_msg.setText(dto.getUsermsg());
                    } catch (Exception e) {
                        user_msg.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {
                Log.d("내 사진 가져오기 실패", t.getMessage());
            }
        });
    }

    //닉네임 중복확인
    public void Checkingoverlap(String nickname) {
        MemberDTO dto = new MemberDTO();
        dto.setNickname(nickname);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<ResponseBody> Checkingoverlap = NetRetrofit.getInstance().getService().Checkingoverlap(objJson);
        Checkingoverlap.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        nickname_check = Integer.parseInt(response.body().string());
                        if (nickname_check == 1) {
                            Toast.makeText(getContext(), "사용가능한 별명입니다 :)", Toast.LENGTH_SHORT).show();
                            overlap_check = 1;
                        } else {
                            Toast.makeText(getContext(), "사용 중인 별명입니다 :(", Toast.LENGTH_SHORT).show();
                            nickname_check = 0;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
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