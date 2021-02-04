package com.puppyland.mongnang.hospital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.puppyland.mongnang.DTO.HospitalDTO;
import com.puppyland.mongnang.R;

import java.util.ArrayList;

public class ListViewAdapterHospital extends BaseAdapter {

    private TextView Tx_name, Tx_number, Tx_realaddress, Tx_countreviews;

    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<HospitalDTO> listViewItemList = new ArrayList<HospitalDTO>();

    //ListViewAdapter의 생성자
    public ListViewAdapterHospital() {

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
            convertView = inflater.inflate(R.layout.hospitallistview_item, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        Tx_name = convertView.findViewById(R.id.Tx_name);
        Tx_number = convertView.findViewById(R.id.Tx_number);
        Tx_realaddress = convertView.findViewById(R.id.Tx_realaddress);
        Tx_countreviews = convertView.findViewById(R.id.Tx_countreviews);

        HospitalDTO listViewItem = listViewItemList.get(position);

        Tx_name.setText(listViewItem.getName());
        Tx_number.setText(listViewItem.getNumber());
        Tx_realaddress.setText(listViewItem.getRealaddress());
        Tx_countreviews.setText(listViewItem.getReviews());

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
    public void addItem(String address1, String address2, String name, String resladdress, String number, String reviews) {

        HospitalDTO item = new HospitalDTO();

        item.setAddress1(address1);
        item.setAddress2(address2);
        item.setName(name);
        item.setRealaddress(resladdress);
        item.setNumber(number);
        item.setReviews(reviews);

        listViewItemList.add(item);
    }

}