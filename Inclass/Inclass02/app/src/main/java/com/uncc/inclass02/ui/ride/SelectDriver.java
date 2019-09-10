package com.uncc.inclass02.ui.ride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.GlideApp;
import com.uncc.inclass02.R;
import com.uncc.inclass02.utilities.Auth;
import com.uncc.inclass02.utilities.Driver;

import java.util.ArrayList;

public class SelectDriver extends AppCompatActivity implements SelectDriverAsyncTask {

    ViewPager viewPager;
    RecyclerView recyclerView;
    ArrayList<Driver> driverList;
    SelectDriverListAdapter driverListAdapter;
    DatabaseReference mRootRef;
    String chatroomId;
    String tripId;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_driver);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            chatroomId = b.getString(AppConstant.CHATROOM_ID);
            tripId = b.getString(AppConstant.TRIP_ID);
        }

        mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.RIDE_DB_KEY).child(new Auth().getCurrentUserID()).child(tripId).child(AppConstant.DRIVER_DB_KEY);

        driverList = new ArrayList<>();
        driverListAdapter = new SelectDriverListAdapter(driverList, this);

        RecyclerView recyclerView = findViewById(R.id.driverRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectDriver.this));
        recyclerView.setAdapter(driverListAdapter);

        initDriverList();

        message = findViewById(R.id.trip_mesg);
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

    private void initDriverList() {

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    displayMessageList(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayMessageList(DataSnapshot dataSnapshot) {
        driverList.clear();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            Driver driver = child.getValue(Driver.class);
            driverList.add(driver);
        }
        if (driverList.size() == 0) {
            String text = getResources().getString(R.string.no_driver);
            message.setText(text);
        } else {
            message.setVisibility(View.GONE);
        }
        driverListAdapter.notifyDataSetChanged();
    }

    @Override
    public void renderPhoto(String link, final ImageView iv) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child(link);
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                GlideApp.with(SelectDriver.this)
                        .load(downloadUrl)
                        .into(iv);
            }




        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                GlideApp.with(SelectDriver.this)
                        .load("https://www.freeiconspng.com/uploads/no-image-icon-11.PNG")
                        .into(iv);
            }
        });
    }
}
