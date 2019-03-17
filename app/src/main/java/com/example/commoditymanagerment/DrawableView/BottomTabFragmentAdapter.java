package com.example.commoditymanagerment.DrawableView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class BottomTabFragmentAdapter extends FragmentPagerAdapter {


    private FragmentManager fm ;
    private List<Fragment> fragmentList ;
    public BottomTabFragmentAdapter(FragmentManager fm , List<Fragment> fragments) {
        super(fm);
        this.fm = fm ;
        fragmentList = fragments ;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
