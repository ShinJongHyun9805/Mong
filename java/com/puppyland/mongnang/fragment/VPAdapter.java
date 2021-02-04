package com.puppyland.mongnang.fragment;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class VPAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> items;
    private ArrayList<String> itext = new ArrayList<String>();

    public VPAdapter(FragmentManager fm) {
        super(fm);
        items = new ArrayList<Fragment>();
        items.add(new Fragment2());
        items.add(new Fragment1());
        items.add(new Fragment3());

        itext.add("홈");
        itext.add("이거어때");
        itext.add("토독");
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return itext.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
