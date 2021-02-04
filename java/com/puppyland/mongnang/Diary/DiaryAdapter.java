package com.puppyland.mongnang.Diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.puppyland.mongnang.DTO.DiaryDTO;
import com.puppyland.mongnang.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DiaryAdapter extends BaseAdapter{
    private  ArrayList<DiaryDTO> mList = new ArrayList<DiaryDTO>();
    Context context;
    //아이템 데이터 추가를 위한 함수.



    public void addItem(String dno, String title, String userid, String create_date, String imgname , String content , String nickname , String font , int likey ,String ShareConfirmation){
        DiaryDTO item = new DiaryDTO();

        item.setDno(dno);
        item.setUserid(userid);
        item.setTitle(title);
        item.setCreate_date(create_date);
        item.setImg(imgname);
        item.setDcontent(content);
        item.setNickname(nickname);
        item.setFont(font);
        item.setLikey(likey);
        item.setShareConfirmation(ShareConfirmation);
        mList.add(item);

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public DiaryDTO getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context=parent.getContext();
        DiaryDTO diaryDTO = mList.get(position);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.diarylist_item , parent ,false);
        }

        TextView bno = convertView.findViewById(R.id.dno_listitem);
        TextView userid = convertView.findViewById(R.id.id_listitem);;
        TextView dtitle = convertView.findViewById(R.id.title_listitem);;
        TextView ddate = convertView.findViewById(R.id.date_listitem);;
        TextView share = convertView.findViewById(R.id.share);
        ImageView img = convertView.findViewById(R.id.img_listitem);

        bno.setText(diaryDTO.getDno());
        userid.setText(diaryDTO.getUserid());
        dtitle.setText(diaryDTO.getTitle());
        ddate.setText(diaryDTO.getCreate_date());
        share.setText(diaryDTO.getShareConfirmation());
        Picasso.get().load("http://192.168.219.100:8092/upload/" + diaryDTO.getImg()).error(R.drawable.monglogo2).fit().centerCrop().into(img);
        return convertView;
    }
}
