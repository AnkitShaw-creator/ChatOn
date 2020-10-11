package com.example.chaton;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment implements ChatsAdapter.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private View mView;
    private DatabaseReference mMessages;
    private RecyclerView mMessagesRecyclerView;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUsers;
    private ChatsAdapter mChatsAdapter;

    private final List<String> mUIDs = new ArrayList<>();

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);
        mMessagesRecyclerView = mView.findViewById(R.id.list_chats);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mMessages = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUser.getUid());
        mUsers = FirebaseDatabase.getInstance().getReference();
        mChatsAdapter = new ChatsAdapter(mUIDs, this);

        mMessagesRecyclerView.setHasFixedSize(true);
        mMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessagesRecyclerView.setAdapter(mChatsAdapter);

        populateUI();
        return mView;
    }

    private void populateUI() {
        mMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String UID = snapshot.getKey();
                Log.d("Chat Fragment", "onChildAdded: USER_ID " + UID);
                mUIDs.add(UID);
                mChatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
/**
    @Override
    public void onStart() {
        super.onStart();
        mMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String UID = snapshot.getKey();
                Log.d("Chat Fragment", "onChildAdded: USER_ID " + UID);
                mUIDs.add(UID);
                mChatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
 **/
    @Override
    public void onItemClick(int position) {
        String userId = mUIDs.get(position);
        Intent chatIntent = new Intent(getActivity().getApplicationContext(),ChatActivity.class);
        chatIntent.putExtra("USER_ID", userId);
        startActivity(chatIntent);
    }
}