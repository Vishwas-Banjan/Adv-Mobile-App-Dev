package com.uncc.inclass01.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uncc.inclass01.AppConstant;
import com.uncc.inclass01.R;
import com.uncc.inclass01.ui.chatroom.Chatroom;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatroomList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatroomList extends Fragment implements ChatroomAsyncTask {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    RecyclerView recyclerView;
    ArrayList<String> chatroomList;
    ChatroomListAdapter chatroomListAdapter;

    public ChatroomList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChatroomList.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatroomList newInstance(String param1) {
        ChatroomList fragment = new ChatroomList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
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
        initList();

        RecyclerView recyclerView = getView().findViewById(R.id.chatRoomRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(chatroomListAdapter);


    }

    private void startActivity(int code, Class<?> cls) {
        Intent i = new Intent(getActivity(), cls);
        startActivityForResult(i, code);
    }

    private void initList() {
        chatroomList.add("Chatroom 1");
    }


    @Override
    public void goToChatroom(int id) {
        startActivity(AppConstant.CHATROOM_CODE, Chatroom.class);
    }
}
