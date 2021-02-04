package com.puppyland.mongnang.Alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.puppyland.mongnang.DTO.TimeCheckDTO;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    String memo=null;
    boolean flag = false;
    @Override
    public void onReceive(Context context, Intent intent) {

        //여기서 레트로핏 해서 값 메세지에 띄워지는지 부터 확인하고 진행
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");


        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남


            String channelName ="매일 알람 채널";
            String description = "매일 정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남


        if (notificationManager != null) {

            Date currentTime = Calendar.getInstance().getTime();
            android.icu.text.SimpleDateFormat dayFormat = new android.icu.text.SimpleDateFormat("dd", Locale.getDefault());
            android.icu.text.SimpleDateFormat monthFormat = new android.icu.text.SimpleDateFormat("MM", Locale.getDefault());
            android.icu.text.SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

            String year = yearFormat.format(currentTime);
            String month = monthFormat.format(currentTime);
            String day = dayFormat.format(currentTime);

            SharedPreferences sharedPreferences = context.getSharedPreferences("Loginfile",MODE_PRIVATE); // receiver 에선 context로 접근
            String loginId = sharedPreferences.getString("id",null); // 지금 로그인한 사람 id
            String resultDay = year+","+month+","+day;
            Log.v("wegsx" , resultDay);
            TimeCheckDTO dto = new TimeCheckDTO();
            dto.setUserId(loginId);
            dto.setMeetDate(resultDay);

            Gson gson = new Gson();
            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환`
            //오늘날짜로 누른거 있나없나 체크

            final Call<ResponseBody> getOneTimeCheck = NetRetrofit.getInstance().getService().getOneTimeCheck(objJson);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        memo= getOneTimeCheck.execute().body().string();
                        Log.v("wgxovob", memo);
                        flag = true;
                    } catch (IOException e) {
                        memo = "";
                    }
                }
            }).start();
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(flag == true){
                builder.setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())

                        .setTicker("{Time to watch some cool stuff!}")
                        .setContentTitle("오늘 기록된 일정이 있어요!")
                        .setContentText(String.valueOf(memo))
                        .setContentInfo("INFO")
                        .setContentIntent(pendingI);
                // 노티피케이션 동작시킴
                notificationManager.notify(1234, builder.build());
                flag = false;
            }
           // Calendar nextNotifyTime = Calendar.getInstance();
            // 내일 같은 시간으로 알람시간 결정
          //  nextNotifyTime.add(Calendar.DATE, 1);

            //  Preference에 설정한 값 저장
         //   SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
          //  editor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
          //  editor.apply();
        }
    }
}