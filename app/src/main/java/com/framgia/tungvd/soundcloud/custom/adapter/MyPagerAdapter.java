package com.framgia.tungvd.soundcloud.custom.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragments;
    private ArrayList<String> mNames;

    public MyPagerAdapter(FragmentManager fm,
                          ArrayList<Fragment> fragments,
                          ArrayList<String> names) {
        super(fm);
        mFragments = fragments;
        mNames = names;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mNames.get(position);
    }
}
