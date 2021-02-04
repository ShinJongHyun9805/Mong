package com.puppyland.mongnang.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
public class MyPagerAdapter extends FragmentPagerAdapter {
    Context mContext;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        getItem(0);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPos = position % (3);
        View myView = new View(mContext);
        // View 속성 설정
        // 위의 realPos 기반으로 현재 뷰에서 보여주어야 하는 값을 세팅한다던가 하는 작업 필요
        ((ViewPager) container).addView(myView);
        return myView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View)object);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}
