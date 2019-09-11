package com.uncc.inclass02.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.Login;
import com.uncc.inclass02.R;
import com.uncc.inclass02.utilities.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class Dashboard extends AppCompatActivity {

    DashboardPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.CHATROOM_DB_KEY);
    private final int[] TAB_ICONS = new int[]{R.drawable.ic_chat, R.drawable.ic_edit_profile, R.drawable.ic_clock, R.drawable.ic_user};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sectionsPagerAdapter = new DashboardPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(TAB_ICONS[0]);
        tabs.getTabAt(1).setIcon(TAB_ICONS[1]);
        tabs.getTabAt(2).setIcon(TAB_ICONS[2]);
        tabs.getTabAt(3).setIcon(TAB_ICONS[3]);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));


        ImageView logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Auth().signOutUser();
                startActivity(new Intent(Dashboard.this, Login.class));
                finish();
            }
        });
    }

}