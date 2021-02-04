package com.puppyland.mongnang.board;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.puppyland.mongnang.DTO.BoardDTO;
import com.puppyland.mongnang.R;


import java.util.ArrayList;

public class ListViewAdapter2 extends BaseAdapter {

    private TextView bno, title, userid, create_date, comment_count, views;
    String cnt = null;

    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<BoardDTO> listViewItemList = new ArrayList<BoardDTO>();

    //ListViewAdapter의 생성자
    public ListViewAdapter2() {

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
            convertView = inflater.inflate(R.layout.boardlistview_item, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        bno = (TextView) convertView.findViewById(R.id.text1);
        title = (TextView) convertView.findViewById(R.id.text2);
        userid = (TextView) convertView.findViewById(R.id.text3);
        create_date = (TextView) convertView.findViewById(R.id.text4);
        comment_count = (TextView) convertView.findViewById(R.id.comment_count);
        views = (TextView) convertView.findViewById(R.id.views);


        BoardDTO listViewItem = listViewItemList.get(position);

        bno.setText(listViewItem.getBno());
        title.setText(listViewItem.getTitle());
        userid.setText(listViewItem.getNickname());
        create_date.setText(listViewItem.getCreate_date());
        comment_count.setText(listViewItem.getCnt());
        views.setText("조회 " + listViewItem.getViews());

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
    public void addItem(String bno, String title, String userid, String create_date, String cnt, String nickname, int hit) {
        String views = String.valueOf(hit);

        BoardDTO item = new BoardDTO();

        item.setBno(bno);
        item.setTitle(title);
        item.setUserid(userid);
        item.setCreate_date(create_date);
        item.setCnt(cnt);
        item.setNickname(nickname);
        item.setViews(views);

        listViewItemList.add(item);
    }

}