package com.puppyland.mongnang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.puppyland.mongnang.Alarm.AlarmReceiver;
import com.puppyland.mongnang.Alarm.DeviceBootReceiver;
import com.puppyland.mongnang.DTO.TimeCheckDTO;
import com.puppyland.mongnang.contract.TimeCheckContract;
import com.puppyland.mongnang.presenter.TimeCheckPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity implements  TimeCheckContract.view{

    private TextView Tx_Schedulesave;
    //달력 관련
    MaterialCalendarView materialCalendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    //일정관리하는 Alert Dialogs
    MaterialAlertDialogBuilder builder;
    TextInputEditText name;
    TextInputEditText location;
    TextInputEditText time;
    boolean flag = false;
    boolean isFlag = false;
    TimeCheckContract.presenter timePresenter;
    String shot_Day;
    ArrayList<String> result;
    List<String> result2;
    View view;
    ViewGroup viewGroup;

    Calendar calendar;
    int Year;
    String Month;
    String Day;
    public MaterialAlertDialogBuilder getBuilder() {
        return this.builder;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_schedule);
        SharedPreferences sharedPreferences3 = getSharedPreferences("Loginfile", MODE_PRIVATE);
        final String loginId = sharedPreferences3.getString("id", null);
        CoordinatorLayout coordinatorLayout = findViewById(R.id.myCoordinatorLayout);
        view = findViewById(R.id.rootid);
        //MainAcitivity2에서 넘오는 false값의 Flag로 최초 실행 확인
        SharedPreferences sharedPreferences = getSharedPreferences("weatherFlag", MODE_PRIVATE);
        flag = sharedPreferences.getBoolean("Flag", false);
        timePresenter = new TimeCheckPresenter(this);
        builder = new MaterialAlertDialogBuilder(ScheduleActivity.this);
        name = new TextInputEditText(ScheduleActivity.this);
        location = new TextInputEditText(ScheduleActivity.this);
        time = new TextInputEditText(ScheduleActivity.this);


        Tx_Schedulesave = findViewById(R.id.Tx_Schedulesave);
        Tx_Schedulesave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //캘린더
        materialCalendarView = findViewById(R.id.calendarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(oneDayDecorator);
        materialCalendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader);
        result = new ArrayList<String>();
        result2 = new ArrayList<String>();


        TimeCheckDTO dto = new TimeCheckDTO();
        dto.setUserId(loginId);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<List<String>> getTime  = NetRetrofit.getInstance().getService().getTimeCheck(objJson);
        getTime.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                for(int i=0;i<response.body().size();i++) {
                    Log.v("timechedule", response.body().get(i));
                    result.add(response.body().get(i));
                }
                new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });


        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                isFlag = false;
                Year = date.getYear();
                Month = String.valueOf(date.getMonth() + 1);
                Day = String.valueOf(date.getDay());

                Log.i("Year test", Year + "");
                Log.i("Month test", Month + "");
                Log.i("Day test", Day + "");
                if (Month.length() == 1) {
                    Month = "0" + Month;
                }
                if (Day.length() == 1) {
                    Day = "0" + Day;
                }
                shot_Day = Year + "," + Month + "," + Day;

                Log.v("ttestt", loginId + " " + shot_Day);

                String cmp1;
                String cmp2;
                for (int i = 0; i < result.size(); i++) {
                    cmp1 = result.get(i);
                    cmp2 = shot_Day;
                    if (cmp1.equals(cmp2)) {
                        Log.v("same?a" ,"일치하는게 있다.");
                        isFlag = true;
                    }
                }

                if (isFlag) {
                    TimeCheckDTO dto = new TimeCheckDTO();
                    dto.setUserId(loginId);
                    dto.setMeetDate(shot_Day);

                    Gson gson = new Gson();
                    String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환`

                    Call<ResponseBody> getOneTimeCheck = NetRetrofit.getInstance().getService().getOneTimeCheck(objJson);
                    getOneTimeCheck.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.body() != null) {
                                    name.setText(response.body().string());
                                } else {
                                    Log.v("wgexgg22", "널임");
                                    isFlag = true;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                        }
                    });
                    isFlag = true;
                }

                Log.i("shot_Day test", shot_Day + "");

                Snackbar.make(coordinatorLayout, shot_Day + "일정을 확인하시겠습니까?", Snackbar.LENGTH_LONG).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // 확인을 누르면 해당 일정의 데이터가 DB에 저장 5개까지만 저장하는걸로 하자 테이블 하나 새로 만들어야할듯
                        //다이얼로그 화면

                        TimeCheckDTO dto = new TimeCheckDTO();
                        dto.setUserId(loginId);
                        dto.setMeetDate(shot_Day);

                        Gson gson = new Gson();
                        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환`

                        Call<ResponseBody> getOneTimeCheck = NetRetrofit.getInstance().getService().getOneTimeCheck(objJson);
                        getOneTimeCheck.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                builder.setTitle("Memo");
                                builder.setMessage("일정을 메모해보세요.");
                                if (name.getParent() != null) {
                                    ((ViewGroup) name.getParent()).removeView(name);
                                }
                                try {
                                    if (response.body() != null) {
                                        name.setText(response.body().string());
                                        builder.setView(name);
                                    } else {

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();

                                }
                                builder.setIcon(R.drawable.dog2);
                                builder.setBackground(getResources().getDrawable(R.drawable.dialog_bg)); // 입력 수정 둘다 함
                                builder.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String memo = name.getText().toString();
                                        Log.v("loginIddddd", String.valueOf(loginId));
                                        Log.v("shot_Daylll", String.valueOf(shot_Day));
                                        Log.v("memoooo", String.valueOf(memo));
                                        if(isFlag == false){ // 이 날짜에 메모가 없었으면
                                            timePresenter.Timeinsert(loginId, shot_Day, memo);
                                            result.add(shot_Day);//이거 안해주면 앱새로 껐다가 안키면 새로 추가한 날짜 눌러볼때 오류뜸.



                                            //일정 알람 설정  이렇게 하면 아마 앱켤때마다 설정 초기화되서 다음날에 시간 지정이 안될꺼 같음
                                            //알람 설정 버튼을 누를때 아래 코드가 실행되게 하면 버튼 눌렀을때만 아래 객체가 만들어지니까 그렇게 하면 될듯
                                            calendar = Calendar.getInstance();
                                            calendar.setTimeInMillis(System.currentTimeMillis()); //현재시간 즉 그러니까 앱을 켰을때 main2가 나왔을때의 시간
                                            calendar.set(Calendar.YEAR,Year);
                                            calendar.set(Calendar.MONTH,Integer.parseInt(Month));
                                            calendar.set(Calendar.DATE,Integer.parseInt(Day));
                                            calendar.set(Calendar.HOUR_OF_DAY, 17); //10시를 알림 보내는 시간으로 고정
                                            calendar.set(Calendar.MINUTE, 15);
                                            calendar.set(Calendar.SECOND, 0);

                                            Date currentTime = Calendar.getInstance().getTime();
                                            android.icu.text.SimpleDateFormat dayFormat = new android.icu.text.SimpleDateFormat("dd", Locale.getDefault());
                                            android.icu.text.SimpleDateFormat monthFormat = new android.icu.text.SimpleDateFormat("MM", Locale.getDefault());
                                            android.icu.text.SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

                                            String year = yearFormat.format(currentTime);
                                            String month = monthFormat.format(currentTime);
                                            String day = dayFormat.format(currentTime);
                                            String resultDay = year + "," + month + "," + day;
                                            Log.v("wegsx", resultDay);
                                            // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                                            //    if (calendar.before(Calendar.getInstance())) {
                                            //        calendar.add(Calendar.DATE, 1);
                                            //     } // 이렇게 해주면 시간 객체가 새로 생성되도 다음날로 알아서 지정해주게 된다. 그럼 클릭해서 다시 이 oncreate가 시작되도
                                            // 9시에 보내는걸로 고정해놨기 때문에 푸시알람을 클릭한다고 해도 그냥 다음날 알람이 설정되어 아래로 갈뿐 또 알람이 발생하진 않는다.
                                            diaryNotification(calendar);

                                        }

                                        if (isFlag == true) { // 이 날짜에 메모가 있었으면
                                            timePresenter.TimeUpdate(loginId, shot_Day, memo);
                                            //일정 알람 설정  이렇게 하면 아마 앱켤때마다 설정 초기화되서 다음날에 시간 지정이 안될꺼 같음
                                            //알람 설정 버튼을 누를때 아래 코드가 실행되게 하면 버튼 눌렀을때만 아래 객체가 만들어지니까 그렇게 하면 될듯
                                            calendar = Calendar.getInstance();
                                            calendar.setTimeInMillis(System.currentTimeMillis()); //현재시간 즉 그러니까 앱을 켰을때 main2가 나왔을때의 시간
                                            calendar.set(Calendar.YEAR,Year);
                                            calendar.set(Calendar.MONTH,Integer.parseInt(Month));
                                            calendar.set(Calendar.DATE,Integer.parseInt(Day));
                                            calendar.set(Calendar.HOUR_OF_DAY, 17); //10시를 알림 보내는 시간으로 고정
                                            calendar.set(Calendar.MINUTE, 20);
                                            calendar.set(Calendar.SECOND, 0);

                                            Date currentTime = Calendar.getInstance().getTime();
                                            android.icu.text.SimpleDateFormat dayFormat = new android.icu.text.SimpleDateFormat("dd", Locale.getDefault());
                                            android.icu.text.SimpleDateFormat monthFormat = new android.icu.text.SimpleDateFormat("MM", Locale.getDefault());
                                            android.icu.text.SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

                                            String year = yearFormat.format(currentTime);
                                            String month = monthFormat.format(currentTime);
                                            String day = dayFormat.format(currentTime);
                                            String resultDay = year + "," + month + "," + day;
                                            Log.v("wegsx", resultDay);
                                            // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                                            //    if (calendar.before(Calendar.getInstance())) {
                                            //        calendar.add(Calendar.DATE, 1);
                                            //     } // 이렇게 해주면 시간 객체가 새로 생성되도 다음날로 알아서 지정해주게 된다. 그럼 클릭해서 다시 이 oncreate가 시작되도
                                            // 9시에 보내는걸로 고정해놨기 때문에 푸시알람을 클릭한다고 해도 그냥 다음날 알람이 설정되어 아래로 갈뿐 또 알람이 발생하진 않는다.
                                            diaryNotification(calendar);
                                        }
                                        //이건 새로 추가되고 난뒤에 새로 일정표기 최신화 해주려고 한거임
                                        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());
                                    }
                                });
                                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                }).show();
            }
        });
    }

    @Override
    public void UpdateCalendar(List<TimeCheckDTO> list) {

    }

    //테스트중
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        ArrayList<String> Time_Result;


        ApiSimulator(ArrayList<String> Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for (int i = 0; i < Time_Result.size(); i++) {

                String[] time = Time_Result.get(i).split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);
                calendar.set(year, month - 1, dayy);
                CalendarDay day = CalendarDay.from(calendar);
                Log.v("timechedule", String.valueOf(year)+String.valueOf(month)+String.valueOf(year));
                dates.add(day);

            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);


            materialCalendarView.addDecorator(new EventDecorator(Color.BLACK, calendarDays, ScheduleActivity.this));
        }
    }

    void diaryNotification(Calendar calendar) {
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {
            if (alarmManager != null) {

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

}