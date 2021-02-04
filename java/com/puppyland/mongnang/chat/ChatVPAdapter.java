package com.puppyland.mongnang.chat;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.puppyland.mongnang.fragment.ChatAcceptUserLIstFragment;
import com.puppyland.mongnang.fragment.ChatRoomFragment;
import com.puppyland.mongnang.fragment.UserListFragment;

import java.util.ArrayList;

public class ChatVPAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> items;

    public ChatVPAdapter(FragmentManager fm) {
        super(fm);
        items = new ArrayList<Fragment>();

        items.add(new UserListFragment());
        items.add(new ChatRoomFragment());
        items.add(new ChatAcceptUserLIstFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

}
