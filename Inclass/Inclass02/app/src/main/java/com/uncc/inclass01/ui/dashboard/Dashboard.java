package com.uncc.inclass01.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uncc.inclass01.AppConstant;
import com.uncc.inclass01.Login;
import com.uncc.inclass01.R;
import com.uncc.inclass01.utilities.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class Dashboard extends AppCompatActivity {

    DashboardPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.CHATROOM_DB_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sectionsPagerAdapter = new DashboardPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

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