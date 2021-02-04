package com.puppyland.mongnang.PushAlarm;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.puppyland.mongnang.AlarmLogActivity;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.MainActivity2;
import com.puppyland.mongnang.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.v("wefijssv" , token);
        SharedPreferences sharedPreferences = getSharedPreferences("DeviceIdFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("deviceId",token);
        editor.commit(); //앱을 설치하고 처음 실행할때만 토큰을 발급받음. 그걸 DeviceIdFile에 저장. 그러면 그 화면에서 회원가입을 안해도
        //앱을 지우지 않는 이상 계속 저장되어 있어서 회원가입할때 상관없음.
      //  sendRegistrationToServer(token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String surveyUrl = remoteMessage.getData().get("data");
        String no = remoteMessage.getData().get("where");
        // FCM서버로 부터 수신한 메시지중 data라는 키를 가진 값을 꺼내어 메소드로 파라미터로 넘겨준다
        sendNotification(surveyUrl , no);
    }

    /**
     * Android 핸드폰에 알림을 띄워주는 메소드
     *
     */
    private void sendNotification(String surveyUrl , String no) {
        Uri uri = Uri.parse(surveyUrl);
        Log.v("wweofjweof" , "푸시알람 반응있음");
        Intent intent;
        if(MainActivity2.start ==null){
            intent = new Intent(this, MainActivity2.class);

        }
        else{
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("pushflag" , "1");
        }
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        String channelId = getString(R.string.channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.mongappicon)
                        .setContentTitle("몽냥몽냥")
                        .setContentText(surveyUrl)// 내용바뀌는지 한번 테스트 해봐야함.
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel channel = new NotificationChannel(channelId, "jy", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * 디바이스의 전화번호를 받아오는 메소드
     * @return
     */
    private String getPhoneNumber() {
        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String phoneNumber = "";
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                return "";

            phoneNumber = telephony.getLine1Number();
            phoneNumber = phoneNumber.replace("+82","0");
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return phoneNumber;
    }

}
