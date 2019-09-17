package com.uncc.inclass02.ui.chatroom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.ride.SelectDriver;
import com.uncc.inclass02.utilities.Auth;
import com.uncc.inclass02.utilities.Driver;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class Chatroom extends AppCompatActivity {

    ChatroomPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    String chatroomId;
    DatabaseReference mRootRef;
    TextView badge;
    ImageView notification;
    String tripId, chatRoomTAG = "Chatroom";
    DatabaseReference mTripRef;
    ValueEventListener valueListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            chatroomId = b.getString(AppConstant.CHATROOM_ID);
            setTitle(b.getString(AppConstant.CHATROOM_NAME));
            mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.CHATROOM_DB_KEY).child(chatroomId).child(AppConstant.CURR_USERS);
            Log.d(chatRoomTAG, b.containsKey(AppConstant.TRIP_ID_RESULT) + " ");
            if (b.containsKey(AppConstant.TRIP_ID_RESULT)) {
                tripId = b.getString(AppConstant.TRIP_ID_RESULT);
            } else {
                tripId = AppConstant.WRONG_TRIP_ID;
            }
        }

        sectionsPagerAdapter = new ChatroomPagerAdapter(this, getSupportFragmentManager(), chatroomId);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        addToChatroom();

        mTripRef = FirebaseDatabase.getInstance().getReference(AppConstant.RIDE_DB_KEY).child(new Auth().getCurrentUserID());
    }

    private void addToChatroom() {
        String uid = new Auth().getCurrentUserID();
        mRootRef.child(uid).setValue(uid);
    }

    private void removeFromChatroom() {
        mRootRef.child(new Auth().getCurrentUserID()).removeValue();
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
                badge.setVisibility(View.INVISIBLE);
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

    public void setTripId(String id) {
        this.tripId = id;
        if (id != null) {
            setDriverListener();
        } else {
            mTripRef.removeEventListener(valueListener);
        }
    }

    private void setDriverListener() {
        mTripRef.child(this.tripId).child(AppConstant.CANDIDATE_DB_KEY);
        valueListener = mTripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Driver> drivers = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Driver driver = child.getValue(Driver.class);
                        drivers.add(driver);
                    }
                    if (drivers.size() > 0) {
                        Log.d(chatRoomTAG, drivers.size() + "");
                        badge.setText(drivers.size() + "");
                        badge.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goToSelectDriver() {
        Bundle b = new Bundle();
        b.putString(AppConstant.CHATROOM_ID, chatroomId);
        b.putString(AppConstant.TRIP_ID, tripId);
        startActivity(AppConstant.SELECT_DRIVER_CODE, SelectDriver.class, b);
    }

    private void startActivity(int code, Class<?> cls, Bundle b) {
        Intent i = new Intent(Chatroom.this, cls);
        i.putExtras(b); //Put your id to your next Intent
        startActivityForResult(i, code);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        tripId = AppConstant.WRONG_TRIP_ID;

        removeFromChatroom();
    }
}
