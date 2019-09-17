package com.uncc.inclass02.ui.chatroom;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatUsers extends Fragment implements ChatUserAsyncTask {

    ViewPager viewPager;
    RecyclerView recyclerView;
    ArrayList<String> userList;
    ChatUserListAdapter userListAdapter;
    DatabaseReference mRootRef;

    public ChatUsers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MyProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatUsers newInstance(String param1) {
        ChatUsers fragment = new ChatUsers();
        Bundle args = new Bundle();
        args.putString(AppConstant.CHATROOM_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String chatroomId = getArguments().getString(AppConstant.CHATROOM_ID);
            mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.CHATROOM_DB_KEY).child(chatroomId).child(AppConstant.CURR_USERS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_users, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userList = new ArrayList<>();
        userListAdapter = new ChatUserListAdapter(userList, this);

        RecyclerView recyclerView = getView().findViewById(R.id.chatUserRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userListAdapter);

        initMessageList();
    }

    private void initMessageList() {

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
        userList.clear();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            String userId = child.getValue(String.class);
            userList.add(userId);
        }
        userListAdapter.notifyDataSetChanged();
    }

    @Override
    public void renderDetails(final String userId, final TextView nameTV, final ImageView photo) {
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference(AppConstant.USER_DB_KEY);
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setUserInfo(dataSnapshot, userId, nameTV, photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUserInfo(DataSnapshot dataSnapshot, String userId, TextView nameTV, ImageView photo) {
        UserProfile userProfile = new UserProfile();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            if (child.getKey().equals(userId)) {
                userProfile = child.getValue(UserProfile.class);
                break;
            }
        }
        nameTV.setText(userProfile.getFirstName() + " " + userProfile.getLastName());
        renderPhoto(userProfile.getPhoto(), photo);
    }

    public void renderPhoto(String link, final ImageView iv) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child(link);
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
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
