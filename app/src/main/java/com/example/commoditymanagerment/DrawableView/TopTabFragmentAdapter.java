package com.example.commoditymanagerment.DrawableView;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class TopTabFragmentAdapter extends FragmentPagerAdapter {


    private FragmentManager fm ;
    private List<Fragment> fragmentList ;
    private List<String> titles ;

    public TopTabFragmentAdapter(FragmentManager fm ,List<Fragment> fragments , List<String> titles) {
        super(fm);
        this.fm = fm ;
        this.fragmentList = fragments ;
        this.titles = titles ;

    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
