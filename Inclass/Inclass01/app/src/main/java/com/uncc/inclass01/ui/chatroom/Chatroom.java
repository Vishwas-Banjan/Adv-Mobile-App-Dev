package com.uncc.inclass01.ui.chatroom;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uncc.inclass01.AppConstant;
import com.uncc.inclass01.R;
import com.uncc.inclass01.utilities.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class Chatroom extends AppCompatActivity {

    ChatroomPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    String chatroomId;
    DatabaseReference mRootRef;
    String key;

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
        key = mRootRef.push().getKey();
        mRootRef.child(key).setValue(new Auth().getCurrentUserID());
    }

    private void removeFromChatroom() {
        mRootRef.child(key).removeValue();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                removeFromChatroom();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
