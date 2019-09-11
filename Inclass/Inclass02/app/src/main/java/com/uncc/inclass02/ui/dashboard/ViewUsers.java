package com.uncc.inclass02.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.uncc.inclass02.utilities.UserProfile;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewUsers extends Fragment implements UserAsyncTask {

    RecyclerView recyclerView;
    List<UserProfile> userProfileList;
    UserListAdapter userListAdapter;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.USER_DB_KEY);

    public ViewUsers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ViewUsers.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewUsers newInstance() {
        ViewUsers fragment = new ViewUsers();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_users, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userProfileList = new ArrayList<>();
        userListAdapter = new UserListAdapter(userProfileList, this);

        RecyclerView recyclerView = getView().findViewById(R.id.userRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userListAdapter);

        initUserList();
    }

    private void initUserList() {

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    displayUserList(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayUserList(DataSnapshot dataSnapshot) {
        userProfileList.clear();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            UserProfile userProfile = child.getValue(UserProfile.class);
            userProfileList.add(userProfile);
        }
        userListAdapter.notifyDataSetChanged();
    }

    @Override
    public void viewDetails(UserProfile userProfile) {
        LayoutInflater li = LayoutInflater.from(this.getActivity());
        View promptsView = li.inflate(R.layout.user_profile, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("User Profile");
        alertDialogBuilder.setNegativeButton("CLOSE", null);
        alertDialogBuilder.setView(promptsView);

        TextView firstName = promptsView.findViewById(R.id.firstNameTV);
        firstName.setText(userProfile.getFirstName());
        TextView lastName = promptsView.findViewById(R.id.lastNameTV);
        lastName.setText(userProfile.getLastName());
        TextView email = promptsView.findViewById(R.id.emailTV);
        email.setText(userProfile.getEmail());
        TextView gender = promptsView.findViewById(R.id.genderTV);
        gender.setText(userProfile.getGender());
        TextView city = promptsView.findViewById(R.id.cityTV);
        city.setText(userProfile.getCity());


        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

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

    @Override
    public void renderPhoto(String link, final ImageView iv) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child(link);
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                GlideApp.with(getActivity().getApplicationContext())
                        .load(downloadUrl)
                        .into(iv);
            }




        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                GlideApp.with(getActivity().getApplicationContext())
                        .load("https://www.freeiconspng.com/uploads/no-image-icon-11.PNG")
                        .into(iv);
            }
        });
    }
}
