package com.uncc.inclass02.ui.chatroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.ride.RequestRide;
import com.uncc.inclass02.ui.ride.SelectDriver;
import com.uncc.inclass02.utilities.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;

public class Chatroom extends AppCompatActivity {

    ChatroomPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    String chatroomId;
    DatabaseReference mRootRef;
    String key;
    TextView badge;
    ImageView notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            chatroomId = b.getString(AppConstant.CHATROOM_ID);
            setTitle(b.getString(AppConstant.CHATROOM_NAME));
            mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.CHATROOM_DB_KEY).child(chatroomId).child(AppConstant.CURR_USERS);
        }

        sectionsPagerAdapter = new ChatroomPagerAdapter(this, getSupportFragmentManager(), chatroomId);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        addToChatroom();
    }

    private void addToChatroom() {
        String uid = new Auth().getCurrentUserID();
        mRootRef.child(uid).setValue(uid);
    }

    private void removeFromChatroom() {
        mRootRef.child(key).removeValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatroom, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = menuItem.getActionView();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        badge = actionView.findViewById(R.id.notif_badge);
        badge.setVisibility(View.INVISIBLE);

        notification = actionView.findViewById(R.id.notif);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSelectDriver();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToSelectDriver() {
        startActivity(AppConstant.SELECT_DRIVER_CODE, SelectDriver.class);
    }

    private void startActivity(int code, Class<?> cls) {
        Intent i = new Intent(Chatroom.this, cls);
        startActivityForResult(i, code);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeFromChatroom();
    }
}
