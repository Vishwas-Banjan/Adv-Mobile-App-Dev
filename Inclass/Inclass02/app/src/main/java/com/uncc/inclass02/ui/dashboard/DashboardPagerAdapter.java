package com.uncc.inclass02.ui.dashboard;

import android.content.Context;

import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.ride.RideHistoryFragment;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class DashboardPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_chatrooms, R.string.tab_myprofile, R.string.trip_history, R.string.tab_users};

    public DashboardPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ChatroomList.newInstance();
            case 1:
                return MyProfile.newInstance(AppConstant.MY_PROFILE);
            case 2:
                return RideHistoryFragment.newInstance(1);
            case 3:
                return ViewUsers.newInstance();

        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return TAB_TITLES.length;
    }
}