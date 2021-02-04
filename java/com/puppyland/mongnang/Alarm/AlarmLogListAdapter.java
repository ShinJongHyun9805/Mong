package com.puppyland.mongnang.Alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.puppyland.mongnang.DTO.NotificationDTO;
import com.puppyland.mongnang.R;

import java.util.ArrayList;

public class AlarmLogListAdapter extends BaseAdapter {

    private TextView AlarmLogTime ,WhatAlarmLog;

    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<NotificationDTO> listViewItemList = new ArrayList<NotificationDTO>();

    public AlarmLogListAdapter() {

    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        //listView_item Layout을 inflate하여 ConvertView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.alarmloglistview_item, parent, false);
        }
        AlarmLogTime = convertView.findViewById(R.id.AlarmLogTime);
        WhatAlarmLog= convertView.findViewById(R.id.WhatAlarmLog);

        AlarmLogTime.setText(listViewItemList.get(position).getDate());
        if(listViewItemList.get(position).getItem().equals("게시판")){
            WhatAlarmLog.setText("게시글에 새로운 소식이 있습니다.");
        }else if(listViewItemList.get(position).getItem().equals("스토리")){
            WhatAlarmLog.setText("스토리에 새로운 소식이 있습니다.");
        }else if(listViewItemList.get(position).getItem().equals("비디오")){
            WhatAlarmLog.setText("동영상에 새로운 소식이 있습니다.");
        }

        return convertView;
    }

    //지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    //지정된 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    //아이템 데이터 추가를 위한 함수.
    public void addItem(NotificationDTO dto) {
        listViewItemList.add(dto);
    }
}
