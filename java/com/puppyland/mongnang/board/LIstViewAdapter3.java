package com.puppyland.mongnang.board;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.puppyland.mongnang.DTO.CommentDTO;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.SelectedUserDiaryActivity;

import java.util.ArrayList;

public class LIstViewAdapter3 extends BaseAdapter {

    private TextView id, content ,commentid;

    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<CommentDTO> listViewItemList = new ArrayList<CommentDTO>();

    //ListViewAdapter의 생성자
    public LIstViewAdapter3(){}

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
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
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.commentlistview_item, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        id = (TextView) convertView.findViewById(R.id.commenttext1);
        content = (TextView) convertView.findViewById(R.id.commenttext2);
        commentid = (TextView) convertView.findViewById(R.id.commenttext0);

        CommentDTO listViewItem = listViewItemList.get(position);

        id.setText(listViewItem.getNickname());
        content.setText(listViewItem.getContent());
        commentid.setText(listViewItem.getId());

        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SelectedUserDiaryActivity.class);
                intent.putExtra("selectedId" ,listViewItem.getId());
                intent.putExtra("selectedNickname",listViewItem.getNickname());
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    //아이템 데이터 추가를 위한 함수.
    public void addItem(String id, String content , String nickname){
        CommentDTO item = new CommentDTO();

        item.setId(id);
        item.setContent(content);
        item.setNickname(nickname);

        listViewItemList.add(item);

    }
}
