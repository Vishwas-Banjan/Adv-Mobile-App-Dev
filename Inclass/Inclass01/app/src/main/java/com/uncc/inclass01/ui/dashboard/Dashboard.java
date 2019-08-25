package com.uncc.inclass01.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.uncc.inclass01.R;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Dashboard extends AppCompatActivity {

    DashboardPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;

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

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(Dashboard.this);
                View promptsView = li.inflate(R.layout.create_new, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Dashboard.this);
                alertDialogBuilder.setMessage("Add new chatroom");
                alertDialogBuilder.setPositiveButton("ADD", null);
                alertDialogBuilder.setNegativeButton("CANCEL", null);
                alertDialogBuilder.setView(promptsView);

                final EditText chatrommName = promptsView.findViewById(R.id.chatroomName);

                final AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!validateForm(chatrommName)) {
                                    return;
                                }
                                addChatroom(chatrommName.getText().toString());
                                dialog.dismiss();
                            }
                        });

                        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });

                alertDialog.show();
            }
        });
    }

    private boolean validateForm(EditText chatroomNameET) {
        boolean valid = true;
        String chatroomName = chatroomNameET.getText().toString();

        if (TextUtils.isEmpty(chatroomName)) {
            chatroomNameET.setError("Required.");
            valid = false;
        } else {
            chatroomNameET.setError(null);
        }
        return valid;
    }

    private void addChatroom(String name) {
//        Task task = new Task();
//        task.setName(name);
//        task.setDeadline(deadline);
//        task.setStatus(AppConstant.STATUS_TODO);
//        mRootRef.push().setValue(task);
    }

}