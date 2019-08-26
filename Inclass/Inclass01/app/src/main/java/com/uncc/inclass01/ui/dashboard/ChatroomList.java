package com.uncc.inclass01.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uncc.inclass01.AppConstant;
import com.uncc.inclass01.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatroomList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatroomList extends Fragment implements ChatroomAsyncTask {

    RecyclerView recyclerView;
    ArrayList<Chatroom> chatroomList;
    ChatroomListAdapter chatroomListAdapter;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(AppConstant.CHATROOM_DB_KEY);

    public ChatroomList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatroomList.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatroomList newInstance() {
        ChatroomList fragment = new ChatroomList();
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
        return inflater.inflate(R.layout.fragment_chatroom_list, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chatroomList = new ArrayList<>();
        chatroomListAdapter = new ChatroomListAdapter(chatroomList, this);

        RecyclerView recyclerView = getView().findViewById(R.id.userRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(chatroomListAdapter);

        initChatroomList();
    }

    private void initChatroomList() {

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    displayChatroomList(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayChatroomList(DataSnapshot dataSnapshot) {
        chatroomList.clear();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            Chatroom chatroom = child.getValue(Chatroom.class);
            chatroom.setId(child.getKey());
            chatroomList.add(chatroom);
        }
        chatroomListAdapter.notifyDataSetChanged();
    }

    private void startActivity(int code, Class<?> cls, Chatroom chatroom) {
        Intent i = new Intent(getActivity(), cls);
        Bundle b = new Bundle();
        b.putString(AppConstant.CHATROOM_ID, chatroom.getId());
        b.putString(AppConstant.CHATROOM_NAME, chatroom.getName());
        i.putExtras(b); //Put your id to your next Intent
        startActivityForResult(i, code);
    }


    @Override
    public void goToChatroom(Chatroom chatroom) {
        startActivity(AppConstant.CHATROOM_CODE, com.uncc.inclass01.ui.chatroom.Chatroom.class, chatroom);
    }
}
