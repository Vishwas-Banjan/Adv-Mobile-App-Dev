package com.uncc.inclass01.ui.chatroom;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.uncc.inclass01.AppConstant;
import com.uncc.inclass01.GlideApp;
import com.uncc.inclass01.R;
import com.uncc.inclass01.utilities.Auth;
import com.uncc.inclass01.utilities.Message;
import com.uncc.inclass01.utilities.UserProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chat extends Fragment implements MessageAsyncTask {

    ViewPager viewPager;
    RecyclerView recyclerView;
    ArrayList<Message> messageList;
    MessageListAdapter messageListAdapter;
    DatabaseReference mRootRef;
    DatabaseReference mUserRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.USER_DB_KEY);
    EditText messageET;

    public Chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ViewUsers.
     */
    // TODO: Rename and change types and number of parameters
    public static Chat newInstance(String param1) {
        Chat fragment = new Chat();
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
            mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.CHATROOM_DB_KEY).child(chatroomId).child(AppConstant.MESSAGES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatroom, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        messageList = new ArrayList<>();
        messageListAdapter = new MessageListAdapter(messageList, this);

        recyclerView = getView().findViewById(R.id.messageRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(messageListAdapter);

        initMessageList();

        messageET = getView().findViewById(R.id.messageET);
        Button send = getView().findViewById(R.id.sendButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mesg = messageET.getText().toString();
                if (!mesg.isEmpty()) {
                    sendMessage(mesg);
                }
            }
        });
    }

    private void sendMessage(String mesg) {
        Message message = new Message();
        message.setText(mesg);
        message.setUserId(new Auth().getCurrentUserID());
        message.setPostedAt(getCurrTime());
        mRootRef.push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                messageET.setText("");
            }
        });
    }

    private String getCurrTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstant.TIME_FORMAT);
        return dateFormat.format(new Date());
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
        messageList.clear();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            Message message = child.getValue(Message.class);
            message.setId(child.getKey());
            messageList.add(message);
        }
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        messageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteMessage(final int idx, String messageId) {
        mRootRef.child(messageId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // messageList.remove(idx);
                // messageListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void likeMessage(String messageId, String userId) {
        mRootRef.child(messageId).child(AppConstant.USER_LIKING).push().setValue(userId);
    }

    @Override
    public void unlikeMessage(int idx, String messageId, String key) {
        mRootRef.child(messageId).child(AppConstant.USER_LIKING).child(key).removeValue();
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
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                GlideApp.with(getActivity())
                        .load(downloadUrl)
                        .into(iv);
            }




        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                GlideApp.with(getActivity())
                        .load("https://www.freeiconspng.com/uploads/no-image-icon-11.PNG")
                        .into(iv);
            }
        });
    }
}
