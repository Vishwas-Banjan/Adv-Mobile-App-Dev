package com.uncc.inclass01.ui.chatroom;

import android.content.Context;

import com.uncc.inclass01.AppConstant;
import com.uncc.inclass01.R;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ChatroomPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_chatroom, R.string.tab_chat_users};
    String chatroomId;

    public ChatroomPagerAdapter(Context context, FragmentManager fm, String chatroomId) {
        super(fm);
        mContext = context;
        this.chatroomId = chatroomId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return Chat.newInstance(chatroomId);
            case 1: return ChatUsers.newInstance(chatroomId);
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