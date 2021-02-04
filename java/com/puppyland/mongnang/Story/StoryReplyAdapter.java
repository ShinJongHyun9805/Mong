package com.puppyland.mongnang.Story;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.puppyland.mongnang.DTO.StoryReplyDTO;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.SelectedUserDiaryActivity;

import java.util.ArrayList;

public class StoryReplyAdapter  extends BaseAdapter {

    private ArrayList<StoryReplyDTO> mList = new ArrayList<StoryReplyDTO>();
    TextView UserId , content ,ddate;

    public StoryReplyAdapter(){}

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        //listView_item Layout을 inflate하여 ConvertView 참조 획득

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.storylist_subitem, parent, false);


        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        UserId = (TextView) convertView.findViewById(R.id.subid);
        content = (TextView) convertView.findViewById(R.id.subContent);
        ddate= (TextView)convertView.findViewById(R.id.subDate);
      //  replyNickname = (TextView) convertView.findViewById(R.id.replyNickname);

        StoryReplyDTO  item  = mList.get(position);

        UserId.setText(item.getNickname());
        content.setText(item.getContent());
        ddate.setText(item.getDate());
     //   replyNickname.setText(item.getUserid());

        UserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SelectedUserDiaryActivity.class);
                intent.putExtra("selectedId" ,item.getUserid());
                intent.putExtra("selectedNickname",item.getNickname());
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    public void addItem(String dno,String userid, String content , String date , String nickanme){
        StoryReplyDTO item = new StoryReplyDTO();
        item.setDno(dno);
        item.setUserid(userid);
        item.setContent(content);
        item.setDate(date);
        item.setNickname(nickanme);
        mList.add(item);

    }


}
