package com.example.wanted.chatplus;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public final List<Fragment> mFragmentList = new ArrayList<>();
    public final List<String> mFragmentTitleList = new ArrayList<>();
    private Main_Page m;


    public void addFragment(Fragment fragment,String title)
    {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

   @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

   /* @Override
    public Fragment getItem(int position)
    {
        switch (position) {
            case 0:
                return new Chat_Fragment();
            case 1:
                return new Group_Fragment();
            case 2:
                return new Contact_Fragment();
            default:
                return null;
        }
    }
    */


    @Override
    public int getCount() {
        return mFragmentList.size();
    }


}
